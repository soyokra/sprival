"""
工具管理器

管理工具生命周期，处理工具配置
"""

from typing import Dict, Any, Optional
from ..tools.base import Tool
from ..tools.registry import ToolRegistry
from ..utils.logger import Logger

logger = Logger.get_logger("tool_manager")


class ToolManager:
    """
    工具管理器
    
    管理工具生命周期，处理工具配置
    """
    
    def __init__(self, registry: ToolRegistry):
        """
        初始化工具管理器
        
        Args:
            registry: 工具注册表
        """
        self.registry = registry
        self._configs: Dict[str, Dict[str, Any]] = {}
        self._initialized: Dict[str, bool] = {}
    
    def initialize_tool(self, tool_name: str, config: Dict[str, Any]) -> bool:
        """
        初始化工具
        
        Args:
            tool_name: 工具名称
            config: 工具配置
            
        Returns:
            是否初始化成功
        """
        tool = self.registry.get(tool_name)
        if not tool:
            logger.error(f"工具不存在: {tool_name}")
            return False
        
        try:
            # 保存配置
            self._configs[tool_name] = config
            
            # 初始化工具
            success = tool.initialize(config)
            
            if success:
                self._initialized[tool_name] = True
                logger.info(f"工具初始化成功: {tool_name}")
            else:
                logger.error(f"工具初始化失败: {tool_name}")
            
            return success
        
        except Exception as e:
            logger.error(f"工具初始化异常: {tool_name}, 错误: {e}", exc_info=True)
            return False
    
    def get_config(self, tool_name: str) -> Optional[Dict[str, Any]]:
        """
        获取工具配置
        
        Args:
            tool_name: 工具名称
            
        Returns:
            工具配置，如果不存在则返回 None
        """
        return self._configs.get(tool_name)
    
    def update_config(self, tool_name: str, config: Dict[str, Any]) -> bool:
        """
        更新工具配置
        
        Args:
            tool_name: 工具名称
            config: 新配置
            
        Returns:
            是否更新成功
        """
        tool = self.registry.get(tool_name)
        if not tool:
            logger.error(f"工具不存在: {tool_name}")
            return False
        
        # 更新配置
        old_config = self._configs.get(tool_name, {})
        self._configs[tool_name] = {**old_config, **config}
        
        # 重新初始化工具
        return self.initialize_tool(tool_name, self._configs[tool_name])
    
    def is_initialized(self, tool_name: str) -> bool:
        """
        检查工具是否已初始化
        
        Args:
            tool_name: 工具名称
            
        Returns:
            是否已初始化
        """
        return self._initialized.get(tool_name, False)
    
    def health_check(self, tool_name: str) -> bool:
        """
        工具健康检查
        
        Args:
            tool_name: 工具名称
            
        Returns:
            是否健康
        """
        tool = self.registry.get(tool_name)
        if not tool:
            return False
        
        # 简单的健康检查：工具是否存在且已初始化
        return self.is_initialized(tool_name)
    
    def cleanup_tool(self, tool_name: str) -> None:
        """
        清理工具资源
        
        Args:
            tool_name: 工具名称
        """
        tool = self.registry.get(tool_name)
        if tool:
            try:
                tool.cleanup()
                logger.info(f"工具资源清理完成: {tool_name}")
            except Exception as e:
                logger.error(f"工具资源清理失败: {tool_name}, 错误: {e}", exc_info=True)
        
        # 清除状态
        if tool_name in self._initialized:
            del self._initialized[tool_name]
    
    def cleanup_all(self) -> None:
        """清理所有工具资源"""
        for tool_name in list(self._initialized.keys()):
            self.cleanup_tool(tool_name)

