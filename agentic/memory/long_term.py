"""
长期记忆模块

持久化保存历史执行记录和经验
"""

import json
import os
from typing import Dict, Any, List, Optional
from datetime import datetime
from pathlib import Path
from ..utils.logger import Logger

logger = Logger.get_logger("long_term_memory")


class LongTermMemory:
    """
    长期记忆
    
    持久化保存历史执行记录和经验
    使用文件系统存储（可扩展为数据库）
    """
    
    def __init__(self, storage_dir: str = "data/memory"):
        """
        初始化长期记忆
        
        Args:
            storage_dir: 存储目录
        """
        self.storage_dir = Path(storage_dir)
        self.storage_dir.mkdir(parents=True, exist_ok=True)
        
        # 创建子目录
        self.executions_dir = self.storage_dir / "executions"
        self.experiences_dir = self.storage_dir / "experiences"
        self.patterns_dir = self.storage_dir / "patterns"
        
        for dir_path in [self.executions_dir, self.experiences_dir, self.patterns_dir]:
            dir_path.mkdir(parents=True, exist_ok=True)
    
    def save_execution(self, execution_id: str, data: Dict[str, Any]) -> None:
        """
        保存执行记录
        
        Args:
            execution_id: 执行 ID
            data: 执行数据
        """
        file_path = self.executions_dir / f"{execution_id}.json"
        
        execution_data = {
            'execution_id': execution_id,
            'saved_at': datetime.now().isoformat(),
            'data': data,
        }
        
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(execution_data, f, ensure_ascii=False, indent=2)
        
        logger.debug(f"保存执行记录: {execution_id}")
    
    def load_execution(self, execution_id: str) -> Optional[Dict[str, Any]]:
        """
        加载执行记录
        
        Args:
            execution_id: 执行 ID
            
        Returns:
            执行数据，如果不存在则返回 None
        """
        file_path = self.executions_dir / f"{execution_id}.json"
        
        if not file_path.exists():
            return None
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                return data.get('data')
        except Exception as e:
            logger.error(f"加载执行记录失败: {execution_id}, 错误: {e}")
            return None
    
    def list_executions(self, limit: int = 100) -> List[Dict[str, Any]]:
        """
        列出执行记录
        
        Args:
            limit: 返回数量限制
            
        Returns:
            执行记录列表
        """
        executions = []
        
        for file_path in sorted(self.executions_dir.glob("*.json"), reverse=True)[:limit]:
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    executions.append({
                        'execution_id': data.get('execution_id'),
                        'saved_at': data.get('saved_at'),
                        'summary': data.get('data', {}).get('summary', {}),
                    })
            except Exception as e:
                logger.error(f"读取执行记录失败: {file_path}, 错误: {e}")
        
        return executions
    
    def save_experience(self, experience_id: str, data: Dict[str, Any]) -> None:
        """
        保存经验
        
        Args:
            experience_id: 经验 ID
            data: 经验数据
        """
        file_path = self.experiences_dir / f"{experience_id}.json"
        
        experience_data = {
            'experience_id': experience_id,
            'saved_at': datetime.now().isoformat(),
            'data': data,
        }
        
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(experience_data, f, ensure_ascii=False, indent=2)
        
        logger.debug(f"保存经验: {experience_id}")
    
    def load_experience(self, experience_id: str) -> Optional[Dict[str, Any]]:
        """
        加载经验
        
        Args:
            experience_id: 经验 ID
            
        Returns:
            经验数据，如果不存在则返回 None
        """
        file_path = self.experiences_dir / f"{experience_id}.json"
        
        if not file_path.exists():
            return None
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                return data.get('data')
        except Exception as e:
            logger.error(f"加载经验失败: {experience_id}, 错误: {e}")
            return None
    
    def search_similar_executions(self, goal: str, limit: int = 10) -> List[Dict[str, Any]]:
        """
        搜索相似执行记录
        
        Args:
            goal: 目标描述
            limit: 返回数量限制
            
        Returns:
            相似执行记录列表
        """
        # 简单的关键词匹配（可扩展为向量搜索）
        goal_lower = goal.lower()
        similar = []
        
        for file_path in self.executions_dir.glob("*.json"):
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    execution_goal = data.get('data', {}).get('goal', '').lower()
                    
                    # 简单的关键词匹配
                    if any(keyword in execution_goal for keyword in goal_lower.split()):
                        similar.append({
                            'execution_id': data.get('execution_id'),
                            'goal': data.get('data', {}).get('goal'),
                            'saved_at': data.get('saved_at'),
                        })
                        
                        if len(similar) >= limit:
                            break
            except Exception as e:
                logger.error(f"搜索执行记录失败: {file_path}, 错误: {e}")
        
        return similar
    
    def save_pattern(self, pattern_id: str, data: Dict[str, Any]) -> None:
        """
        保存模式
        
        Args:
            pattern_id: 模式 ID
            data: 模式数据
        """
        file_path = self.patterns_dir / f"{pattern_id}.json"
        
        pattern_data = {
            'pattern_id': pattern_id,
            'saved_at': datetime.now().isoformat(),
            'data': data,
        }
        
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(pattern_data, f, ensure_ascii=False, indent=2)
        
        logger.debug(f"保存模式: {pattern_id}")

