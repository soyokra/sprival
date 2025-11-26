"""
智能体测试

测试智能体的基本功能
"""

import sys
import os
import unittest

# 添加当前目录到路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from core.agent import Agent
from tools.registry import ToolRegistry
from tools.builtin.echo_tool import EchoTool
from models.execution_result import ExecutionStatus


class TestAgent(unittest.TestCase):
    """智能体测试类"""
    
    def setUp(self):
        """测试前准备"""
        self.registry = ToolRegistry()
        echo_tool = EchoTool()
        self.registry.register(echo_tool, category="test")
        self.agent = Agent(tool_registry=self.registry)
    
    def test_execute_simple_task(self):
        """测试执行简单任务"""
        result = self.agent.execute(
            goal="测试 Echo 工具",
            context={"message": "Hello, Test!"}
        )
        
        self.assertIsNotNone(result)
        self.assertEqual(result.goal, "测试 Echo 工具")
        self.assertIn(result.status, [ExecutionStatus.SUCCESS, ExecutionStatus.FAILED])
    
    def test_plan_generation(self):
        """测试计划生成"""
        plan = self.agent.plan(
            goal="测试计划生成",
            context={"test": True}
        )
        
        self.assertIsNotNone(plan)
        self.assertEqual(plan.goal, "测试计划生成")
        self.assertGreater(len(plan.steps), 0)
    
    def test_tool_registry(self):
        """测试工具注册表"""
        tools = self.registry.list_tools()
        self.assertIn("echo", tools)
        
        tool_info = self.registry.get_tool_info("echo")
        self.assertIsNotNone(tool_info)
        self.assertEqual(tool_info["name"], "echo")


if __name__ == "__main__":
    unittest.main()

