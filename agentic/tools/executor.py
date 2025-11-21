"""
工具执行器

执行工具调用，处理工具结果
"""

import time
from typing import Dict, Any, Optional, List
from datetime import datetime

from ..tools.base import Tool, ToolResult, ToolResultStatus
from ..tools.registry import ToolRegistry
from ..utils.logger import Logger

logger = Logger.get_logger("tool_executor")


class ToolExecutor:
    """
    工具执行器
    
    负责执行工具调用，处理工具结果
    """
    
    def __init__(self, registry: ToolRegistry, default_timeout: int = 300):
        """
        初始化工具执行器
        
        Args:
            registry: 工具注册表
            default_timeout: 默认超时时间（秒）
        """
        self.registry = registry
        self.default_timeout = default_timeout
    
    def execute(self, tool_name: str, params: Dict[str, Any], 
                timeout: Optional[int] = None) -> ToolResult:
        """
        执行工具
        
        Args:
            tool_name: 工具名称
            params: 工具参数
            timeout: 超时时间（秒），如果为 None 则使用默认值
            
        Returns:
            工具执行结果
        """
        tool = self.registry.get(tool_name)
        if not tool:
            return ToolResult(
                success=False,
                status=ToolResultStatus.FAILED,
                error=f"工具不存在: {tool_name}"
            )
        
        # 验证参数
        is_valid, error_msg = tool.validate_params(params)
        if not is_valid:
            return ToolResult(
                success=False,
                status=ToolResultStatus.FAILED,
                error=f"参数验证失败: {error_msg}"
            )
        
        # 执行工具
        timeout = timeout or self.default_timeout
        start_time = time.time()
        
        try:
            logger.info(f"执行工具: {tool_name}, 参数: {params}")
            
            # 这里可以添加超时控制（使用 signal 或 threading）
            result = tool.execute(params)
            
            duration = time.time() - start_time
            logger.info(f"工具执行完成: {tool_name}, 耗时: {duration:.2f}s, 成功: {result.success}")
            
            # 添加执行时间到元数据
            if result.metadata is None:
                result.metadata = {}
            result.metadata['duration'] = duration
            result.metadata['executed_at'] = datetime.now().isoformat()
            
            return result
        
        except Exception as e:
            duration = time.time() - start_time
            logger.error(f"工具执行失败: {tool_name}, 错误: {e}", exc_info=True)
            
            return ToolResult(
                success=False,
                status=ToolResultStatus.FAILED,
                error=f"执行异常: {str(e)}",
                metadata={
                    'duration': duration,
                    'executed_at': datetime.now().isoformat(),
                }
            )
    
    def execute_batch(self, tool_calls: List[Dict[str, Any]]) -> List[ToolResult]:
        """
        批量执行工具
        
        Args:
            tool_calls: 工具调用列表，每个元素包含 tool_name 和 params
            
        Returns:
            工具执行结果列表
        """
        results = []
        
        for tool_call in tool_calls:
            tool_name = tool_call.get('tool_name')
            params = tool_call.get('params', {})
            timeout = tool_call.get('timeout')
            
            result = self.execute(tool_name, params, timeout)
            results.append(result)
        
        return results

