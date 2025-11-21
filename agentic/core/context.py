"""
执行上下文

在 core 模块中重新导出 ExecutionContext，避免循环导入
"""

from ..models.context import ExecutionContext

__all__ = ['ExecutionContext']

