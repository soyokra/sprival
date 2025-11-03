"""
基础数据生成器抽象类

定义所有数据生成器的基础接口和通用行为
"""

from abc import ABC, abstractmethod
from typing import Any


class BaseGenerator(ABC):
    """
    数据生成器基类
    
    所有具体的数据生成器都应该继承此类并实现 generate 方法
    """
    
    @abstractmethod
    def generate(self, **kwargs) -> Any:
        """
        生成数据的抽象方法
        
        Args:
            **kwargs: 生成数据所需的参数
            
        Returns:
            生成的数据
        """
        pass
    
    def generate_batch(self, count: int, **kwargs) -> list:
        """
        批量生成数据
        
        Args:
            count: 生成数据的数量
            **kwargs: 生成数据所需的参数
            
        Returns:
            生成的数据列表
        """
        return [self.generate(**kwargs) for _ in range(count)]

