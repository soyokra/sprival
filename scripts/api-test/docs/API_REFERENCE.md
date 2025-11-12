# API 参考文档

API Test Framework 的详细 API 参考。

## Mock 数据模块

### DataFactory

统一的数据生成入口。

#### 字符串生成

```python
from src.mock_data.factory import DataFactory

# 随机字符串
DataFactory.string(length=10, charset="alphanumeric")
# charset 可选值: alphanumeric, alpha, numeric, lowercase, uppercase, special, chinese

# 邮箱
DataFactory.email(domain="example.com")

# URL
DataFactory.url(protocol="https", domain=None, path=True)

# 手机号
DataFactory.phone(country_code="86", length=11)

# UUID
DataFactory.uuid()

# 用户名
DataFactory.username(min_length=4, max_length=16)
```

#### 数值生成

```python
# 整数
DataFactory.integer(min_val=0, max_val=100)

# 浮点数
DataFactory.float(min_val=0.0, max_val=100.0, precision=2)

# 价格
DataFactory.price(min_val=0.01, max_val=10000.0)

# 百分比
DataFactory.percentage(precision=2)

# 概率
DataFactory.probability(precision=2)
```

#### 日期时间生成

```python
# 时间戳
DataFactory.timestamp(milliseconds=False)

# 日期
DataFactory.date(date_format="%Y-%m-%d")

# 日期时间
DataFactory.datetime(date_format="%Y-%m-%d %H:%M:%S")

# 时间
DataFactory.time(date_format="%H:%M:%S")

# ISO8601 格式
DataFactory.iso8601()

# 随机日期
DataFactory.random_date(start_date=None, end_date=None, date_format="%Y-%m-%d")

# 过去的日期
DataFactory.past_date(days=30, date_format="%Y-%m-%d")

# 未来的日期
DataFactory.future_date(days=30, date_format="%Y-%m-%d")
```

#### 对象生成

```python
# 从模板生成对象
template = {
    "name": "${mock:username}",
    "email": "${mock:email}",
    "age": "${mock:int:18:60}"
}
DataFactory.object(template)

# 生成数组
DataFactory.array(item_template={"id": "${mock:int:1:100}"}, min_count=1, max_count=10)

# 嵌套对象
DataFactory.nested_object(depth=3, key_count=5)

# 分页响应
DataFactory.paginated_response(
    data_template={"id": "${mock:int}"}, 
    page=1, 
    page_size=20, 
    total=None
)
```

## API 模块

### APIBase

API 基类，提供常用的 HTTP 方法。

```python
from src.api.api_base import APIBase

# 创建客户端
api = APIBase(
    base_url="https://api.example.com",
    timeout=30,
    max_retries=3,
    default_headers={"Content-Type": "application/json"}
)

# GET 请求
response = api.get("/users", params={"page": 1})

# POST 请求
response = api.post("/users", json={"name": "John", "email": "john@example.com"})

# PUT 请求
response = api.put("/users/1", json={"name": "Jane"})

# DELETE 请求
response = api.delete("/users/1")

# PATCH 请求
response = api.patch("/users/1", json={"email": "new@example.com"})

# 关闭客户端
api.close()

# 使用上下文管理器
with APIBase(base_url="https://api.example.com") as api:
    response = api.get("/users")
```

### ResponseHandler

响应处理器，提供响应验证和数据提取。

```python
# 响应对象由 APIBase 返回
response = api.get("/users/1")

# 基本属性
response.status_code      # 状态码
response.headers          # 响应头
response.text             # 响应文本
response.json             # JSON 数据
response.elapsed_ms       # 响应时间（毫秒）

# 判断成功
response.is_success()     # 状态码 2xx

# 断言方法（支持链式调用）
response.assert_status_code(200)
response.assert_success()
response.assert_json_structure(["id", "name", "email"], strict=False)
response.assert_field_value("name", "John")
response.assert_field_type("age", int)

# 自定义断言
response.assert_custom(
    lambda r: r.extract_field("age") > 18,
    "年龄必须大于 18"
)

# 数据提取
user_id = response.extract_field("id")
user_name = response.extract_field("data.user.name")  # 支持嵌套
fields = response.extract_fields(["id", "name", "email"])

# 转换为字典
response_dict = response.to_dict()
```

### RequestBuilder

请求构建器，提供流式接口。

```python
from src.api.request_builder import RequestBuilder

# 构建请求
request = RequestBuilder.create() \
    .method("POST") \
    .endpoint("/users") \
    .header("Authorization", "Bearer token") \
    .headers({"Content-Type": "application/json"}) \
    .param("page", 1) \
    .params({"size": 20}) \
    .json_body({"name": "John"}) \
    .build()

# 从模板构建
request = RequestBuilder.create() \
    .method("POST") \
    .endpoint("/users") \
    .body_from_template({
        "username": "${mock:username}",
        "email": "${mock:email}"
    }) \
    .build()

# 使用请求
api = APIBase(base_url="https://api.example.com")
response = api.request_from_builder(RequestBuilder.create().endpoint("/test"))
```

