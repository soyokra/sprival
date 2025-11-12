"""
报告基类模块

定义报告生成器的基础接口
"""

from abc import ABC, abstractmethod
from typing import Dict, Any
from datetime import datetime


class BaseReporter(ABC):
    """
    报告生成器基类
    
    所有具体的报告生成器都应继承此类
    """
    
    def __init__(self, report_dir: str = "reports"):
        """
        初始化报告生成器
        
        Args:
            report_dir: 报告输出目录
        """
        self.report_dir = report_dir
        self.report_data: Dict[str, Any] = {}
        self.start_time: datetime = datetime.now()
    
    def set_report_data(self, data: Dict[str, Any]):
        """
        设置报告数据
        
        Args:
            data: 报告数据
        """
        self.report_data = data
        self.report_data["report_time"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        self.report_data["start_time"] = self.start_time.strftime("%Y-%m-%d %H:%M:%S")
    
    @abstractmethod
    def generate(self) -> str:
        """
        生成报告
        
        Returns:
            报告文件路径或内容
        """
        pass
    
    @abstractmethod
    def save(self, filename: str = None) -> str:
        """
        保存报告
        
        Args:
            filename: 文件名（不指定则自动生成）
            
        Returns:
            保存的文件路径
        """
        pass

