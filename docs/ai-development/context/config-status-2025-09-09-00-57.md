## 配置文件状态

### 核心配置文件
- **pom.xml**: Maven依赖管理，包含所有组件依赖
- **application.properties**: 应用配置，包含所有组件配置
- **redisson.yml**: Redisson配置
- **spy.properties**: P6Spy SQL监控配置

### Docker配置
- **dockers/docker-compose.yml**: 容器编排配置
- **dockers/*/Dockerfile**: 各组件容器化配置
- **dockers/*/volumes/**: 数据持久化目录

### 启动脚本
- **start-utf8.bat**: Windows UTF-8启动脚本
- **start-utf8.ps1**: PowerShell UTF-8启动脚本

### 文档结构
- **docs/**: 项目文档根目录
- **docs/ai-development/**: AI辅助开发文档
- **docs/spring-*/**: 各组件文档
- **docs/SYSTEM-ENVIRONMENT.md**: 系统环境配置
- **docs/ENCODING-STANDARDS.md**: 编码规范

