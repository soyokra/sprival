"""
评估器

评估执行结果，判断是否达成目标
"""

from typing import Dict, Any
from ..models.plan import Plan, Step
from ..models.execution_result import ExecutionResult, StepResult, ExecutionStatus
from ..utils.logger import Logger

logger = Logger.get_logger("evaluator")


class Evaluator:
    """
    评估器
    
    负责评估执行结果，判断是否达成目标
    """
    
    def __init__(self):
        """初始化评估器"""
        pass
    
    def evaluate_step(self, step: Step, result: StepResult) -> Dict[str, Any]:
        """
        评估步骤执行结果
        
        Args:
            step: 执行步骤
            result: 步骤结果
            
        Returns:
            评估结果
        """
        evaluation = {
            'step_id': step.id,
            'success': result.status.value == 'success',
            'quality_score': 0.0,
            'feedback': [],
        }
        
        if result.status.value == 'success':
            evaluation['quality_score'] = 1.0
            evaluation['feedback'].append(f"步骤 {step.id} 执行成功")
        else:
            evaluation['quality_score'] = 0.0
            evaluation['feedback'].append(f"步骤 {step.id} 执行失败: {result.error}")
        
        # 可以添加更详细的评估逻辑
        # 例如：性能评估、资源使用评估等
        
        return evaluation
    
    def evaluate_goal(self, goal: str, execution_result: ExecutionResult) -> Dict[str, Any]:
        """
        评估目标达成情况
        
        Args:
            goal: 目标描述
            execution_result: 执行结果
            
        Returns:
            目标评估结果
        """
        evaluation = {
            'goal': goal,
            'achieved': execution_result.status == ExecutionStatus.SUCCESS,
            'status': execution_result.status.value,
            'success_rate': 0.0,
            'quality_score': 0.0,
            'feedback': [],
            'summary': {
                'total_steps': execution_result.get_total_steps(),
                'success_count': execution_result.get_success_count(),
                'failed_count': execution_result.get_failed_count(),
            }
        }
        
        # 计算成功率
        total_steps = execution_result.get_total_steps()
        if total_steps > 0:
            evaluation['success_rate'] = execution_result.get_success_count() / total_steps
            evaluation['quality_score'] = evaluation['success_rate']
        
        # 生成反馈
        if execution_result.status == ExecutionStatus.SUCCESS:
            evaluation['feedback'].append("目标已达成，所有步骤执行成功")
        elif execution_result.status == ExecutionStatus.FAILED:
            evaluation['feedback'].append(f"目标未达成，执行失败: {execution_result.error}")
        elif execution_result.status == ExecutionStatus.PARTIAL:
            evaluation['feedback'].append("目标部分达成，部分步骤执行成功")
        
        # 添加详细反馈
        if execution_result.get_failed_count() > 0:
            failed_steps = [
                r.step_id for r in execution_result.step_results
                if r.status.value == 'failed'
            ]
            evaluation['feedback'].append(f"失败的步骤: {', '.join(failed_steps)}")
        
        logger.info(f"目标评估完成: {goal}, 达成: {evaluation['achieved']}, "
                   f"成功率: {evaluation['success_rate']:.2%}")
        
        return evaluation

