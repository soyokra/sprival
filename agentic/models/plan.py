"""
计划模型

定义执行计划相关的数据模型
"""

from dataclasses import dataclass, field
from typing import List, Dict, Any, Optional
from enum import Enum
from datetime import datetime


class StepStatus(Enum):
    """步骤状态"""
    PENDING = "pending"          # 待执行
    RUNNING = "running"          # 执行中
    SUCCESS = "success"          # 成功
    FAILED = "failed"            # 失败
    SKIPPED = "skipped"          # 跳过
    CANCELLED = "cancelled"      # 已取消


@dataclass
class Step:
    """
    执行步骤
    
    表示计划中的一个原子操作
    """
    id: str                      # 步骤 ID
    name: str                    # 步骤名称
    description: str             # 步骤描述
    tool_name: str               # 使用的工具名称
    tool_params: Dict[str, Any]  # 工具参数
    status: StepStatus = StepStatus.PENDING  # 步骤状态
    result: Optional[Dict[str, Any]] = None   # 执行结果
    error: Optional[str] = None              # 错误信息
    start_time: Optional[datetime] = None    # 开始时间
    end_time: Optional[datetime] = None      # 结束时间
    retry_count: int = 0                     # 重试次数
    max_retries: int = 3                     # 最大重试次数
    depends_on: List[str] = field(default_factory=list)  # 依赖的步骤 ID
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            'id': self.id,
            'name': self.name,
            'description': self.description,
            'tool_name': self.tool_name,
            'tool_params': self.tool_params,
            'status': self.status.value,
            'result': self.result,
            'error': self.error,
            'start_time': self.start_time.isoformat() if self.start_time else None,
            'end_time': self.end_time.isoformat() if self.end_time else None,
            'retry_count': self.retry_count,
            'max_retries': self.max_retries,
            'depends_on': self.depends_on,
        }
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'Step':
        """从字典创建"""
        start_time = None
        if data.get('start_time'):
            start_time = datetime.fromisoformat(data['start_time'])
        
        end_time = None
        if data.get('end_time'):
            end_time = datetime.fromisoformat(data['end_time'])
        
        return cls(
            id=data['id'],
            name=data['name'],
            description=data['description'],
            tool_name=data['tool_name'],
            tool_params=data['tool_params'],
            status=StepStatus(data.get('status', 'pending')),
            result=data.get('result'),
            error=data.get('error'),
            start_time=start_time,
            end_time=end_time,
            retry_count=data.get('retry_count', 0),
            max_retries=data.get('max_retries', 3),
            depends_on=data.get('depends_on', []),
        )


@dataclass
class Plan:
    """
    执行计划
    
    包含一系列有序的步骤
    """
    id: str                      # 计划 ID
    goal: str                    # 目标描述
    steps: List[Step]            # 步骤列表
    context: Dict[str, Any] = field(default_factory=dict)  # 上下文信息
    created_at: datetime = field(default_factory=datetime.now)  # 创建时间
    updated_at: datetime = field(default_factory=datetime.now)  # 更新时间
    
    def get_step(self, step_id: str) -> Optional[Step]:
        """根据 ID 获取步骤"""
        for step in self.steps:
            if step.id == step_id:
                return step
        return None
    
    def get_ready_steps(self) -> List[Step]:
        """获取可以执行的步骤（依赖已满足）"""
        ready_steps = []
        for step in self.steps:
            if step.status != StepStatus.PENDING:
                continue
            
            # 检查依赖是否都已完成
            all_deps_met = True
            for dep_id in step.depends_on:
                dep_step = self.get_step(dep_id)
                if not dep_step or dep_step.status != StepStatus.SUCCESS:
                    all_deps_met = False
                    break
            
            if all_deps_met:
                ready_steps.append(step)
        
        return ready_steps
    
    def is_completed(self) -> bool:
        """检查计划是否已完成"""
        for step in self.steps:
            if step.status not in [StepStatus.SUCCESS, StepStatus.SKIPPED, StepStatus.CANCELLED]:
                return False
        return True
    
    def has_failed(self) -> bool:
        """检查是否有步骤失败"""
        for step in self.steps:
            if step.status == StepStatus.FAILED:
                return True
        return False
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            'id': self.id,
            'goal': self.goal,
            'steps': [step.to_dict() for step in self.steps],
            'context': self.context,
            'created_at': self.created_at.isoformat(),
            'updated_at': self.updated_at.isoformat(),
        }
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'Plan':
        """从字典创建"""
        created_at = datetime.fromisoformat(data['created_at']) if data.get('created_at') else datetime.now()
        updated_at = datetime.fromisoformat(data['updated_at']) if data.get('updated_at') else datetime.now()
        
        steps = [Step.from_dict(step_data) for step_data in data.get('steps', [])]
        
        return cls(
            id=data['id'],
            goal=data['goal'],
            steps=steps,
            context=data.get('context', {}),
            created_at=created_at,
            updated_at=updated_at,
        )

