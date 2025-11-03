# 测试用例示例

这个目录包含了框架的示例测试用例，帮助你快速了解如何使用框架。

## 示例文件

### example_basic_test.py
基础测试示例，演示：
- 简单的 GET 请求
- 使用 Mock 数据的 POST 请求
- Mock 数据生成功能

运行示例：
```bash
python tests/example_basic_test.py
```

### example_scenario_test.py
场景测试示例，演示：
- 恒定负载场景
- 渐进式压测场景

运行示例：
```bash
python tests/example_scenario_test.py
```

⚠️ **注意**: 这些示例会发送实际的网络请求到 `https://jsonplaceholder.typicode.com`（一个免费的测试 API）。

## 编写自己的测试

### 1. 创建简单的 API 测试

```python
from src.api.api_base import APIBase

# 创建 API 客户端
api = APIBase(base_url="https://your-api.com")

# 发送请求并验证
response = api.get("/endpoint")
response.assert_status_code(200)
response.assert_json_structure(["id", "name"])

# 提取数据
user_id = response.extract_field("id")
```

### 2. 使用 Mock 数据

```python
from src.mock_data.factory import DataFactory

# 生成测试数据
data = {
    "username": DataFactory.username(),
    "email": DataFactory.email(),
    "age": DataFactory.integer(min_val=18, max_val=60)
}

# 发送请求
response = api.post("/users", json=data)
```

### 3. 使用配置文件执行测试

创建配置文件 `my_test.json`:
```json
{
  "test_name": "My API Test",
  "http": {
    "base_url": "https://your-api.com"
  },
  "scenario": {
    "type": "constant",
    "threads": 10,
    "duration": 60
  },
  "api": {
    "endpoint": "/api/test",
    "method": "GET"
  }
}
```

执行测试：
```bash
python main.py --config my_test.json
```

## 更多示例

查看 `configs/` 目录中的配置文件示例：
- `default.json` - 默认配置模板
- `ramp_up_example.json` - 渐进式压测示例
- `spike_example.json` - 峰值测试示例

