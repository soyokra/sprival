"""
执行器

按计划执行步骤，调用工具，处理结果
"""

import uuid
from typing import Dict, Any, Optional
from datetime import datetime

from ..models.plan import Plan, Step, StepStatus
from ..models.execution_result import ExecutionResult, StepResult, ExecutionStatus
from ..models.context import ExecutionContext
from ..tools.executor import ToolExecutor
from ..memory.short_term import ShortTermMemory
from ..utils.logger import Logger

logger = Logger.get_logger("executor")


class Executor:
    """
    执行器
    
    负责按计划执行步骤，调用工具，处理结果
    """
    
    def __init__(self, tool_executor: ToolExecutor,
                 short_term_memory: Optional[ShortTermMemory] = None):
        """
        初始化执行器
        
        Args:
            tool_executor: 工具执行器
            short_term_memory: 短期记忆
        """
        self.tool_executor = tool_executor
        self.short_term_memory = short_term_memory
    
    def execute_plan(self, plan: Plan, context: ExecutionContext) -> ExecutionResult:
        """
        执行完整计划
        
        Args:
            plan: 执行计划
            context: 执行上下文
            
        Returns:
            执行结果
        """
        logger.info(f"开始执行计划: {plan.id}, 目标: {plan.goal}")
        
        execution_result = ExecutionResult(
            plan_id=plan.id,
            goal=plan.goal,
            status=ExecutionStatus.RUNNING,
            context=context.variables.copy(),
            start_time=datetime.now(),
        )
        
        # 将计划存入短期记忆
        if self.short_term_memory:
            self.short_term_memory.store('plan', plan.to_dict())
            self.short_term_memory.store('execution_result', execution_result.to_dict())
        
        try:
            # 执行所有步骤
            while not plan.is_completed():
                # 获取可以执行的步骤
                ready_steps = plan.get_ready_steps()
                
                if not ready_steps:
                    # 没有可执行的步骤，检查是否有失败的步骤
                    if plan.has_failed():
                        execution_result.status = ExecutionStatus.FAILED
                        execution_result.error = "计划执行失败，存在失败的步骤"
                        break
                    else:
                        # 可能是循环依赖或其他问题
                        logger.warning("没有可执行的步骤，但计划未完成")
                        execution_result.status = ExecutionStatus.PARTIAL
                        break
                
                # 执行每个可执行的步骤
                for step in ready_steps:
                    step_result = self.execute_step(step, context)
                    execution_result.step_results.append(step_result)
                    
                    # 更新步骤状态
                    step.status = step_result.status
                    step.result = step_result.result
                    step.error = step_result.error
                    step.start_time = step_result.start_time
                    step.end_time = step_result.end_time
                    
                    # 更新短期记忆
                    if self.short_term_memory:
                        self.short_term_memory.store(f'step_{step.id}', step_result.to_dict())
                    
                    # 如果步骤失败且不可重试，标记计划失败
                    if step_result.status == StepStatus.FAILED and step.retry_count >= step.max_retries:
                        logger.error(f"步骤 {step.id} 执行失败且已达到最大重试次数")
                        execution_result.status = ExecutionStatus.FAILED
                        execution_result.error = f"步骤 {step.id} 执行失败: {step_result.error}"
                        break
            
            # 判断最终状态
            if execution_result.status == ExecutionStatus.RUNNING:
                if plan.is_completed() and not plan.has_failed():
                    execution_result.status = ExecutionStatus.SUCCESS
                elif plan.has_failed():
                    execution_result.status = ExecutionStatus.FAILED
        
        except Exception as e:
            logger.error(f"计划执行异常: {plan.id}, 错误: {e}", exc_info=True)
            execution_result.status = ExecutionStatus.FAILED
            execution_result.error = f"执行异常: {str(e)}"
        
        finally:
            execution_result.end_time = datetime.now()
            if execution_result.start_time:
                execution_result.total_duration = (
                    execution_result.end_time - execution_result.start_time
                ).total_seconds()
            
            logger.info(f"计划执行完成: {plan.id}, 状态: {execution_result.status.value}")
        
        return execution_result
    
    def execute_step(self, step: Step, context: ExecutionContext) -> StepResult:
        """
        执行单个步骤
        
        Args:
            step: 执行步骤
            context: 执行上下文
            
        Returns:
            步骤执行结果
        """
        logger.info(f"执行步骤: {step.id} - {step.name}")
        
        start_time = datetime.now()
        step.status = StepStatus.RUNNING
        
        try:
            # 准备工具参数（替换变量）
            tool_params = self._prepare_params(step.tool_params, context)
            
            # 调用工具
            tool_result = self.tool_executor.execute(step.tool_name, tool_params)
            
            # 处理工具结果
            if tool_result.success:
                # 将结果存入上下文
                context.set(f"step_{step.id}_result", tool_result.data)
                
                # 更新上下文变量
                if tool_result.data:
                    context.update(tool_result.data)
                
                end_time = datetime.now()
                duration = (end_time - start_time).total_seconds()
                
                return StepResult(
                    step_id=step.id,
                    status=StepStatus.SUCCESS,
                    result=tool_result.data,
                    duration=duration,
                    start_time=start_time,
                    end_time=end_time,
                )
            else:
                end_time = datetime.now()
                duration = (end_time - start_time).total_seconds()
                
                return StepResult(
                    step_id=step.id,
                    status=StepStatus.FAILED,
                    error=tool_result.error,
                    duration=duration,
                    start_time=start_time,
                    end_time=end_time,
                )
        
        except Exception as e:
            logger.error(f"步骤执行异常: {step.id}, 错误: {e}", exc_info=True)
            end_time = datetime.now()
            duration = (end_time - start_time).total_seconds()
            
            return StepResult(
                step_id=step.id,
                status=StepStatus.FAILED,
                error=f"执行异常: {str(e)}",
                duration=duration,
                start_time=start_time,
                end_time=end_time,
            )
    
    def _prepare_params(self, params: Dict[str, Any], context: ExecutionContext) -> Dict[str, Any]:
        """
        准备工具参数（替换变量）
        
        Args:
            params: 原始参数
            context: 执行上下文
            
        Returns:
            处理后的参数
        """
        prepared_params = {}
        
        for key, value in params.items():
            if isinstance(value, str) and value.startswith('${') and value.endswith('}'):
                # 变量引用，格式: ${variable_name} 或 ${step_id.field}
                var_path = value[2:-1]
                
                if '.' in var_path:
                    # 步骤结果引用，格式: step_id.field
                    step_id, field = var_path.split('.', 1)
                    step_result = context.get(f"step_{step_id}_result")
                    if step_result and isinstance(step_result, dict):
                        prepared_params[key] = step_result.get(field)
                    else:
                        prepared_params[key] = value  # 保持原值
                else:
                    # 上下文变量引用
                    prepared_params[key] = context.get(var_path, value)
            else:
                prepared_params[key] = value
        
        return prepared_params

