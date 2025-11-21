# DevOps Agentic 智能体系统

智能化的 DevOps 自动化系统，提供自主决策、工具驱动、记忆持久化的智能代理能力。

## 核心特性

- **自主决策**：基于上下文和目标自主制定执行计划
- **工具驱动**：通过工具系统扩展能力，而非硬编码
- **记忆持久化**：保存执行历史和经验，支持长期学习
- **可观测性**：完整的执行链路追踪和决策过程记录
- **安全可控**：关键操作需要确认，支持人工介入

## 架构设计

### Agent Core（智能体核心）

- **Planner（规划器）**：理解任务目标，分解为可执行的步骤序列
- **Executor（执行器）**：按计划执行步骤，调用工具，处理结果
- **Evaluator（评估器）**：评估执行结果，判断是否达成目标

### Memory System（记忆系统）

- **Short-term Memory（短期记忆）**：保存当前任务执行过程中的上下文信息
- **Long-term Memory（长期记忆）**：持久化保存历史执行记录和经验
- **Knowledge Base（知识库）**：存储领域知识和最佳实践

### Tool System（工具系统）

- **Tool Registry（工具注册表）**：管理所有可用工具，提供工具发现和查询
- **Tool Executor（工具执行器）**：执行工具调用，处理工具结果
- **Tool Manager（工具管理器）**：管理工具生命周期，处理工具配置

## 快速开始

### 安装依赖

```bash
pip install -r requirements.txt
```

### 基本使用

```python
from agentic.core.agent import Agent
from agentic.tools.registry import ToolRegistry
from agentic.tools.builtin.echo_tool import EchoTool

# 创建工具注册表
registry = ToolRegistry()

# 注册工具
echo_tool = EchoTool()
registry.register(echo_tool, category="test")

# 创建智能体
agent = Agent(tool_registry=registry)

# 执行任务
result = agent.execute(
    goal="测试 Echo 工具",
    context={"message": "Hello, Agentic!"}
)

print(f"执行状态: {result.status.value}")
print(f"执行结果: {result.to_dict()}")
```

## 项目结构

```
agentic/
├── core/                      # 智能体核心
│   ├── agent.py              # Agent 主类
│   ├── planner.py            # 规划器
│   ├── executor.py           # 执行器
│   ├── evaluator.py          # 评估器
│   └── context.py            # 执行上下文
│
├── memory/                    # 记忆系统
│   ├── short_term.py         # 短期记忆
│   ├── long_term.py          # 长期记忆
│   └── knowledge_base.py     # 知识库
│
├── tools/                     # 工具系统
│   ├── registry.py           # 工具注册表
│   ├── executor.py           # 工具执行器
│   ├── manager.py            # 工具管理器
│   ├── base.py               # 工具基类
│   └── builtin/              # 内置工具
│       └── echo_tool.py
│
├── models/                    # 数据模型
│   ├── plan.py               # 计划模型
│   ├── execution_result.py   # 执行结果模型
│   └── context.py            # 上下文模型
│
└── utils/                     # 工具函数
    └── logger.py
```

## 开发工具

### 创建自定义工具

```python
from agentic.tools.base import Tool, ToolResult, ToolResultStatus

class MyTool(Tool):
    def __init__(self):
        super().__init__(
            name="my_tool",
            description="我的自定义工具",
            version="1.0.0"
        )
    
    def execute(self, params: dict) -> ToolResult:
        # 实现工具逻辑
        return ToolResult(
            success=True,
            status=ToolResultStatus.SUCCESS,
            data={"result": "success"}
        )
```

### 注册工具

```python
from agentic.tools.registry import ToolRegistry

registry = ToolRegistry()
my_tool = MyTool()
registry.register(my_tool, category="custom")
```

## 文档

详细的架构设计和使用文档请参考 [devops-agentic.plan.md](../devops-agentic.plan.md)。

## 许可证

与主项目保持一致。

