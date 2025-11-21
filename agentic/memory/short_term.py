"""
短期记忆模块

保存当前任务执行过程中的上下文信息
"""

from typing import Dict, Any, Optional
from datetime import datetime, timedelta
import threading
from ..utils.logger import Logger

logger = Logger.get_logger("short_term_memory")


class ShortTermMemory:
    """
    短期记忆
    
    在内存中保存任务执行过程中的上下文信息
    任务执行期间有效，支持快速读写
    """
    
    def __init__(self, task_id: str, ttl: int = 3600):
        """
        初始化短期记忆
        
        Args:
            task_id: 任务 ID
            ttl: 生存时间（秒），默认 1 小时
        """
        self.task_id = task_id
        self.ttl = ttl
        self._data: Dict[str, Any] = {}
        self._timestamps: Dict[str, datetime] = {}
        self._lock = threading.RLock()
        self.created_at = datetime.now()
    
    def store(self, key: str, value: Any) -> None:
        """
        存储信息
        
        Args:
            key: 键
            value: 值
        """
        with self._lock:
            self._data[key] = value
            self._timestamps[key] = datetime.now()
            logger.debug(f"存储短期记忆: {key} = {value}")
    
    def retrieve(self, key: str, default: Any = None) -> Any:
        """
        检索信息
        
        Args:
            key: 键
            default: 默认值
            
        Returns:
            存储的值，如果不存在或已过期则返回默认值
        """
        with self._lock:
            if key not in self._data:
                return default
            
            # 检查是否过期
            if key in self._timestamps:
                age = (datetime.now() - self._timestamps[key]).total_seconds()
                if age > self.ttl:
                    logger.debug(f"短期记忆已过期: {key} (age: {age}s)")
                    del self._data[key]
                    del self._timestamps[key]
                    return default
            
            return self._data.get(key, default)
    
    def update(self, data: Dict[str, Any]) -> None:
        """
        批量更新
        
        Args:
            data: 要更新的数据字典
        """
        with self._lock:
            now = datetime.now()
            for key, value in data.items():
                self._data[key] = value
                self._timestamps[key] = now
            logger.debug(f"批量更新短期记忆: {len(data)} 个键")
    
    def delete(self, key: str) -> None:
        """
        删除信息
        
        Args:
            key: 键
        """
        with self._lock:
            if key in self._data:
                del self._data[key]
            if key in self._timestamps:
                del self._timestamps[key]
            logger.debug(f"删除短期记忆: {key}")
    
    def clear(self) -> None:
        """清空所有信息"""
        with self._lock:
            self._data.clear()
            self._timestamps.clear()
            logger.debug("清空短期记忆")
    
    def get_all(self) -> Dict[str, Any]:
        """
        获取所有信息
        
        Returns:
            所有存储的数据
        """
        with self._lock:
            # 清理过期数据
            now = datetime.now()
            expired_keys = []
            for key, timestamp in self._timestamps.items():
                age = (now - timestamp).total_seconds()
                if age > self.ttl:
                    expired_keys.append(key)
            
            for key in expired_keys:
                del self._data[key]
                del self._timestamps[key]
            
            return self._data.copy()
    
    def exists(self, key: str) -> bool:
        """
        检查键是否存在
        
        Args:
            key: 键
            
        Returns:
            是否存在
        """
        with self._lock:
            if key not in self._data:
                return False
            
            # 检查是否过期
            if key in self._timestamps:
                age = (datetime.now() - self._timestamps[key]).total_seconds()
                if age > self.ttl:
                    del self._data[key]
                    del self._timestamps[key]
                    return False
            
            return True
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            'task_id': self.task_id,
            'data': self.get_all(),
            'created_at': self.created_at.isoformat(),
        }

