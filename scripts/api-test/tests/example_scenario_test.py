"""
场景测试示例

演示如何使用不同的负载场景进行测试
"""

import sys
import os

# 添加父目录到路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from src.scenarios.load_patterns import ConstantLoadScenario, RampUpScenario
from src.api.api_base import APIBase


def create_simple_task():
    """创建简单的测试任务"""
    api = APIBase(base_url="https://jsonplaceholder.typicode.com")
    
    def task():
        """执行单个请求"""
        try:
            response = api.get("/posts/1")
            return response.response
        except Exception as e:
            print(f"请求失败: {str(e)}")
            raise
    
    return task


def test_constant_load():
    """测试恒定负载场景"""
    print("\n=== 测试恒定负载场景 ===\n")
    
    # 创建场景
    scenario = ConstantLoadScenario(
        name="Constant Load Test",
        threads=5,
        duration=10  # 运行 10 秒
    )
    
    # 设置任务
    scenario.set_task(create_simple_task())
    
    # 执行场景
    metrics = scenario.execute()
    
    # 打印结果
    print("\n" + scenario.metrics.get_summary())
    
    print("\n✓ 恒定负载场景测试完成")


def test_ramp_up():
    """测试渐进式压测场景"""
    print("\n=== 测试渐进式压测场景 ===\n")
    
    # 创建场景
    scenario = RampUpScenario(
        name="Ramp Up Test",
        target_threads=10,
        ramp_duration=10,  # 10 秒渐进到目标
        hold_duration=10,  # 保持 10 秒
        initial_threads=2,
        ramp_steps=3
    )
    
    # 设置任务
    scenario.set_task(create_simple_task())
    
    # 执行场景
    metrics = scenario.execute()
    
    # 打印结果
    print("\n" + scenario.metrics.get_summary())
    
    print("\n✓ 渐进式压测场景测试完成")


if __name__ == '__main__':
    try:
        # 注意：这些测试会实际发送网络请求
        print("警告：这些测试会发送实际的网络请求到 jsonplaceholder.typicode.com")
        print("如果不想执行，请按 Ctrl+C 退出\n")
        
        import time
        time.sleep(2)
        
        test_constant_load()
        test_ramp_up()
        
        print("\n" + "=" * 50)
        print("所有场景测试完成")
        print("=" * 50)
        
    except KeyboardInterrupt:
        print("\n\n测试被用户中断")
    except Exception as e:
        print(f"\n✗ 测试失败: {str(e)}")
        import traceback
        traceback.print_exc()