## 场景模块

### ConstantLoadScenario

恒定负载场景。

```python
from src.scenarios.load_patterns import ConstantLoadScenario

scenario = ConstantLoadScenario(
    name="Constant Test",
    threads=10,
    duration=60,
    requests_per_thread=None  # None 表示持续运行
)

# 设置任务
scenario.set_task(lambda: api.get("/test"))

# 执行
metrics = scenario.execute()

# 获取指标
print(scenario.metrics.get_summary())
```

### RampUpScenario

渐进式压测场景。

```python
from src.scenarios.load_patterns import RampUpScenario

scenario = RampUpScenario(
    name="Ramp Up Test",
    target_threads=100,
    ramp_duration=60,
    hold_duration=120,
    initial_threads=1,
    ramp_steps=10
)

scenario.set_task(task_func)
metrics = scenario.execute()
```

### SpikeScenario

峰值冲击场景。

```python
from src.scenarios.load_patterns import SpikeScenario

scenario = SpikeScenario(
    name="Spike Test",
    spike_threads=200,
    spike_duration=10,
    base_threads=10,
    warmup_duration=30
)

scenario.set_task(task_func)
metrics = scenario.execute()
```

### WaveScenario

波浪式负载场景。

```python
from src.scenarios.load_patterns import WaveScenario

scenario = WaveScenario(
    name="Wave Test",
    min_threads=10,
    max_threads=100,
    wave_period=60,
    total_duration=300
)

scenario.set_task(task_func)
metrics = scenario.execute()
```

## 报告模块

### ReportAggregator

报告聚合器，统一生成多种格式报告。

```python
from src.reporters.report_aggregator import ReportAggregator

aggregator = ReportAggregator(report_dir="reports")

report_data = {
    'test_name': 'My Test',
    'metrics': metrics
}

# 生成所有格式报告
reports = aggregator.generate_reports(data=report_data)

# 生成指定格式
reports = aggregator.generate_reports(
    data=report_data,
    formats=["console", "html"]
)

# 获取特定报告器
console_reporter = aggregator.get_reporter("console")
console_reporter.print_live_metrics(metrics)
```

## 配置模块

### Settings

全局配置对象。

```python
from src.config.settings import Settings

# 创建默认配置
settings = Settings()

# 从字典创建
settings = Settings.from_dict({
    'test_name': 'My Test',
    'http': {
        'base_url': 'https://api.example.com'
    },
    'scenario': {
        'type': 'constant',
        'threads': 10,
        'duration': 60
    }
})

# 转换为字典
config_dict = settings.to_dict()

# 访问配置
print(settings.test_name)
print(settings.http.base_url)
print(settings.scenario.type)
```

### ConfigLoader

配置加载器。

```python
from src.executor.config_loader import ConfigLoader

loader = ConfigLoader()

# 从文件加载
settings = loader.load("configs/my_test.json")

# 从字典加载
settings = loader.load_from_dict(config_dict)

# 保存配置
loader.save(settings, "configs/saved_config.json")
```

## 工具模块

### Logger

日志管理器。

```python
from src.utils.logger import Logger

# 获取日志记录器
logger = Logger.get_logger(
    name="my_test",
    level=logging.INFO,
    log_to_file=True,
    log_to_console=True,
    log_dir="logs"
)

# 记录日志
logger.debug("调试信息")
logger.info("普通信息")
logger.warning("警告信息")
logger.error("错误信息")
logger.critical("严重错误")

# 设置日志级别
Logger.set_level("my_test", logging.DEBUG)
```

### Timer

计时器。

```python
from src.utils.timer import Timer, measure_time, timeit

# 使用计时器
timer = Timer()
timer.start()
# ... 执行操作 ...
elapsed = timer.stop()
print(f"耗时: {elapsed}秒")

# 使用上下文管理器
with measure_time("数据库查询"):
    # ... 执行操作 ...
    pass

# 使用装饰器
@timeit
def my_function():
    pass
```

### Validator

数据验证器。

```python
from src.utils.validator import Validator, ResponseValidator

# 通用验证
Validator.not_none(value, "字段名")
Validator.not_empty(value, "字段名")
Validator.in_range(10, min_val=1, max_val=100)
Validator.length_in_range("text", min_length=1, max_length=10)
Validator.matches_pattern(value, r"^\d+$")
Validator.is_email("test@example.com")
Validator.is_url("https://example.com")
Validator.is_type(value, str)
Validator.in_choices(value, ["option1", "option2"])

# 响应验证
ResponseValidator.assert_status_code(response, 200)
ResponseValidator.assert_json_structure(data, ["id", "name"], strict=False)
ResponseValidator.assert_field_value(data, "name", "John")
ResponseValidator.assert_field_type(data, "age", int)
```

