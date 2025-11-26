"""
智能体主类

协调规划器、执行器和评估器，提供统一的智能体接口
"""

import uuid
from typing import Dict, Any, Optional
from datetime import datetime

from .planner import Planner
from .executor import Executor
from .evaluator import Evaluator
from ..models.plan import Plan
from ..models.execution_result import ExecutionResult, ExecutionStatus
from ..models.context import ExecutionContext
from ..memory.short_term import ShortTermMemory
from ..memory.long_term import LongTermMemory
from ..memory.knowledge_base import KnowledgeBase
from ..tools.registry import ToolRegistry
from ..tools.executor import ToolExecutor
from ..tools.manager import ToolManager
from ..utils.logger import Logger

logger = Logger.get_logger("agent")


class Agent:
    """
    智能体主类
    
    协调规划器、执行器和评估器，提供统一的智能体接口
    """
    
    def __init__(self,
                 tool_registry: Optional[ToolRegistry] = None,
                 long_term_memory: Optional[LongTermMemory] = None,
                 knowledge_base: Optional[KnowledgeBase] = None):
        """
        初始化智能体
        
        Args:
            tool_registry: 工具注册表
            long_term_memory: 长期记忆
            knowledge_base: 知识库
        """
        # 初始化组件
        self.tool_registry = tool_registry or ToolRegistry()
        self.long_term_memory = long_term_memory or LongTermMemory()
        self.knowledge_base = knowledge_base or KnowledgeBase()
        
        # 初始化核心组件
        self.planner = Planner(self.long_term_memory, self.knowledge_base)
        self.tool_executor = ToolExecutor(self.tool_registry)
        self.evaluator = Evaluator()
        
        # 短期记忆在每次执行时创建
        self._short_term_memories: Dict[str, ShortTermMemory] = {}
    
    def execute(self, goal: str, context: Optional[Dict[str, Any]] = None) -> ExecutionResult:
        """
        执行任务
        
        Args:
            goal: 目标描述
            context: 上下文信息
            
        Returns:
            执行结果
        """
        task_id = str(uuid.uuid4())
        logger.info(f"开始执行任务: {task_id}, 目标: {goal}")
        
        # 创建执行上下文
        execution_context = ExecutionContext(
            task_id=task_id,
            goal=goal,
            variables=context or {},
        )
        
        # 创建短期记忆
        short_term_memory = ShortTermMemory(task_id)
        self._short_term_memories[task_id] = short_term_memory
        
        # 创建执行器（使用短期记忆）
        executor = Executor(self.tool_executor, short_term_memory)
        
        try:
            # 1. 生成计划
            plan = self.planner.plan(goal, execution_context.variables)
            logger.info(f"计划生成完成: {plan.id}, 包含 {len(plan.steps)} 个步骤")
            
            # 2. 执行计划
            execution_result = executor.execute_plan(plan, execution_context)
            
            # 3. 评估结果
            goal_evaluation = self.evaluator.evaluate_goal(goal, execution_result)
            execution_result.context['evaluation'] = goal_evaluation
            
            # 4. 保存到长期记忆
            if execution_result.status.value == 'success':
                self.long_term_memory.save_execution(
                    execution_id=task_id,
                    data={
                        'goal': goal,
                        'plan': plan.to_dict(),
                        'result': execution_result.to_dict(),
                        'evaluation': goal_evaluation,
                    }
                )
                logger.info(f"执行记录已保存到长期记忆: {task_id}")
            
            return execution_result
        
        except Exception as e:
            logger.error(f"任务执行异常: {task_id}, 错误: {e}", exc_info=True)
            
            # 返回失败结果
            return ExecutionResult(
                plan_id="",
                goal=goal,
                status=ExecutionStatus.FAILED,
                error=f"执行异常: {str(e)}",
                start_time=datetime.now(),
                end_time=datetime.now(),
            )
        
        finally:
            # 清理短期记忆（可选，可以保留一段时间）
            # del self._short_term_memories[task_id]
            pass
    
    def plan(self, goal: str, context: Optional[Dict[str, Any]] = None) -> Plan:
        """
        生成计划（不执行）
        
        Args:
            goal: 目标描述
            context: 上下文信息
            
        Returns:
            执行计划
        """
        logger.info(f"生成计划，目标: {goal}")
        return self.planner.plan(goal, context or {})
    
    def get_short_term_memory(self, task_id: str) -> Optional[ShortTermMemory]:
        """
        获取短期记忆
        
        Args:
            task_id: 任务 ID
            
        Returns:
            短期记忆实例，如果不存在则返回 None
        """
        return self._short_term_memories.get(task_id)

