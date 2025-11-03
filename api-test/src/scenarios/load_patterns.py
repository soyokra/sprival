"""
负载模式模块

定义各种负载模式（恒定、渐进、峰值、波浪等）
"""

import time
import math
from typing import Callable, Optional
from .base_scenario import BaseScenario
from .thread_pool import DynamicThreadPool
from .metrics_collector import MetricsCollector
from ..utils.logger import Logger


class ConstantLoadScenario(BaseScenario):
    """
    恒定负载场景
    
    固定线程数持续运行指定时间
    """
    
    def __init__(self,
                 name: str = "ConstantLoad",
                 threads: int = 10,
                 duration: int = 60,
                 requests_per_thread: Optional[int] = None):
        """
        初始化恒定负载场景
        
        Args:
            name: 场景名称
            threads: 线程数
            duration: 持续时间（秒），0 表示一直运行直到手动停止
            requests_per_thread: 每个线程的请求数（不指定则持续运行）
        """
        super().__init__(name)
        self.threads = threads
        self.duration = duration
        self.requests_per_thread = requests_per_thread
        self.metrics = MetricsCollector()
        self.thread_pool: Optional[DynamicThreadPool] = None
    
    def run(self):
        """执行恒定负载场景"""
        self.logger.info(f"恒定负载: {self.threads} 线程, 持续 {self.duration} 秒")
        
        if not self._task_func:
            raise ValueError("未设置任务函数")
        
        # 创建线程池
        self.thread_pool = DynamicThreadPool(initial_thread_count=self.threads)
        
        # 启动指标收集
        self.metrics.start()
        
        # 启动线程池
        self.thread_pool.start(self._worker)
        
        # 提交任务
        start_time = time.time()
        while self._is_running:
            if self.duration > 0 and (time.time() - start_time) >= self.duration:
                break
            
            # 持续提交任务
            for _ in range(self.threads):
                if not self._is_running:
                    break
                self.thread_pool.submit_task(self._task_func)
            
            time.sleep(0.01)  # 短暂休眠避免过度消耗CPU
        
        # 停止
        self.metrics.stop()
        self.thread_pool.stop(wait=True, timeout=10)
        
        return self.metrics.get_metrics()
    
    def _worker(self, task_queue, is_running_func):
        """工作线程函数"""
        while is_running_func():
            try:
                task = task_queue.get(timeout=0.1)
                if task:
                    self._execute_task(task)
                task_queue.task_done()
            except Exception:
                continue
    
    def _execute_task(self, task_func: Callable):
        """执行任务"""
        start_time = time.time()
        try:
            result = task_func()
            elapsed_ms = (time.time() - start_time) * 1000
            
            # 记录成功
            status_code = getattr(result, 'status_code', 200) if result else 200
            self.metrics.record_request(
                success=True,
                response_time=elapsed_ms,
                status_code=status_code
            )
        except Exception as e:
            elapsed_ms = (time.time() - start_time) * 1000
            # 记录失败
            self.metrics.record_request(
                success=False,
                response_time=elapsed_ms,
                error_message=str(e)
            )


