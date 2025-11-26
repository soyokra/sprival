"""
智能体使用示例

演示如何使用智能体执行任务
"""

import sys
import os

# 添加父目录到路径，以便导入 agentic 包
parent_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, parent_dir)

from agentic.core.agent import Agent
from agentic.tools.registry import ToolRegistry
from agentic.tools.builtin.echo_tool import EchoTool


def main():
    """主函数"""
    print("=" * 60)
    print("DevOps Agentic 智能体系统示例")
    print("=" * 60)
    
    # 1. 创建工具注册表
    print("\n1. 创建工具注册表...")
    registry = ToolRegistry()
    
    # 2. 注册工具
    print("2. 注册工具...")
    echo_tool = EchoTool()
    registry.register(echo_tool, category="test")
    print(f"   已注册工具: {echo_tool.name}")
    
    # 3. 创建智能体
    print("3. 创建智能体...")
    agent = Agent(tool_registry=registry)
    
    # 4. 执行任务
    print("\n4. 执行任务...")
    print("   目标: 测试 Echo 工具")
    print("   上下文: {'message': 'Hello, Agentic!'}")
    
    result = agent.execute(
        goal="测试 Echo 工具",
        context={
            "message": "Hello, Agentic!",
            "test": True
        }
    )
    
    # 5. 显示结果
    print("\n5. 执行结果:")
    print(f"   状态: {result.status.value}")
    print(f"   总步骤数: {result.get_total_steps()}")
    print(f"   成功步骤数: {result.get_success_count()}")
    print(f"   失败步骤数: {result.get_failed_count()}")
    print(f"   总耗时: {result.total_duration:.2f} 秒")
    
    if result.error:
        print(f"   错误: {result.error}")
    
    # 6. 显示步骤结果
    print("\n6. 步骤详情:")
    for step_result in result.step_results:
        print(f"   步骤 {step_result.step_id}:")
        print(f"     状态: {step_result.status.value}")
        print(f"     耗时: {step_result.duration:.2f} 秒")
        if step_result.result:
            print(f"     结果: {step_result.result}")
        if step_result.error:
            print(f"     错误: {step_result.error}")
    
    print("\n" + "=" * 60)
    print("示例执行完成！")
    print("=" * 60)


if __name__ == "__main__":
    main()

