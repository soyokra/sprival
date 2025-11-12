"""
基础测试示例

演示如何使用框架进行简单的 API 测试
"""

import sys
import os

# 添加父目录到路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from src.api.api_base import APIBase
from src.mock_data.factory import DataFactory


def test_simple_get_request():
    """测试简单的 GET 请求"""
    print("\n=== 测试简单的 GET 请求 ===")
    
    # 创建 API 客户端
    api = APIBase(base_url="https://jsonplaceholder.typicode.com")
    
    try:
        # 发送请求
        response = api.get("/posts/1")
        
        # 断言状态码
        response.assert_status_code(200)
        
        # 断言 JSON 结构
        response.assert_json_structure(["id", "userId", "title", "body"])
        
        # 打印响应
        print(f"状态码: {response.status_code}")
        print(f"响应时间: {response.elapsed_ms:.2f}ms")
        print(f"标题: {response.extract_field('title')}")
        
        print("✓ 测试通过")
        
    finally:
        api.close()


def test_post_request_with_mock_data():
    """测试使用 Mock 数据的 POST 请求"""
    print("\n=== 测试使用 Mock 数据的 POST 请求 ===")
    
    # 生成 Mock 数据
    mock_data = {
        "title": DataFactory.string(length=20),
        "body": DataFactory.string(length=100),
        "userId": DataFactory.integer(min_val=1, max_val=10)
    }
    
    print(f"Mock 数据: {mock_data}")
    
    # 创建 API 客户端
    api = APIBase(base_url="https://jsonplaceholder.typicode.com")
    
    try:
        # 发送请求
        response = api.post("/posts", json=mock_data)
        
        # 断言成功
        response.assert_success()
        
        # 打印响应
        print(f"状态码: {response.status_code}")
        print(f"响应时间: {response.elapsed_ms:.2f}ms")
        print(f"创建的 ID: {response.extract_field('id')}")
        
        print("✓ 测试通过")
        
    finally:
        api.close()


def test_mock_data_generation():
    """测试 Mock 数据生成"""
    print("\n=== 测试 Mock 数据生成 ===")
    
    # 字符串生成
    print(f"随机字符串: {DataFactory.string(length=10)}")
    print(f"邮箱: {DataFactory.email()}")
    print(f"URL: {DataFactory.url()}")
    print(f"手机号: {DataFactory.phone()}")
    print(f"UUID: {DataFactory.uuid()}")
    print(f"用户名: {DataFactory.username()}")
    
    # 数值生成
    print(f"整数: {DataFactory.integer(min_val=1, max_val=100)}")
    print(f"浮点数: {DataFactory.float(min_val=0.0, max_val=1.0, precision=2)}")
    print(f"价格: {DataFactory.price()}")
    
    # 日期生成
    print(f"时间戳: {DataFactory.timestamp()}")
    print(f"日期: {DataFactory.date()}")
    print(f"日期时间: {DataFactory.datetime()}")
    
    # 对象生成
    user_template = {
        "name": "${mock:username}",
        "email": "${mock:email}",
        "age": "${mock:int:18:60}",
        "phone": "${mock:phone}",
        "created_at": "${mock:iso8601}"
    }
    
    user_data = DataFactory.object(user_template)
    print(f"\n生成的用户对象:")
    for key, value in user_data.items():
        print(f"  {key}: {value}")
    
    print("\n✓ Mock 数据生成成功")


if __name__ == '__main__':
    try:
        test_simple_get_request()
        test_post_request_with_mock_data()
        test_mock_data_generation()
        
        print("\n" + "=" * 50)
        print("所有测试完成")
        print("=" * 50)
        
    except Exception as e:
        print(f"\n✗ 测试失败: {str(e)}")
        import traceback
        traceback.print_exc()

