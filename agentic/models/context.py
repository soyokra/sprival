"""
执行上下文模型

定义执行过程中的上下文信息
"""

from dataclasses import dataclass, field
from typing import Dict, Any, Optional
from datetime import datetime


@dataclass
class ExecutionContext:
    """
    执行上下文
    
    保存任务执行过程中的上下文信息
    """
    task_id: str                 # 任务 ID
    goal: str                    # 目标
    variables: Dict[str, Any] = field(default_factory=dict)  # 变量
    metadata: Dict[str, Any] = field(default_factory=dict)   # 元数据
    created_at: datetime = field(default_factory=datetime.now)  # 创建时间
    updated_at: datetime = field(default_factory=datetime.now)   # 更新时间
    
    def get(self, key: str, default: Any = None) -> Any:
        """获取变量值"""
        return self.variables.get(key, default)
    
    def set(self, key: str, value: Any) -> None:
        """设置变量值"""
        self.variables[key] = value
        self.updated_at = datetime.now()
    
    def update(self, data: Dict[str, Any]) -> None:
        """批量更新变量"""
        self.variables.update(data)
        self.updated_at = datetime.now()
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            'task_id': self.task_id,
            'goal': self.goal,
            'variables': self.variables,
            'metadata': self.metadata,
            'created_at': self.created_at.isoformat(),
            'updated_at': self.updated_at.isoformat(),
        }
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'ExecutionContext':
        """从字典创建"""
        created_at = datetime.fromisoformat(data['created_at']) if data.get('created_at') else datetime.now()
        updated_at = datetime.fromisoformat(data['updated_at']) if data.get('updated_at') else datetime.now()
        
        return cls(
            task_id=data['task_id'],
            goal=data['goal'],
            variables=data.get('variables', {}),
            metadata=data.get('metadata', {}),
            created_at=created_at,
            updated_at=updated_at,
        )

