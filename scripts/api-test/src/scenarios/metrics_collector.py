"""
性能指标收集器模块

收集和统计测试过程中的性能数据
"""

import threading
import time
from typing import Dict, Any, List
from collections import defaultdict
import numpy as np


class MetricsCollector:
    """
    性能指标收集器
    
    线程安全地收集QPS、响应时间、成功率等指标
    """
    
    def __init__(self):
        self._lock = threading.Lock()
        self._start_time: float = 0
        self._end_time: float = 0
        
        # 请求计数
        self._total_requests = 0
        self._success_requests = 0
        self._failed_requests = 0
        
        # 响应时间（毫秒）
        self._response_times: List[float] = []
        
        # 错误统计
        self._errors: Dict[str, int] = defaultdict(int)
        
        # 状态码统计
        self._status_codes: Dict[int, int] = defaultdict(int)
    
    def start(self):
        """开始收集"""
        with self._lock:
            self._start_time = time.time()
    
    def stop(self):
        """停止收集"""
        with self._lock:
            self._end_time = time.time()
    
    def record_request(self,
                      success: bool,
                      response_time: float,
                      status_code: int = 0,
                      error_message: str = ""):
        """
        记录单个请求
        
        Args:
            success: 是否成功
            response_time: 响应时间（毫秒）
            status_code: HTTP 状态码
            error_message: 错误消息
        """
        with self._lock:
            self._total_requests += 1
            
            if success:
                self._success_requests += 1
            else:
                self._failed_requests += 1
                if error_message:
                    self._errors[error_message] += 1
            
            self._response_times.append(response_time)
            
            if status_code:
                self._status_codes[status_code] += 1
    
    def get_metrics(self) -> Dict[str, Any]:
        """
        获取当前指标
        
        Returns:
            指标字典
        """
        with self._lock:
            elapsed = self._get_elapsed_time()
            
            if not self._response_times:
                return self._empty_metrics(elapsed)
            
            # 计算响应时间统计
            response_times = np.array(self._response_times)
            
            metrics = {
                # 基础指标
                "total_requests": self._total_requests,
                "success_requests": self._success_requests,
                "failed_requests": self._failed_requests,
                "success_rate": self._success_requests / self._total_requests * 100 if self._total_requests > 0 else 0,
                
                # 时间指标
                "elapsed_time": elapsed,
                "qps": self._total_requests / elapsed if elapsed > 0 else 0,
                
                # 响应时间指标（毫秒）
                "response_time": {
                    "min": float(np.min(response_times)),
                    "max": float(np.max(response_times)),
                    "mean": float(np.mean(response_times)),
                    "median": float(np.median(response_times)),
                    "p90": float(np.percentile(response_times, 90)),
                    "p95": float(np.percentile(response_times, 95)),
                    "p99": float(np.percentile(response_times, 99)),
                    "std": float(np.std(response_times))
                },
                
                # 状态码统计
                "status_codes": dict(self._status_codes),
                
                # 错误统计
                "errors": dict(self._errors)
            }
            
            return metrics
    
    def _empty_metrics(self, elapsed: float) -> Dict[str, Any]:
        """
        返回空指标
        
        Args:
            elapsed: 经过时间
            
        Returns:
            空指标字典
        """
        return {
            "total_requests": 0,
            "success_requests": 0,
            "failed_requests": 0,
            "success_rate": 0,
            "elapsed_time": elapsed,
            "qps": 0,
            "response_time": {
                "min": 0,
                "max": 0,
                "mean": 0,
                "median": 0,
                "p90": 0,
                "p95": 0,
                "p99": 0,
                "std": 0
            },
            "status_codes": {},
            "errors": {}
        }
    
    def _get_elapsed_time(self) -> float:
        """
        获取经过时间
        
        Returns:
            经过时间（秒）
        """
        if self._end_time > 0:
            return self._end_time - self._start_time
        elif self._start_time > 0:
            return time.time() - self._start_time
        else:
            return 0
    
    def get_summary(self) -> str:
        """
        获取指标摘要（格式化字符串）
        
        Returns:
            摘要字符串
        """
        metrics = self.get_metrics()
        
        lines = [
            "=" * 60,
            "性能指标摘要",
            "=" * 60,
            f"总请求数: {metrics['total_requests']}",
            f"成功请求: {metrics['success_requests']}",
            f"失败请求: {metrics['failed_requests']}",
            f"成功率: {metrics['success_rate']:.2f}%",
            f"运行时间: {metrics['elapsed_time']:.2f}秒",
            f"QPS: {metrics['qps']:.2f}",
            "",
            "响应时间（毫秒）:",
            f"  最小值: {metrics['response_time']['min']:.2f}",
            f"  最大值: {metrics['response_time']['max']:.2f}",
            f"  平均值: {metrics['response_time']['mean']:.2f}",
            f"  中位数: {metrics['response_time']['median']:.2f}",
            f"  P90: {metrics['response_time']['p90']:.2f}",
            f"  P95: {metrics['response_time']['p95']:.2f}",
            f"  P99: {metrics['response_time']['p99']:.2f}",
        ]
        
        if metrics['status_codes']:
            lines.append("")
            lines.append("状态码分布:")
            for code, count in sorted(metrics['status_codes'].items()):
                lines.append(f"  {code}: {count}")
        
        if metrics['errors']:
            lines.append("")
            lines.append("错误统计:")
            for error, count in sorted(metrics['errors'].items(), key=lambda x: x[1], reverse=True):
                lines.append(f"  {error}: {count}")
        
        lines.append("=" * 60)
        
        return "\n".join(lines)
    
    def reset(self):
        """重置所有指标"""
        with self._lock:
            self._start_time = 0
            self._end_time = 0
            self._total_requests = 0
            self._success_requests = 0
            self._failed_requests = 0
            self._response_times.clear()
            self._errors.clear()
            self._status_codes.clear()

