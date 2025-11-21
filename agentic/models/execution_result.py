"""
执行结果模型

定义执行结果相关的数据模型
"""

from dataclasses import dataclass, field
from typing import Dict, Any, Optional, List
from enum import Enum
from datetime import datetime

from .plan import Step, StepStatus


class ExecutionStatus(Enum):
    """执行状态"""
    PENDING = "pending"          # 待执行
    RUNNING = "running"          # 执行中
    SUCCESS = "success"          # 成功
    FAILED = "failed"           # 失败
    CANCELLED = "cancelled"      # 已取消
    PARTIAL = "partial"          # 部分成功


@dataclass
class StepResult:
    """
    步骤执行结果
    """
    step_id: str                 # 步骤 ID
    status: StepStatus           # 步骤状态
    result: Optional[Dict[str, Any]] = None  # 执行结果
    error: Optional[str] = None             # 错误信息
    duration: float = 0.0                    # 执行时长（秒）
    start_time: Optional[datetime] = None   # 开始时间
    end_time: Optional[datetime] = None      # 结束时间
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            'step_id': self.step_id,
            'status': self.status.value,
            'result': self.result,
            'error': self.error,
            'duration': self.duration,
            'start_time': self.start_time.isoformat() if self.start_time else None,
            'end_time': self.end_time.isoformat() if self.end_time else None,
        }


@dataclass
class ExecutionResult:
    """
    执行结果
    
    包含整个计划执行的结果
    """
    plan_id: str                 # 计划 ID
    goal: str                    # 目标
    status: ExecutionStatus      # 执行状态
    step_results: List[StepResult] = field(default_factory=list)  # 步骤结果列表
    context: Dict[str, Any] = field(default_factory=dict)        # 上下文信息
    error: Optional[str] = None                                   # 错误信息
    start_time: Optional[datetime] = None                          # 开始时间
    end_time: Optional[datetime] = None                           # 结束时间
    total_duration: float = 0.0                                    # 总执行时长（秒）
    
    def get_step_result(self, step_id: str) -> Optional[StepResult]:
        """根据步骤 ID 获取结果"""
        for result in self.step_results:
            if result.step_id == step_id:
                return result
        return None
    
    def get_success_count(self) -> int:
        """获取成功步骤数"""
        return sum(1 for r in self.step_results if r.status == StepStatus.SUCCESS)
    
    def get_failed_count(self) -> int:
        """获取失败步骤数"""
        return sum(1 for r in self.step_results if r.status == StepStatus.FAILED)
    
    def get_total_steps(self) -> int:
        """获取总步骤数"""
        return len(self.step_results)
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            'plan_id': self.plan_id,
            'goal': self.goal,
            'status': self.status.value,
            'step_results': [r.to_dict() for r in self.step_results],
            'context': self.context,
            'error': self.error,
            'start_time': self.start_time.isoformat() if self.start_time else None,
            'end_time': self.end_time.isoformat() if self.end_time else None,
            'total_duration': self.total_duration,
            'summary': {
                'total_steps': self.get_total_steps(),
                'success_count': self.get_success_count(),
                'failed_count': self.get_failed_count(),
            }
        }