class RampUpScenario(BaseScenario):
    """
    渐进式压测场景
    
    从初始线程数逐步增加到目标线程数
    """
    
    def __init__(self,
                 name: str = "RampUp",
                 target_threads: int = 100,
                 ramp_duration: int = 60,
                 hold_duration: int = 60,
                 initial_threads: int = 1,
                 ramp_steps: int = 10):
        """
        初始化渐进式压测场景
        
        Args:
            name: 场景名称
            target_threads: 目标线程数
            ramp_duration: 渐进时长（秒）
            hold_duration: 保持目标负载的时长（秒）
            initial_threads: 初始线程数
            ramp_steps: 渐进步数
        """
        super().__init__(name)
        self.target_threads = target_threads
        self.ramp_duration = ramp_duration
        self.hold_duration = hold_duration
        self.initial_threads = initial_threads
        self.ramp_steps = ramp_steps
        self.metrics = MetricsCollector()
        self.thread_pool: Optional[DynamicThreadPool] = None
    
    def run(self):
        """执行渐进式压测场景"""
        self.logger.info(
            f"渐进式压测: {self.initial_threads} → {self.target_threads} 线程, "
            f"渐进 {self.ramp_duration}s, 保持 {self.hold_duration}s"
        )
        
        if not self._task_func:
            raise ValueError("未设置任务函数")
        
        # 创建动态线程池
        self.thread_pool = DynamicThreadPool(
            initial_thread_count=self.initial_threads,
            max_threads=self.target_threads
        )
        
        # 启动指标收集
        self.metrics.start()
        
        # 启动线程池
        self.thread_pool.start(self._worker)
        
        # 渐进阶段
        self._ramp_up_phase()
        
        # 保持阶段
        self._hold_phase()
        
        # 停止
        self.metrics.stop()
        self.thread_pool.stop(wait=True, timeout=10)
        
        return self.metrics.get_metrics()
    
    def _ramp_up_phase(self):
        """渐进阶段"""
        step_duration = self.ramp_duration / self.ramp_steps
        thread_increment = (self.target_threads - self.initial_threads) / self.ramp_steps
        
        current_threads = self.initial_threads
        
        for step in range(self.ramp_steps):
            if not self._is_running:
                break
            
            # 调整线程数
            current_threads += thread_increment
            self.thread_pool.scale_to(int(current_threads))
            
            self.logger.info(f"渐进步骤 {step+1}/{self.ramp_steps}: {int(current_threads)} 线程")
            
            # 提交任务
            step_end = time.time() + step_duration
            while time.time() < step_end and self._is_running:
                for _ in range(int(current_threads)):
                    self.thread_pool.submit_task(self._task_func)
                time.sleep(0.01)
    
    def _hold_phase(self):
        """保持阶段"""
        self.logger.info(f"保持目标负载: {self.target_threads} 线程, {self.hold_duration} 秒")
        
        end_time = time.time() + self.hold_duration
        while time.time() < end_time and self._is_running:
            for _ in range(self.target_threads):
                self.thread_pool.submit_task(self._task_func)
            time.sleep(0.01)
    
    def _worker(self, task_queue, is_running_func):
        """工作线程函数"""
        while is_running_func():
            try:
                task = task_queue.get(timeout=0.1)
                if task:
                    self._execute_task(task)
                task_queue.task_done()
            except Exception:
                continue
    
    def _execute_task(self, task_func: Callable):
        """执行任务"""
        start_time = time.time()
        try:
            result = task_func()
            elapsed_ms = (time.time() - start_time) * 1000
            
            status_code = getattr(result, 'status_code', 200) if result else 200
            self.metrics.record_request(
                success=True,
                response_time=elapsed_ms,
                status_code=status_code
            )
        except Exception as e:
            elapsed_ms = (time.time() - start_time) * 1000
            self.metrics.record_request(
                success=False,
                response_time=elapsed_ms,
                error_message=str(e)
            )


class SpikeScenario(BaseScenario):
    """
    峰值冲击场景
    
    瞬间达到峰值负载，持续一段时间后回落
    """
    
    def __init__(self,
                 name: str = "Spike",
                 spike_threads: int = 200,
                 spike_duration: int = 10,
                 base_threads: int = 10,
                 warmup_duration: int = 30):
        """
        初始化峰值冲击场景
        
        Args:
            name: 场景名称
            spike_threads: 峰值线程数
            spike_duration: 峰值持续时间（秒）
            base_threads: 基础线程数
            warmup_duration: 预热时间（秒）
        """
        super().__init__(name)
        self.spike_threads = spike_threads
        self.spike_duration = spike_duration
        self.base_threads = base_threads
        self.warmup_duration = warmup_duration
        self.metrics = MetricsCollector()
        self.thread_pool: Optional[DynamicThreadPool] = None
    
    def run(self):
        """执行峰值冲击场景"""
        self.logger.info(
            f"峰值冲击: 基础 {self.base_threads} → 峰值 {self.spike_threads} 线程, "
            f"峰值持续 {self.spike_duration}s"
        )
        
        if not self._task_func:
            raise ValueError("未设置任务函数")
        
        # 创建动态线程池
        self.thread_pool = DynamicThreadPool(
            initial_thread_count=self.base_threads,
            max_threads=self.spike_threads
        )
        
        # 启动指标收集
        self.metrics.start()
        
        # 启动线程池
        self.thread_pool.start(self._worker)
        
        # 预热阶段
        if self.warmup_duration > 0:
            self._warmup_phase()
        
        # 峰值阶段
        self._spike_phase()
        
        # 恢复基础负载
        self._cooldown_phase()
        
        # 停止
        self.metrics.stop()
        self.thread_pool.stop(wait=True, timeout=10)
        
        return self.metrics.get_metrics()
    
    def _warmup_phase(self):
        """预热阶段"""
        self.logger.info(f"预热阶段: {self.base_threads} 线程, {self.warmup_duration} 秒")
        
        end_time = time.time() + self.warmup_duration
        while time.time() < end_time and self._is_running:
            for _ in range(self.base_threads):
                self.thread_pool.submit_task(self._task_func)
            time.sleep(0.01)
    
    def _spike_phase(self):
        """峰值阶段"""
        self.logger.info(f"峰值冲击: {self.spike_threads} 线程, {self.spike_duration} 秒")
        
        # 瞬间扩展到峰值线程数
        self.thread_pool.scale_to(self.spike_threads)
        
        end_time = time.time() + self.spike_duration
        while time.time() < end_time and self._is_running:
            for _ in range(self.spike_threads):
                self.thread_pool.submit_task(self._task_func)
            time.sleep(0.01)
    
    def _cooldown_phase(self):
        """冷却阶段"""
        self.logger.info(f"恢复基础负载: {self.base_threads} 线程")
        
        # 不需要实际减少线程，只是减少任务提交
        for _ in range(50):  # 再运行一小段时间
            if not self._is_running:
                break
            for _ in range(self.base_threads):
                self.thread_pool.submit_task(self._task_func)
            time.sleep(0.1)
    
    def _worker(self, task_queue, is_running_func):
        """工作线程函数"""
        while is_running_func():
            try:
                task = task_queue.get(timeout=0.1)
                if task:
                    self._execute_task(task)
                task_queue.task_done()
            except Exception:
                continue
    
    def _execute_task(self, task_func: Callable):
        """执行任务"""
        start_time = time.time()
        try:
            result = task_func()
            elapsed_ms = (time.time() - start_time) * 1000
            
            status_code = getattr(result, 'status_code', 200) if result else 200
            self.metrics.record_request(
                success=True,
                response_time=elapsed_ms,
                status_code=status_code
            )
        except Exception as e:
            elapsed_ms = (time.time() - start_time) * 1000
            self.metrics.record_request(
                success=False,
                response_time=elapsed_ms,
                error_message=str(e)
            )


