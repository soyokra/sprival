# API Test Framework

一个功能完善的 Python REST API 接口测试框架，支持复杂场景并发测试、灵活的数据 Mock 和多格式报告输出。

## ✨ 特性

- 🚀 **智能数据 Mock**: 支持字符串、数字、日期、复杂对象等多种数据类型生成
- 🔥 **复杂场景模拟**: 支持恒定、渐进式、峰值、波浪式等多种负载模式
- 📊 **多格式报告**: 支持控制台、JSON、HTML 等多种报告格式
- ⚙️ **灵活配置**: JSON 配置文件 + 命令行参数，优先级可控
- ⚡ **快速执行**: 预定义常用测试场景，一键执行
- 📈 **实时监控**: 执行过程中实时输出 QPS、成功率、响应时间
- 🎯 **易于扩展**: 模块化设计，方便添加自定义功能
- 🛡️ **可靠稳定**: 完善的错误处理和资源管理

## 🚀 快速开始

### 安装

```bash
# 进入项目目录
cd api-test

# 安装依赖
pip install -r requirements.txt

# 或者安装整个框架
pip install -e .
```

### 基本使用

#### 方式一：使用配置文件

```bash
python main.py --config configs/default.json
```

#### 方式二：命令行参数

```bash
python main.py \
  --url https://api.example.com \
  --endpoint /api/test \
  --scenario constant \
  --threads 10 \
  --duration 60 \
  --reports console,json,html
```

#### 方式三：快速命令

```bash
# 冒烟测试
python main.py quick smoke_test --url https://your-api.com --endpoint /health

# 压力测试
python main.py quick stress_test --url https://your-api.com --endpoint /api/users

# 稳定性测试
python main.py quick stability_test --url https://your-api.com --endpoint /api/orders

# 峰值测试
python main.py quick spike_test --url https://your-api.com --endpoint /api/products
```

## 📦 架构设计

```
api-test/
├── src/
│   ├── mock_data/        # Mock数据生成模块
│   ├── scenarios/        # 并发场景模块
│   ├── api/              # 接口封装模块
│   ├── executor/         # 测试执行引擎
│   ├── reporters/        # 报告生成模块
│   ├── config/           # 配置管理
│   └── utils/            # 工具类
├── tests/                # 测试用例示例
├── configs/              # 配置文件目录
├── docs/                 # 文档
├── reports/              # 测试报告输出
└── main.py               # 命令行入口
```

## 📖 核心功能

### 1. Mock 数据生成

提供强大的数据生成功能：

```python
from src.mock_data.factory import DataFactory

# 字符串
DataFactory.string(length=10)       # 随机字符串
DataFactory.email()                 # 邮箱
DataFactory.url()                   # URL
DataFactory.username()              # 用户名

# 数字
DataFactory.integer(1, 100)         # 整数
DataFactory.float(0.0, 1.0)         # 浮点数
DataFactory.price()                 # 价格

# 日期时间
DataFactory.timestamp()             # 时间戳
DataFactory.date()                  # 日期
DataFactory.datetime()              # 日期时间

# 复杂对象
DataFactory.object(template)        # 从模板生成对象
DataFactory.array(item_template)    # 生成数组
```

### 2. 负载场景

支持多种负载模式：

| 场景类型 | 描述 | 适用场景 |
|---------|------|---------|
| **Constant** | 恒定负载 | 基础性能测试 |
| **Ramp Up** | 渐进式压测 | 容量规划、寻找性能拐点 |
| **Spike** | 峰值冲击 | 突发流量测试 |
| **Wave** | 波浪式负载 | 周期性负载变化 |

### 3. 报告系统

#### 控制台报告
- 实时彩色输出
- 性能指标实时监控
- 进度条显示

#### JSON 报告
- 机器可读格式
- 便于集成到 CI/CD
- 支持数据分析

#### HTML 报告
- 美观的可视化界面
- 详细的性能统计
- 错误分析

## 🔧 配置示例

### 基础配置

```json
{
  "test_name": "API Performance Test",
  "http": {
    "base_url": "https://api.example.com",
    "timeout": 30
  },
  "scenario": {
    "type": "constant",
    "threads": 10,
    "duration": 60
  },
  "api": {
    "endpoint": "/api/users",
    "method": "GET"
  }
}
```

### 使用环境变量占位符

在项目根目录的 `.env` 文件中声明服务地址：

```bash
sprival.url=https://api.example.com
```

然后在配置文件中引用占位符：

```json
{
  "http": {
    "base_url": "${sprival.url}",
    "timeout": 30
  }
}
```

当占位符无法解析时，加载过程会提示缺失的环境变量；仍可继续使用直接写入域名的方式。

### 使用 Mock 数据

```json
{
  "api": {
    "endpoint": "/api/users",
    "method": "POST",
    "body_template": {
      "username": "${mock:username}",
      "email": "${mock:email}",
      "age": "${mock:int:18:60}",
      "created_at": "${mock:iso8601}"
    }
  }
}
```

### 渐进式压测

```json
{
  "scenario": {
    "type": "ramp_up",
    "target_threads": 100,
    "ramp_duration": 60,
    "hold_duration": 120,
    "initial_threads": 1
  }
}
```

## 📚 文档

- [快速开始指南](docs/QUICK_START.md) - 5分钟上手
- [API 参考](docs/API_REFERENCE.md) - 完整的 API 文档
- [配置示例](configs/) - 各种场景的配置示例
- [测试示例](tests/) - 可运行的测试示例
- [更新日志](docs/CHANGELOG.md) - 版本更新记录

## 🎯 使用场景

- ✅ **接口功能测试**: 验证 API 功能正确性
- ✅ **性能测试**: 测试 API 性能指标（QPS、响应时间）
- ✅ **压力测试**: 找到系统性能瓶颈
- ✅ **稳定性测试**: 长时间运行测试系统稳定性
- ✅ **容量规划**: 确定系统容量上限

## 🛠️ 技术栈

- **Python 3.8+**: 核心语言
- **requests**: HTTP 客户端
- **jinja2**: HTML 模板引擎
- **jsonschema**: 配置验证
- **colorama**: 彩色输出
- **numpy**: 统计计算

## 🤝 贡献指南

欢迎贡献代码、报告问题或提出建议！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

---

如有问题或建议，欢迎提 [Issue](../../issues) 或联系维护者。

