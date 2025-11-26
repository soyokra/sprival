"""
工具注册表

管理所有可用工具，提供工具发现和查询
"""

from typing import Dict, List, Optional
from threading import RLock
from ..tools.base import Tool
from ..utils.logger import Logger

logger = Logger.get_logger("tool_registry")


class ToolRegistry:
    """
    工具注册表
    
    管理所有可用工具，提供工具发现和查询功能
    """
    
    def __init__(self):
        """初始化工具注册表"""
        self._tools: Dict[str, Tool] = {}
        self._categories: Dict[str, List[str]] = {}  # 分类 -> 工具名称列表
        self._lock = RLock()
    
    def register(self, tool: Tool, category: str = "general") -> bool:
        """
        注册工具
        
        Args:
            tool: 工具实例
            category: 工具分类
            
        Returns:
            是否注册成功
        """
        with self._lock:
            if tool.name in self._tools:
                logger.warning(f"工具已存在: {tool.name}，将覆盖")
            
            self._tools[tool.name] = tool
            
            if category not in self._categories:
                self._categories[category] = []
            
            if tool.name not in self._categories[category]:
                self._categories[category].append(tool.name)
            
            logger.info(f"注册工具: {tool.name} (分类: {category})")
            return True
    
    def unregister(self, tool_name: str) -> bool:
        """
        注销工具
        
        Args:
            tool_name: 工具名称
            
        Returns:
            是否注销成功
        """
        with self._lock:
            if tool_name not in self._tools:
                logger.warning(f"工具不存在: {tool_name}")
                return False
            
            del self._tools[tool_name]
            
            # 从分类中移除
            for category in self._categories:
                if tool_name in self._categories[category]:
                    self._categories[category].remove(tool_name)
            
            logger.info(f"注销工具: {tool_name}")
            return True
    
    def get(self, tool_name: str) -> Optional[Tool]:
        """
        获取工具
        
        Args:
            tool_name: 工具名称
            
        Returns:
            工具实例，如果不存在则返回 None
        """
        with self._lock:
            return self._tools.get(tool_name)
    
    def list_tools(self, category: Optional[str] = None) -> List[str]:
        """
        列出工具
        
        Args:
            category: 工具分类，如果为 None 则返回所有工具
            
        Returns:
            工具名称列表
        """
        with self._lock:
            if category:
                return self._categories.get(category, []).copy()
            return list(self._tools.keys())
    
    def list_categories(self) -> List[str]:
        """
        列出所有分类
        
        Returns:
            分类列表
        """
        with self._lock:
            return list(self._categories.keys())
    
    def get_tool_info(self, tool_name: str) -> Optional[Dict]:
        """
        获取工具信息
        
        Args:
            tool_name: 工具名称
            
        Returns:
            工具信息，如果不存在则返回 None
        """
        tool = self.get(tool_name)
        if not tool:
            return None
        
        return {
            'name': tool.name,
            'description': tool.description,
            'version': tool.version,
            'schema': tool.get_schema(),
        }
    
    def search_tools(self, keyword: str) -> List[str]:
        """
        搜索工具
        
        Args:
            keyword: 关键词
            
        Returns:
            匹配的工具名称列表
        """
        with self._lock:
            keyword_lower = keyword.lower()
            matches = []
            
            for tool_name, tool in self._tools.items():
                if (keyword_lower in tool_name.lower() or 
                    keyword_lower in tool.description.lower()):
                    matches.append(tool_name)
            
            return matches
    
    def get_tools_by_category(self, category: str) -> List[Tool]:
        """
        根据分类获取工具
        
        Args:
            category: 工具分类
            
        Returns:
            工具实例列表
        """
        with self._lock:
            tool_names = self._categories.get(category, [])
            return [self._tools[name] for name in tool_names if name in self._tools]

