#!/bin/bash

# Sprival AI开发启动脚本
# 在AI编程前自动生成项目上下文

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# 参数解析
SKIP_CONTEXT=false
OPEN_CONTEXT=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-context)
            SKIP_CONTEXT=true
            shift
            ;;
        --open-context)
            OPEN_CONTEXT=true
            shift
            ;;
        -h|--help)
            echo "用法: $0 [选项]"
            echo "选项:"
            echo "  --skip-context    跳过上下文生成"
            echo "  --open-context    自动打开上下文文件"
            echo "  -h, --help        显示此帮助信息"
            exit 0
            ;;
        *)
            echo "未知参数: $1"
            exit 1
            ;;
    esac
done

echo -e "${GREEN}🚀 Sprival AI开发环境启动...${NC}"

# 1. 生成项目上下文（除非跳过）
if [ "$SKIP_CONTEXT" = false ]; then
    echo -e "${YELLOW}📋 生成项目上下文...${NC}"
    
    # 检查脚本是否存在
    CONTEXT_SCRIPT="./scripts/generate-project-context.sh"
    if [ -f "$CONTEXT_SCRIPT" ]; then
        chmod +x "$CONTEXT_SCRIPT"
        "$CONTEXT_SCRIPT"
        echo -e "${GREEN}✅ 项目上下文生成完成${NC}"
    else
        echo -e "${YELLOW}⚠️ 上下文生成脚本不存在，跳过上下文生成${NC}"
    fi
fi

# 1.5. 验证项目结构
echo -e "${YELLOW}🔍 验证项目结构...${NC}"
VALIDATE_SCRIPT="./scripts/validate-project-structure.sh"
if [ -f "$VALIDATE_SCRIPT" ]; then
    chmod +x "$VALIDATE_SCRIPT"
    "$VALIDATE_SCRIPT"
    echo -e "${GREEN}✅ 项目结构验证完成${NC}"
else
    echo -e "${YELLOW}⚠️ 结构验证脚本不存在，跳过结构验证${NC}"
fi

# 2. 显示项目状态
echo -e "\n${CYAN}📊 项目状态概览:${NC}"
echo -e "   ${WHITE}- 项目名称: Sprival${NC}"
echo -e "   ${WHITE}- 技术栈: Spring Boot 2.7.18 + Java 8${NC}"
echo -e "   ${WHITE}- 已完成组件: 8个 (HTTP Server, MySQL, Redis, ClickHouse, MongoDB, RabbitMQ, Kafka, HTTP Client)${NC}"
echo -e "   ${WHITE}- 编码格式: UTF-8${NC}"
echo -e "   ${WHITE}- 启动端口: 8338${NC}"

# 3. 显示上下文文件位置
echo -e "\n${CYAN}📁 上下文文件位置:${NC}"
CONTEXT_DIR="docs/ai-development/context"
if [ -d "$CONTEXT_DIR" ]; then
    find "$CONTEXT_DIR" -name "*-latest.*" -type f | while read -r file; do
        echo -e "   ${WHITE}- $file${NC}"
    done
else
    echo -e "   ${YELLOW}- 上下文目录不存在: $CONTEXT_DIR${NC}"
fi

# 4. 显示快速命令
echo -e "\n${CYAN}⚡ 快速命令:${NC}"
echo -e "   ${WHITE}- 启动应用: mvn spring-boot:run${NC}"
echo -e "   ${WHITE}- 启动应用(脚本): ./start-utf8.sh${NC}"
echo -e "   ${WHITE}- 健康检查: http://localhost:8338/api/actuator/health${NC}"
echo -e "   ${WHITE}- 监控指标: http://localhost:8338/api/actuator/metrics${NC}"
echo -e "   ${WHITE}- 重新生成上下文: ./scripts/generate-project-context.sh${NC}"
echo -e "   ${YELLOW}- **代码验证**: ./scripts/verify-code-changes.sh${NC}"
echo -e "   ${YELLOW}- **日志清理**: ./scripts/cleanup-logs.sh${NC}"

# 5. 显示AI编程指导
echo -e "\n${CYAN}🤖 AI编程指导:${NC}"
echo -e "   ${WHITE}1. 查看项目上下文: cat docs/ai-development/context/sprival-ai-context-latest.md${NC}"
echo -e "   ${WHITE}2. 查看组件状态: cat docs/ai-development/context/component-status-latest.md${NC}"
echo -e "   ${WHITE}3. 查看AI指导: cat docs/ai-development/context/ai-guidance-latest.md${NC}"
echo -e "   ${WHITE}4. 使用上下文模板: docs/ai-development/project-context-template.md${NC}"
echo -e "   ${WHITE}5. 查看开发规范: docs/ai-development/development-standards.md${NC}"
echo -e "   ${WHITE}6. 查看配置格式规范: docs/ai-development/configuration-format-standards.md${NC}"
echo -e "   ${WHITE}7. 查看项目结构: docs/PROJECT-STRUCTURE.md${NC}"
echo -e "   ${YELLOW}8. 查看验证规范: docs/ai-development/code-modification-verification.md${NC}"
echo -e "   ${YELLOW}9. 查看日志管理: docs/components/jetty/log-management.md${NC}"

# 6. 可选：打开上下文文件
if [ "$OPEN_CONTEXT" = true ]; then
    CONTEXT_FILE="docs/ai-development/context/sprival-ai-context-latest.md"
    if [ -f "$CONTEXT_FILE" ]; then
        echo -e "\n${YELLOW}📖 打开上下文文件...${NC}"
        if command -v code >/dev/null 2>&1; then
            code "$CONTEXT_FILE"
        elif command -v vim >/dev/null 2>&1; then
            vim "$CONTEXT_FILE"
        elif command -v nano >/dev/null 2>&1; then
            nano "$CONTEXT_FILE"
        else
            cat "$CONTEXT_FILE"
        fi
    else
        echo -e "${YELLOW}⚠️ 上下文文件不存在: $CONTEXT_FILE${NC}"
    fi
fi

# 7. 显示下一步建议
echo -e "\n${CYAN}🎯 下一步建议:${NC}"
echo -e "   ${WHITE}1. 查看项目上下文了解现状${NC}"
echo -e "   ${WHITE}2. 查看项目结构规范${NC}"
echo -e "   ${WHITE}3. 根据需求选择合适的组件${NC}"
echo -e "   ${WHITE}4. 使用标准模板与AI交互${NC}"
echo -e "   ${WHITE}5. 遵循项目开发规范${NC}"
echo -e "   ${WHITE}6. 运行结构验证确保规范性${NC}"
echo -e "   ${YELLOW}7. **重要**: 每次修改代码后运行验证脚本${NC}"

echo -e "\n${GREEN}✅ AI开发环境准备完成！${NC}"
echo -e "${YELLOW}💡 提示: 使用 --open-context 参数可以自动打开上下文文件${NC}"
