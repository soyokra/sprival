# API Test Framework 文档中心

欢迎使用 API Test Framework 文档中心！

## 📚 文档目录

### 入门指南
- [快速开始](QUICK_START.md) - 5分钟快速上手指南
- [主 README](../README.md) - 项目概览和基本介绍

### 参考文档
- [API 参考](API_REFERENCE.md) - 完整的 API 文档
- [配置示例](../configs/) - 各种场景的配置文件示例
- [测试示例](../tests/) - 可运行的测试用例示例

### 版本信息
- [更新日志](CHANGELOG.md) - 版本更新记录

## 🚀 从这里开始

### 新手入门

如果你是第一次使用这个框架：

1. 阅读 [快速开始指南](QUICK_START.md)
2. 运行示例测试：
   ```bash
   python tests/example_basic_test.py
   ```
3. 尝试快速命令：
   ```bash
   python main.py quick smoke_test --url https://jsonplaceholder.typicode.com --endpoint /posts/1
   ```

### 深入学习

当你掌握了基础用法：

1. 查看 [API 参考](API_REFERENCE.md) 了解所有可用功能
2. 研究 [配置示例](../configs/) 学习高级配置
3. 阅读源代码了解实现细节

## 💡 核心概念

### Mock 数据

框架提供强大的数据生成功能，支持模板语法：

```json
{
  "body_template": {
    "username": "${mock:username}",
    "email": "${mock:email}",
    "age": "${mock:int:18:60}"
  }
}
```

### 测试场景

4种负载模式满足不同测试需求：
- **Constant**: 恒定负载
- **Ramp Up**: 渐进式压测
- **Spike**: 峰值冲击
- **Wave**: 波浪式负载

### 报告系统

3种报告格式：
- **Console**: 实时彩色输出
- **JSON**: 机器可读格式
- **HTML**: 美观的可视化报告

## 🔗 相关链接

- [项目主页](../README.md)
- [示例代码](../tests/)
- [配置文件](../configs/)

## ❓ 常见问题

### 如何测试需要认证的 API？

在配置文件中添加认证头：
```json
{
  "http": {
    "default_headers": {
      "Authorization": "Bearer YOUR_TOKEN"
    }
  }
}
```

### 如何自定义报告格式？

可以扩展 `BaseReporter` 类：
```python
from src.reporters.base_reporter import BaseReporter

class CustomReporter(BaseReporter):
    def generate(self):
        # 自定义报告生成逻辑
        pass
```

### 如何集成到 CI/CD？

使用命令行模式，检查退出代码：
```bash
python main.py --config test.json && echo "测试通过" || echo "测试失败"
```

## 📝 反馈与支持

遇到问题或有建议？

- 查看 [常见问题](QUICK_START.md#常见问题)
- 提交 [Issue](../../issues)
- 查看 [API 参考](API_REFERENCE.md)

---

**Happy Testing! 🎉**

