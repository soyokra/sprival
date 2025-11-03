# 快速开始指南

本指南将帮助你快速上手 API Test Framework。

## 安装

### 1. 克隆或下载项目

```bash
cd api-test
```

### 2. 安装依赖

```bash
pip install -r requirements.txt
```

或者安装整个框架：

```bash
pip install -e .
```

## 基本使用

### 方式一：使用配置文件

1. 创建配置文件 `my_test.json`:

```json
{
  "test_name": "My First API Test",
  "http": {
    "base_url": "https://jsonplaceholder.typicode.com",
    "timeout": 30
  },
  "scenario": {
    "type": "constant",
    "threads": 5,
    "duration": 30
  },
  "api": {
    "endpoint": "/posts/1",
    "method": "GET"
  },
  "report": {
    "formats": ["console", "json", "html"]
  }
}
```

2. 执行测试:

```bash
python main.py --config my_test.json
```

### 方式二：使用命令行参数

```bash
python main.py \
  --url https://jsonplaceholder.typicode.com \
  --endpoint /posts/1 \
  --scenario constant \
  --threads 10 \
  --duration 60 \
  --reports console,json,html
```

### 方式三：使用快速命令

框架提供了预定义的测试场景，可以一键执行：

```bash
# 冒烟测试（快速验证）
python main.py quick smoke_test --url https://your-api.com --endpoint /health

# 压力测试
python main.py quick stress_test --url https://your-api.com --endpoint /api/users

# 稳定性测试（1小时）
python main.py quick stability_test --url https://your-api.com --endpoint /api/orders

# 峰值测试
python main.py quick spike_test --url https://your-api.com --endpoint /api/products

# 波浪测试
python main.py quick wave_test --url https://your-api.com --endpoint /api/test
```

## 核心概念

### 1. Mock 数据

框架提供强大的数据生成功能：

```json
{
  "api": {
    "body_template": {
      "username": "${mock:username}",
      "email": "${mock:email}",
      "age": "${mock:int:18:60}",
      "phone": "${mock:phone}",
      "created_at": "${mock:iso8601}"
    }
  }
}
```

支持的 Mock 类型：
- 字符串：`string:长度:字符集`, `email`, `url`, `phone`, `uuid`, `username`
- 数字：`int:最小值:最大值`, `float:最小值:最大值:精度`, `price`
- 日期：`timestamp`, `date:格式`, `datetime:格式`, `iso8601`
- 其他：`bool`, `choice:选项1:选项2:...`

### 2. 场景类型

#### Constant（恒定负载）
固定线程数持续运行：
```json
{
  "scenario": {
    "type": "constant",
    "threads": 10,
    "duration": 60
  }
}
```

#### Ramp Up（渐进式压测）
逐步增加负载：
```json
{
  "scenario": {
    "type": "ramp_up",
    "target_threads": 100,
    "ramp_duration": 60,
    "hold_duration": 120,
    "initial_threads": 1,
    "ramp_steps": 10
  }
}
```

#### Spike（峰值冲击）
瞬间达到峰值负载：
```json
{
  "scenario": {
    "type": "spike",
    "spike_threads": 200,
    "spike_duration": 10,
    "base_threads": 10,
    "warmup_duration": 30
  }
}
```

#### Wave（波浪式负载）
周期性变化的负载：
```json
{
  "scenario": {
    "type": "wave",
    "min_threads": 10,
    "max_threads": 100,
    "wave_period": 60,
    "total_duration": 300
  }
}
```

### 3. 报告格式

支持三种报告格式：
- **console**: 实时控制台输出，彩色显示
- **json**: 机器可读的 JSON 格式，便于集成
- **html**: 美观的 HTML 报告，类似 Allure 风格

报告保存在 `reports/` 目录下。

## 高级用法

### 自定义请求头

```json
{
  "http": {
    "default_headers": {
      "Authorization": "Bearer ${TOKEN}",
      "Content-Type": "application/json"
    }
  }
}
```

### 参数化请求

```json
{
  "api": {
    "endpoint": "/api/users",
    "method": "GET",
    "params": {
      "page": "${mock:int:1:10}",
      "size": 20
    }
  }
}
```

### 日志配置

```json
{
  "log": {
    "level": "DEBUG",
    "log_to_file": true,
    "log_to_console": true,
    "log_dir": "logs"
  }
}
```

## 示例测试

运行内置示例：

```bash
# 基础功能示例
python tests/example_basic_test.py

# 场景测试示例
python tests/example_scenario_test.py
```

## 下一步

- 查看 [配置参考](configs/) 了解完整的配置选项
- 查看 [示例配置](configs/ramp_up_example.json) 学习更多用法
- 查看 [测试示例](tests/) 了解如何编写自己的测试

## 常见问题

### Q: 如何测试需要认证的 API？

A: 在配置中添加认证头：
```json
{
  "http": {
    "default_headers": {
      "Authorization": "Bearer YOUR_TOKEN"
    }
  }
}
```

### Q: 如何测试 HTTPS API 且跳过证书验证？

A: 设置 `verify_ssl` 为 false：
```json
{
  "http": {
    "verify_ssl": false
  }
}
```

### Q: 如何只生成 JSON 报告？

A: 指定报告格式：
```json
{
  "report": {
    "formats": ["json"]
  }
}
```

或命令行：
```bash
python main.py --config test.json --reports json
```

