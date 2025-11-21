"""
知识库模块

存储领域知识和最佳实践
"""

import json
import os
from typing import Dict, Any, List, Optional
from pathlib import Path
from ..utils.logger import Logger

logger = Logger.get_logger("knowledge_base")


class KnowledgeBase:
    """
    知识库
    
    存储 DevOps 领域知识和最佳实践
    """
    
    def __init__(self, storage_dir: str = "data/knowledge"):
        """
        初始化知识库
        
        Args:
            storage_dir: 存储目录
        """
        self.storage_dir = Path(storage_dir)
        self.storage_dir.mkdir(parents=True, exist_ok=True)
        
        # 创建子目录
        self.best_practices_dir = self.storage_dir / "best_practices"
        self.troubleshooting_dir = self.storage_dir / "troubleshooting"
        self.templates_dir = self.storage_dir / "templates"
        self.rules_dir = self.storage_dir / "rules"
        
        for dir_path in [self.best_practices_dir, self.troubleshooting_dir, 
                         self.templates_dir, self.rules_dir]:
            dir_path.mkdir(parents=True, exist_ok=True)
    
    def save_best_practice(self, name: str, content: Dict[str, Any]) -> None:
        """
        保存最佳实践
        
        Args:
            name: 名称
            content: 内容
        """
        file_path = self.best_practices_dir / f"{name}.json"
        
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(content, f, ensure_ascii=False, indent=2)
        
        logger.debug(f"保存最佳实践: {name}")
    
    def load_best_practice(self, name: str) -> Optional[Dict[str, Any]]:
        """
        加载最佳实践
        
        Args:
            name: 名称
            
        Returns:
            最佳实践内容，如果不存在则返回 None
        """
        file_path = self.best_practices_dir / f"{name}.json"
        
        if not file_path.exists():
            return None
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception as e:
            logger.error(f"加载最佳实践失败: {name}, 错误: {e}")
            return None
    
    def list_best_practices(self) -> List[str]:
        """
        列出所有最佳实践
        
        Returns:
            最佳实践名称列表
        """
        return [f.stem for f in self.best_practices_dir.glob("*.json")]
    
    def save_troubleshooting(self, name: str, content: Dict[str, Any]) -> None:
        """
        保存故障处理知识
        
        Args:
            name: 名称
            content: 内容
        """
        file_path = self.troubleshooting_dir / f"{name}.json"
        
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(content, f, ensure_ascii=False, indent=2)
        
        logger.debug(f"保存故障处理知识: {name}")
    
    def load_troubleshooting(self, name: str) -> Optional[Dict[str, Any]]:
        """
        加载故障处理知识
        
        Args:
            name: 名称
            
        Returns:
            故障处理知识内容，如果不存在则返回 None
        """
        file_path = self.troubleshooting_dir / f"{name}.json"
        
        if not file_path.exists():
            return None
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception as e:
            logger.error(f"加载故障处理知识失败: {name}, 错误: {e}")
            return None
    
    def search_troubleshooting(self, keywords: List[str]) -> List[Dict[str, Any]]:
        """
        搜索故障处理知识
        
        Args:
            keywords: 关键词列表
            
        Returns:
            匹配的故障处理知识列表
        """
        results = []
        
        for file_path in self.troubleshooting_dir.glob("*.json"):
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = json.load(f)
                    text = json.dumps(content).lower()
                    
                    # 简单的关键词匹配
                    if any(keyword.lower() in text for keyword in keywords):
                        results.append({
                            'name': file_path.stem,
                            'content': content,
                        })
            except Exception as e:
                logger.error(f"搜索故障处理知识失败: {file_path}, 错误: {e}")
        
        return results
    
    def save_template(self, name: str, content: Dict[str, Any]) -> None:
        """
        保存配置模板
        
        Args:
            name: 名称
            content: 内容
        """
        file_path = self.templates_dir / f"{name}.json"
        
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(content, f, ensure_ascii=False, indent=2)
        
        logger.debug(f"保存配置模板: {name}")
    
    def load_template(self, name: str) -> Optional[Dict[str, Any]]:
        """
        加载配置模板
        
        Args:
            name: 名称
            
        Returns:
            配置模板内容，如果不存在则返回 None
        """
        file_path = self.templates_dir / f"{name}.json"
        
        if not file_path.exists():
            return None
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception as e:
            logger.error(f"加载配置模板失败: {name}, 错误: {e}")
            return None
    
    def save_rule(self, name: str, content: Dict[str, Any]) -> None:
        """
        保存规则
        
        Args:
            name: 名称
            content: 内容
        """
        file_path = self.rules_dir / f"{name}.json"
        
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(content, f, ensure_ascii=False, indent=2)
        
        logger.debug(f"保存规则: {name}")
    
    def load_rule(self, name: str) -> Optional[Dict[str, Any]]:
        """
        加载规则
        
        Args:
            name: 名称
            
        Returns:
            规则内容，如果不存在则返回 None
        """
        file_path = self.rules_dir / f"{name}.json"
        
        if not file_path.exists():
            return None
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception as e:
            logger.error(f"加载规则失败: {name}, 错误: {e}")
            return None

