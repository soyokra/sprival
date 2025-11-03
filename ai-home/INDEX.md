# AI 工作区索引

> Sprival 项目 AI 专用工作区  
> 用于维护项目索引、参考信息和辅助工具

## 📚 索引文档

### 核心索引
| 文档 | 说明 | 行数 |
|------|------|------|
| [FILE-INDEX.md](FILE-INDEX.md) | 文件快速定位索引 ⭐ | ~140 |
| [TECH-STACK.md](TECH-STACK.md) | 技术栈和架构概览 ⭐ | ~200 |
| [QUICK-REFERENCE.md](QUICK-REFERENCE.md) | 快速参考手册 ⭐ | ~140 |

### 参考文档
| 文档 | 说明 | 行数 |
|------|------|------|
| [TEST-FRAMEWORK-GUIDE.md](TEST-FRAMEWORK-GUIDE.md) | 测试框架开发指南 ⭐ | ~600 |
| [DEVELOPMENT-CHECKLIST.md](DEVELOPMENT-CHECKLIST.md) | 开发检查清单 | ~300 |
| [SYSTEM-ENVIRONMENT.md](SYSTEM-ENVIRONMENT.md) | 系统环境配置 | ~190 |
| [ENCODING-STANDARDS.md](ENCODING-STANDARDS.md) | 编码标准规范 | ~336 |

## 🎯 使用指南

### 快速开始
1. **了解项目架构**: 阅读 [TECH-STACK.md](TECH-STACK.md)
2. **定位文件**: 使用 [FILE-INDEX.md](FILE-INDEX.md)
3. **查询配置**: 参考 [QUICK-REFERENCE.md](QUICK-REFERENCE.md)

### 开发流程
1. **配置环境**: 参考 [SYSTEM-ENVIRONMENT.md](SYSTEM-ENVIRONMENT.md)
2. **遵循规范**: 参考 [ENCODING-STANDARDS.md](ENCODING-STANDARDS.md)
3. **编写测试**: 参考 [TEST-FRAMEWORK-GUIDE.md](TEST-FRAMEWORK-GUIDE.md)
4. **完成检查**: 使用 [DEVELOPMENT-CHECKLIST.md](DEVELOPMENT-CHECKLIST.md)

## 📖 项目文档体系

### AI 工作区（本目录）
```
ai-home/
├── INDEX.md                    # 总索引（本文档）⭐
├── FILE-INDEX.md              # 文件快速定位 ⭐
├── TECH-STACK.md              # 技术栈概览 ⭐
├── QUICK-REFERENCE.md         # 快速参考手册 ⭐
├── TEST-FRAMEWORK-GUIDE.md    # 测试框架开发指南 ⭐
├── DEVELOPMENT-CHECKLIST.md   # 开发检查清单
├── SYSTEM-ENVIRONMENT.md      # 系统环境配置
└── ENCODING-STANDARDS.md      # 编码标准
```

### 项目主文档
```
docs/
├── reference/                 # 参考文档
│   ├── README.md              # 文档索引入口
│   ├── components/            # 组件集成指南
│   ├── logging/               # 日志系统指南
│   ├── monitoring/            # 监控系统指南
│   └── DIRECTORY-STRUCTURE.md # 目录结构规范
└── api/                       # API 文档
    └── README.md              # API 文档入口
```

## 🔍 常见查询

### 我需要...
- **查找某个文件在哪里** → [FILE-INDEX.md](FILE-INDEX.md)
- **了解使用了哪些技术** → [TECH-STACK.md](TECH-STACK.md)
- **查询组件连接信息** → [QUICK-REFERENCE.md](QUICK-REFERENCE.md)
- **编写测试代码** → [TEST-FRAMEWORK-GUIDE.md](TEST-FRAMEWORK-GUIDE.md)
- **配置开发环境** → [SYSTEM-ENVIRONMENT.md](SYSTEM-ENVIRONMENT.md)
- **了解编码规范** → [ENCODING-STANDARDS.md](ENCODING-STANDARDS.md)
- **开发前的检查项** → [DEVELOPMENT-CHECKLIST.md](DEVELOPMENT-CHECKLIST.md)

### 我想了解...
- **项目结构规范** → `docs/reference/DIRECTORY-STRUCTURE.md`
- **日志系统如何工作** → `docs/reference/logging/README.md`
- **监控系统如何配置** → `docs/reference/monitoring/README.md`
- **MySQL 如何集成** → `docs/reference/components/mysql/README.md`
- **Redis 如何使用** → `docs/reference/components/redis/README.md`
- **Kafka 如何配置** → `docs/reference/components/kafka/README.md`

## 📝 文档维护原则

### ai-home 目录的定位
- ✅ **索引和参考**: 为项目建立索引，帮助快速定位和理解
- ✅ **总结和提炼**: 对项目文档进行索引和总结，不重复编写
- ✅ **辅助工具**: 存放辅助开发的脚本和工具
- ❌ **不是文档库**: 项目完整文档在 `docs/` 目录
- ❌ **不是变更记录**: 不保存完成记录、修复记录等临时文档
- ❌ **不是总结报告**: 不创建总结性、汇报性文档

### 文档更新规则
1. **索引为主**: 维护对项目文档的索引，而非复制内容
2. **精简为要**: 保持文档简洁，控制在合理长度
3. **及时更新**: 项目结构变化时同步更新索引
4. **避免重复**: 不与项目主文档重复

## 🎓 相关规范

### 全局开发规范
- 参见项目根目录 `.cursor/rules/ai-team.mdc`
- 包含代码质量、Spring Boot 规范、文档撰写规范等

### 项目特定规范
- 参见 `docs/reference/DIRECTORY-STRUCTURE.md`
- 参见各组件的 README 文档

## 📊 改造记录

### 2025-10-22 改造
- ✅ 删除 13 个违反规范的文档（变更记录、修复记录、总结文档等）
- ✅ 精简 FILE-INDEX.md（285行 → 140行）
- ✅ 精简并重命名 PROJECT-OVERVIEW.md 为 TECH-STACK.md（475行 → 200行）
- ✅ 精简 QUICK-REFERENCE.md（375行 → 140行）
- ✅ 合并并精简 SYSTEM-ENVIRONMENT（两个文件 → 一个文件，190行）
- ✅ 创建 INDEX.md 作为总索引入口

### 改造目标达成
- 文档总数：从 20 个减少到 7 个（减少 65%）
- 核心索引：从 1135 行减少到约 480 行（减少 58%）
- 定位清晰：明确 ai-home 作为索引和参考的定位
- 结构合理：建立清晰的导航和查询体系

---

**最后更新**: 2025-10-25  
**用途**: AI 开发时的项目导航和快速参考

