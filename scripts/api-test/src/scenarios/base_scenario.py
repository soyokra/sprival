"""
场景基类模块

定义测试场景的基础接口和生命周期
"""

from abc import ABC, abstractmethod
from typing import Callable, Dict, Any, Optional
from ..utils.logger import Logger


class BaseScenario(ABC):
    """
    场景基类
    
    定义场景的生命周期：setup -> run -> teardown
    """
    
    def __init__(self, name: str = "DefaultScenario"):
        """
        初始化场景
        
        Args:
            name: 场景名称
        """
        self.name = name
        self.logger = Logger.get_logger(f"scenario.{name}")
        self._task_func: Optional[Callable] = None
        self._is_running = False
    
    def set_task(self, task_func: Callable):
        """
        设置任务函数
        
        Args:
            task_func: 要执行的任务函数
        """
        self._task_func = task_func
    
    def setup(self):
        """
        场景启动前的准备工作
        
        子类可以重写此方法进行自定义初始化
        """
        self.logger.info(f"场景 {self.name} 开始准备")
    
    @abstractmethod
    def run(self) -> Dict[str, Any]:
        """
        执行场景
        
        Returns:
            场景执行结果
        """
        pass
    
    def teardown(self):
        """
        场景结束后的清理工作
        
        子类可以重写此方法进行自定义清理
        """
        self.logger.info(f"场景 {self.name} 清理完成")
    
    def execute(self) -> Dict[str, Any]:
        """
        完整的场景执行流程
        
        Returns:
            场景执行结果
        """
        try:
            self.setup()
            self._is_running = True
            result = self.run()
            return result
        except Exception as e:
            self.logger.error(f"场景执行失败: {str(e)}", exc_info=True)
            raise
        finally:
            self._is_running = False
            self.teardown()
    
    def is_running(self) -> bool:
        """
        场景是否正在运行
        
        Returns:
            是否运行中
        """
        return self._is_running
    
    def stop(self):
        """停止场景"""
        self.logger.info(f"停止场景 {self.name}")
        self._is_running = False

