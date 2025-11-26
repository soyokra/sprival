"""
Echo 工具

用于测试和调试的简单工具，返回输入参数
"""

from typing import Dict, Any
from ..base import Tool, ToolResult, ToolResultStatus


class EchoTool(Tool):
    """
    Echo 工具
    
    用于测试和调试，返回输入参数
    """
    
    def __init__(self):
        super().__init__(
            name="echo",
            description="回显输入参数，用于测试和调试",
            version="1.0.0"
        )
    
    def execute(self, params: Dict[str, Any]) -> ToolResult:
        """
        执行工具
        
        Args:
            params: 工具参数
            
        Returns:
            工具执行结果
        """
        return ToolResult(
            success=True,
            status=ToolResultStatus.SUCCESS,
            data={
                'echo': params,
                'message': 'Echo tool executed successfully'
            }
        )
    
    def validate_params(self, params: Dict[str, Any]) -> tuple[bool, str | None]:
        """验证参数"""
        # Echo 工具接受任何参数
        return True, None

