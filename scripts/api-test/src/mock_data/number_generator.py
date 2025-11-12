"""
数值数据生成器

提供整数、浮点数等数值类型的生成功能
"""

import random
from typing import Optional, Union
from .base_generator import BaseGenerator


class NumberGenerator(BaseGenerator):
    """
    数值生成器
    
    支持生成整数、浮点数，支持范围和精度控制
    """
    
    def generate(self, 
                 value_type: str = "int",
                 min_val: Union[int, float] = 0,
                 max_val: Union[int, float] = 100,
                 precision: Optional[int] = None,
                 **kwargs) -> Union[int, float]:
        """
        生成数值
        
        Args:
            value_type: 数值类型，"int" 或 "float"
            min_val: 最小值
            max_val: 最大值
            precision: 浮点数精度（小数位数），仅对 float 有效
            **kwargs: 其他参数
            
        Returns:
            生成的数值
        """
        if value_type == "int":
            return self.integer(min_val=int(min_val), max_val=int(max_val))
        elif value_type == "float":
            return self.float(min_val=float(min_val), max_val=float(max_val), precision=precision)
        else:
            raise ValueError(f"Unsupported value_type: {value_type}")
    
    def integer(self, min_val: int = 0, max_val: int = 100) -> int:
        """
        生成整数
        
        Args:
            min_val: 最小值
            max_val: 最大值
            
        Returns:
            随机整数
        """
        return random.randint(min_val, max_val)
    
    def float(self, 
              min_val: float = 0.0, 
              max_val: float = 100.0, 
              precision: Optional[int] = 2) -> float:
        """
        生成浮点数
        
        Args:
            min_val: 最小值
            max_val: 最大值
            precision: 小数位数
            
        Returns:
            随机浮点数
        """
        value = random.uniform(min_val, max_val)
        if precision is not None:
            value = round(value, precision)
        return value
    
    def positive_int(self, max_val: int = 100) -> int:
        """
        生成正整数
        
        Args:
            max_val: 最大值
            
        Returns:
            正整数
        """
        return random.randint(1, max_val)
    
    def negative_int(self, min_val: int = -100) -> int:
        """
        生成负整数
        
        Args:
            min_val: 最小值（负数）
            
        Returns:
            负整数
        """
        return random.randint(min_val, -1)
    
    def percentage(self, precision: int = 2) -> float:
        """
        生成百分比数值（0.0 到 100.0）
        
        Args:
            precision: 小数位数
            
        Returns:
            百分比数值
        """
        return self.float(min_val=0.0, max_val=100.0, precision=precision)
    
    def probability(self, precision: int = 2) -> float:
        """
        生成概率数值（0.0 到 1.0）
        
        Args:
            precision: 小数位数
            
        Returns:
            概率数值
        """
        return self.float(min_val=0.0, max_val=1.0, precision=precision)
    
    def price(self, min_val: float = 0.01, max_val: float = 10000.0) -> float:
        """
        生成价格（保留两位小数）
        
        Args:
            min_val: 最小价格
            max_val: 最大价格
            
        Returns:
            价格数值
        """
        return self.float(min_val=min_val, max_val=max_val, precision=2)
    
    def sequence(self, start: int = 1, step: int = 1) -> int:
        """
        生成序列号（每次调用递增）
        
        Args:
            start: 起始值
            step: 步长
            
        Returns:
            序列号
        """
        if not hasattr(self, '_sequence_counter'):
            self._sequence_counter = start
        else:
            self._sequence_counter += step
        return self._sequence_counter
    
    def reset_sequence(self, start: int = 1):
        """
        重置序列号
        
        Args:
            start: 起始值
        """
        self._sequence_counter = start

