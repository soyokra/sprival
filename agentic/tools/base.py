"""
工具基类

定义工具接口和基础实现
"""

from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Dict, Any, Optional
from enum import Enum

from ..utils.logger import Logger

logger = Logger.get_logger("tool")


class ToolResultStatus(Enum):
    """工具执行结果状态"""
    SUCCESS = "success"
    FAILED = "failed"
    PARTIAL = "partial"


@dataclass
class ToolResult:
    """
    工具执行结果
    """
    success: bool                    # 是否成功
    status: ToolResultStatus         # 状态
    data: Optional[Dict[str, Any]] = None  # 返回数据
    error: Optional[str] = None             # 错误信息
    metadata: Dict[str, Any] = None          # 元数据
    
    def __post_init__(self):
        if self.metadata is None:
            self.metadata = {}
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            'success': self.success,
            'status': self.status.value,
            'data': self.data,
            'error': self.error,
            'metadata': self.metadata,
        }


class Tool(ABC):
    """
    工具基类
    
    所有工具必须继承此类并实现 execute 方法
    """
    
    def __init__(self, name: str, description: str, version: str = "1.0.0"):
        """
        初始化工具
        
        Args:
            name: 工具名称
            description: 工具描述
            version: 工具版本
        """
        self.name = name
        self.description = description
        self.version = version
        self.logger = Logger.get_logger(f"tool.{name}")
    
    @abstractmethod
    def execute(self, params: Dict[str, Any]) -> ToolResult:
        """
        执行工具
        
        Args:
            params: 工具参数
            
        Returns:
            工具执行结果
        """
        pass
    
    def validate_params(self, params: Dict[str, Any]) -> tuple[bool, Optional[str]]:
        """
        验证参数
        
        Args:
            params: 工具参数
            
        Returns:
            (是否有效, 错误信息)
        """
        return True, None
    
    def get_schema(self) -> Dict[str, Any]:
        """
        获取工具参数模式
        
        Returns:
            参数模式定义
        """
        return {
            'name': self.name,
            'description': self.description,
            'version': self.version,
            'parameters': {},
        }
    
    def initialize(self, config: Dict[str, Any]) -> bool:
        """
        初始化工具
        
        Args:
            config: 配置信息
            
        Returns:
            是否初始化成功
        """
        return True
    
    def cleanup(self) -> None:
        """清理资源"""
        pass

