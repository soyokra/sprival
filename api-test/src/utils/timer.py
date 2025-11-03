"""
计时器工具模块

提供性能计时和时间统计功能
"""

import time
from typing import Optional, Callable, Any
from contextlib import contextmanager


class Timer:
    """
    计时器类
    
    支持启动、停止、暂停、恢复等功能
    """
    
    def __init__(self):
        self._start_time: Optional[float] = None
        self._end_time: Optional[float] = None
        self._pause_time: Optional[float] = None
        self._total_paused: float = 0.0
        self._is_running: bool = False
        self._is_paused: bool = False
    
    def start(self):
        """启动计时器"""
        if self._is_running:
            return
        
        self._start_time = time.time()
        self._end_time = None
        self._total_paused = 0.0
        self._is_running = True
        self._is_paused = False
    
    def stop(self) -> float:
        """
        停止计时器
        
        Returns:
            elapsed: 经过的时间（秒）
        """
        if not self._is_running:
            return 0.0
        
        if self._is_paused:
            self.resume()
        
        self._end_time = time.time()
        self._is_running = False
        
        return self.elapsed()
    
    def pause(self):
        """暂停计时器"""
        if not self._is_running or self._is_paused:
            return
        
        self._pause_time = time.time()
        self._is_paused = True
    
    def resume(self):
        """恢复计时器"""
        if not self._is_paused:
            return
        
        self._total_paused += time.time() - self._pause_time
        self._pause_time = None
        self._is_paused = False
    
    def elapsed(self) -> float:
        """
        获取经过的时间
        
        Returns:
            经过的时间（秒）
        """
        if self._start_time is None:
            return 0.0
        
        if self._is_paused:
            end = self._pause_time
        elif self._end_time is not None:
            end = self._end_time
        else:
            end = time.time()
        
        return end - self._start_time - self._total_paused
    
    def elapsed_ms(self) -> float:
        """
        获取经过的时间（毫秒）
        
        Returns:
            经过的时间（毫秒）
        """
        return self.elapsed() * 1000
    
    def reset(self):
        """重置计时器"""
        self._start_time = None
        self._end_time = None
        self._pause_time = None
        self._total_paused = 0.0
        self._is_running = False
        self._is_paused = False
    
    @property
    def is_running(self) -> bool:
        """计时器是否正在运行"""
        return self._is_running
    
    @property
    def is_paused(self) -> bool:
        """计时器是否暂停"""
        return self._is_paused


@contextmanager
def measure_time(name: str = "Operation", logger: Optional[Callable] = None):
    """
    测量代码块执行时间的上下文管理器
    
    Args:
        name: 操作名称
        logger: 日志记录函数（可选）
        
    用法:
        with measure_time("数据库查询"):
            # 执行操作
            pass
    """
    start = time.time()
    yield
    elapsed = time.time() - start
    
    message = f"{name} 耗时: {elapsed:.3f} 秒"
    
    if logger:
        logger(message)
    else:
        print(message)


def timeit(func: Callable) -> Callable:
    """
    函数执行时间装饰器
    
    Args:
        func: 要装饰的函数
        
    Returns:
        装饰后的函数
        
    用法:
        @timeit
        def my_function():
            pass
    """
    def wrapper(*args, **kwargs):
        start = time.time()
        result = func(*args, **kwargs)
        elapsed = time.time() - start
        print(f"函数 {func.__name__} 耗时: {elapsed:.3f} 秒")
        return result
    
    return wrapper


class StopWatch:
    """
    秒表类
    
    用于记录多个检查点的时间
    """
    
    def __init__(self):
        self._checkpoints = []
        self._start_time = time.time()
    
    def checkpoint(self, name: str):
        """
        记录检查点
        
        Args:
            name: 检查点名称
        """
        current_time = time.time()
        elapsed = current_time - self._start_time
        
        if self._checkpoints:
            last_time = self._checkpoints[-1][1]
            interval = current_time - last_time
        else:
            interval = elapsed
        
        self._checkpoints.append((name, current_time, elapsed, interval))
    
    def reset(self):
        """重置秒表"""
        self._checkpoints.clear()
        self._start_time = time.time()
    
    def report(self) -> str:
        """
        生成检查点报告
        
        Returns:
            报告字符串
        """
        if not self._checkpoints:
            return "无检查点记录"
        
        lines = ["检查点时间报告:", "-" * 60]
        lines.append(f"{'检查点':<20} {'总耗时(秒)':<15} {'间隔(秒)':<15}")
        lines.append("-" * 60)
        
        for name, _, elapsed, interval in self._checkpoints:
            lines.append(f"{name:<20} {elapsed:>10.3f}     {interval:>10.3f}")
        
        lines.append("-" * 60)
        
        return "\n".join(lines)
    
    def get_checkpoints(self):
        """
        获取所有检查点
        
        Returns:
            检查点列表
        """
        return [
            {
                "name": name,
                "timestamp": timestamp,
                "elapsed": elapsed,
                "interval": interval
            }
            for name, timestamp, elapsed, interval in self._checkpoints
        ]

