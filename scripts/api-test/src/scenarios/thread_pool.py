"""
线程池管理器模块

提供线程池的创建和管理功能
"""

import threading
import queue
import time
from typing import Callable, Any, Optional, List
from ..utils.logger import Logger


class ThreadPool:
    """
    线程池管理器
    
    管理工作线程的创建、启动、停止
    """
    
    def __init__(self, 
                 thread_count: int = 10,
                 task_queue_size: int = 0):
        """
        初始化线程池
        
        Args:
            thread_count: 线程数量
            task_queue_size: 任务队列大小（0 表示无限）
        """
        self.thread_count = thread_count
        self.task_queue = queue.Queue(maxsize=task_queue_size)
        self.threads: List[threading.Thread] = []
        self.is_running = False
        self.logger = Logger.get_logger("thread_pool")
    
    def start(self, worker_func: Callable):
        """
        启动线程池
        
        Args:
            worker_func: 工作函数，接收任务队列作为参数
        """
        if self.is_running:
            self.logger.warning("线程池已经在运行")
            return
        
        self.is_running = True
        self.logger.info(f"启动线程池，线程数: {self.thread_count}")
        
        for i in range(self.thread_count):
            thread = threading.Thread(
                target=self._worker_wrapper,
                args=(worker_func,),
                name=f"Worker-{i+1}",
                daemon=True
            )
            thread.start()
            self.threads.append(thread)
    
    def _worker_wrapper(self, worker_func: Callable):
        """
        工作线程包装器
        
        Args:
            worker_func: 工作函数
        """
        try:
            worker_func(self.task_queue, lambda: self.is_running)
        except Exception as e:
            self.logger.error(f"工作线程异常: {str(e)}", exc_info=True)
    
    def submit_task(self, task: Any, block: bool = True, timeout: Optional[float] = None):
        """
        提交任务
        
        Args:
            task: 任务对象
            block: 是否阻塞等待
            timeout: 超时时间（秒）
        """
        try:
            self.task_queue.put(task, block=block, timeout=timeout)
        except queue.Full:
            self.logger.warning("任务队列已满")
    
    def stop(self, wait: bool = True, timeout: Optional[float] = None):
        """
        停止线程池
        
        Args:
            wait: 是否等待所有任务完成
            timeout: 等待超时时间（秒）
        """
        if not self.is_running:
            return
        
        self.logger.info("停止线程池")
        self.is_running = False
        
        if wait:
            # 等待队列清空
            start_time = time.time()
            while not self.task_queue.empty():
                if timeout and (time.time() - start_time) > timeout:
                    self.logger.warning("等待任务完成超时")
                    break
                time.sleep(0.1)
        
        # 等待所有线程结束
        for thread in self.threads:
            if thread.is_alive():
                thread.join(timeout=1.0)
        
        self.threads.clear()
        
        # 清空队列
        while not self.task_queue.empty():
            try:
                self.task_queue.get_nowait()
            except queue.Empty:
                break
        
        self.logger.info("线程池已停止")
    
    def get_queue_size(self) -> int:
        """
        获取当前队列大小
        
        Returns:
            队列中的任务数量
        """
        return self.task_queue.qsize()
    
    def get_active_thread_count(self) -> int:
        """
        获取活跃线程数
        
        Returns:
            活跃线程数量
        """
        return sum(1 for thread in self.threads if thread.is_alive())


class DynamicThreadPool(ThreadPool):
    """
    动态线程池
    
    支持动态调整线程数量
    """
    
    def __init__(self, 
                 initial_thread_count: int = 10,
                 min_threads: int = 1,
                 max_threads: int = 100,
                 task_queue_size: int = 0):
        """
        初始化动态线程池
        
        Args:
            initial_thread_count: 初始线程数
            min_threads: 最小线程数
            max_threads: 最大线程数
            task_queue_size: 任务队列大小
        """
        super().__init__(initial_thread_count, task_queue_size)
        self.min_threads = min_threads
        self.max_threads = max_threads
        self.worker_func: Optional[Callable] = None
    
    def start(self, worker_func: Callable):
        """启动动态线程池"""
        self.worker_func = worker_func
        super().start(worker_func)
    
    def scale_to(self, target_count: int):
        """
        调整线程数到目标数量
        
        Args:
            target_count: 目标线程数
        """
        target_count = max(self.min_threads, min(target_count, self.max_threads))
        current_count = len(self.threads)
        
        if target_count > current_count:
            # 增加线程
            self._add_threads(target_count - current_count)
        elif target_count < current_count:
            # 减少线程（实际上很难优雅地减少，这里只是标记）
            self.logger.info(f"线程数将从 {current_count} 逐步减少到 {target_count}")
    
    def _add_threads(self, count: int):
        """
        添加线程
        
        Args:
            count: 要添加的线程数
        """
        if not self.is_running or not self.worker_func:
            return
        
        self.logger.info(f"增加 {count} 个线程")
        
        for i in range(count):
            thread = threading.Thread(
                target=self._worker_wrapper,
                args=(self.worker_func,),
                name=f"Worker-{len(self.threads)+1}",
                daemon=True
            )
            thread.start()
            self.threads.append(thread)