class WaveScenario(BaseScenario):
    """
    波浪式负载场景
    
    负载周期性波动
    """
    
    def __init__(self,
                 name: str = "Wave",
                 min_threads: int = 10,
                 max_threads: int = 100,
                 wave_period: int = 60,
                 total_duration: int = 300):
        """
        初始化波浪式负载场景
        
        Args:
            name: 场景名称
            min_threads: 最小线程数
            max_threads: 最大线程数
            wave_period: 波动周期（秒）
            total_duration: 总持续时间（秒）
        """
        super().__init__(name)
        self.min_threads = min_threads
        self.max_threads = max_threads
        self.wave_period = wave_period
        self.total_duration = total_duration
        self.metrics = MetricsCollector()
        self.thread_pool: Optional[DynamicThreadPool] = None
    
    def run(self):
        """执行波浪式负载场景"""
        self.logger.info(
            f"波浪式负载: {self.min_threads} ↔ {self.max_threads} 线程, "
            f"周期 {self.wave_period}s, 总时长 {self.total_duration}s"
        )
        
        if not self._task_func:
            raise ValueError("未设置任务函数")
        
        # 创建动态线程池
        self.thread_pool = DynamicThreadPool(
            initial_thread_count=self.min_threads,
            max_threads=self.max_threads
        )
        
        # 启动指标收集
        self.metrics.start()
        
        # 启动线程池
        self.thread_pool.start(self._worker)
        
        # 波浪式运行
        start_time = time.time()
        while (time.time() - start_time) < self.total_duration and self._is_running:
            # 计算当前线程数（正弦波）
            elapsed = time.time() - start_time
            wave_progress = (elapsed % self.wave_period) / self.wave_period
            sine_value = math.sin(wave_progress * 2 * math.pi)
            
            # 映射到线程数范围
            thread_range = self.max_threads - self.min_threads
            current_threads = self.min_threads + int((sine_value + 1) / 2 * thread_range)
            
            # 调整线程数
            self.thread_pool.scale_to(current_threads)
            
            # 提交任务
            for _ in range(current_threads):
                self.thread_pool.submit_task(self._task_func)
            
            time.sleep(0.1)
        
        # 停止
        self.metrics.stop()
        self.thread_pool.stop(wait=True, timeout=10)
        
        return self.metrics.get_metrics()
    
    def _worker(self, task_queue, is_running_func):
        """工作线程函数"""
        while is_running_func():
            try:
                task = task_queue.get(timeout=0.1)
                if task:
                    self._execute_task(task)
                task_queue.task_done()
            except Exception:
                continue
    
    def _execute_task(self, task_func: Callable):
        """执行任务"""
        start_time = time.time()
        try:
            result = task_func()
            elapsed_ms = (time.time() - start_time) * 1000
            
            status_code = getattr(result, 'status_code', 200) if result else 200
            self.metrics.record_request(
                success=True,
                response_time=elapsed_ms,
                status_code=status_code
            )
        except Exception as e:
            elapsed_ms = (time.time() - start_time) * 1000
            self.metrics.record_request(
                success=False,
                response_time=elapsed_ms,
                error_message=str(e)
            )

