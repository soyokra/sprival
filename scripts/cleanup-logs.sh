#!/bin/bash

# Sprival日志清理脚本
# 用于清理旧的日志文件

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# 默认参数
RETAIN_DAYS=7
FORCE=false
VERBOSE=false

# 参数解析
while [[ $# -gt 0 ]]; do
    case $1 in
        -d|--days)
            RETAIN_DAYS="$2"
            shift 2
            ;;
        -f|--force)
            FORCE=true
            shift
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -h|--help)
            echo "用法: $0 [选项]"
            echo "选项:"
            echo "  -d, --days DAYS     保留天数 (默认: 7)"
            echo "  -f, --force         强制删除，不询问确认"
            echo "  -v, --verbose       详细输出"
            echo "  -h, --help          显示此帮助信息"
            exit 0
            ;;
        *)
            echo "未知参数: $1"
            exit 1
            ;;
    esac
done

echo -e "${GREEN}🧹 开始清理日志文件...${NC}"
echo -e "${CYAN}📅 保留天数: $RETAIN_DAYS${NC}"

LOGS_DIR="logs"
CUTOFF_DATE=$(date -d "-$RETAIN_DAYS days" +%Y%m%d)

if [ ! -d "$LOGS_DIR" ]; then
    echo -e "${YELLOW}⚠️ 日志目录不存在: $LOGS_DIR${NC}"
    exit 0
fi

# 获取所有日志文件
LOG_FILES=$(find "$LOGS_DIR" -name "*.log*" -type f)

if [ -z "$LOG_FILES" ]; then
    echo -e "${GREEN}✅ 没有找到需要清理的日志文件${NC}"
    exit 0
fi

# 计算文件数量
FILE_COUNT=$(echo "$LOG_FILES" | wc -l)
echo -e "${CYAN}📊 找到 $FILE_COUNT 个日志文件${NC}"

DELETED_COUNT=0
KEPT_COUNT=0
TOTAL_SIZE=0

# 处理每个日志文件
echo "$LOG_FILES" | while read -r log_file; do
    if [ -f "$log_file" ]; then
        # 获取文件大小
        FILE_SIZE=$(stat -c%s "$log_file" 2>/dev/null || echo "0")
        TOTAL_SIZE=$((TOTAL_SIZE + FILE_SIZE))
        
        # 获取文件修改时间
        FILE_DATE=$(stat -c%Y "$log_file" 2>/dev/null)
        if [ -n "$FILE_DATE" ]; then
            FILE_DATE_STR=$(date -d "@$FILE_DATE" +%Y%m%d)
            
            if [ "$FILE_DATE_STR" -lt "$CUTOFF_DATE" ]; then
                if [ "$VERBOSE" = true ]; then
                    FILE_SIZE_KB=$((FILE_SIZE / 1024))
                    FILE_MOD_TIME=$(date -d "@$FILE_DATE" '+%Y-%m-%d %H:%M:%S')
                    echo -e "${YELLOW}🗑️ 删除旧日志: $(basename "$log_file") (大小: ${FILE_SIZE_KB}KB, 修改时间: $FILE_MOD_TIME)${NC}"
                fi
                
                if [ "$FORCE" = true ] || [ "$VERBOSE" = true ]; then
                    rm -f "$log_file"
                    if [ $? -eq 0 ]; then
                        DELETED_COUNT=$((DELETED_COUNT + 1))
                    else
                        echo -e "${RED}❌ 删除失败: $(basename "$log_file")${NC}"
                    fi
                else
                    # 询问确认
                    read -p "删除文件 $(basename "$log_file")? (y/N): " -n 1 -r
                    echo
                    if [[ $REPLY =~ ^[Yy]$ ]]; then
                        rm -f "$log_file"
                        if [ $? -eq 0 ]; then
                            DELETED_COUNT=$((DELETED_COUNT + 1))
                        else
                            echo -e "${RED}❌ 删除失败: $(basename "$log_file")${NC}"
                        fi
                    else
                        KEPT_COUNT=$((KEPT_COUNT + 1))
                    fi
                fi
            else
                if [ "$VERBOSE" = true ]; then
                    FILE_SIZE_KB=$((FILE_SIZE / 1024))
                    FILE_MOD_TIME=$(date -d "@$FILE_DATE" '+%Y-%m-%d %H:%M:%S')
                    echo -e "${GREEN}✅ 保留日志: $(basename "$log_file") (大小: ${FILE_SIZE_KB}KB, 修改时间: $FILE_MOD_TIME)${NC}"
                fi
                KEPT_COUNT=$((KEPT_COUNT + 1))
            fi
        else
            echo -e "${YELLOW}⚠️ 无法获取文件修改时间: $(basename "$log_file")${NC}"
            KEPT_COUNT=$((KEPT_COUNT + 1))
        fi
    fi
done

# 重新计算统计信息（因为子shell中的变量不会影响父shell）
DELETED_COUNT=0
KEPT_COUNT=0
TOTAL_SIZE=0

for log_file in $LOG_FILES; do
    if [ -f "$log_file" ]; then
        FILE_SIZE=$(stat -c%s "$log_file" 2>/dev/null || echo "0")
        TOTAL_SIZE=$((TOTAL_SIZE + FILE_SIZE))
        KEPT_COUNT=$((KEPT_COUNT + 1))
    fi
done

# 计算删除的文件数
ORIGINAL_COUNT=$FILE_COUNT
DELETED_COUNT=$((ORIGINAL_COUNT - KEPT_COUNT))

# 输出清理结果
echo -e "\n${CYAN}📋 清理结果:${NC}"
echo -e "   ${WHITE}总文件数: $ORIGINAL_COUNT${NC}"
echo -e "   ${WHITE}删除文件: $DELETED_COUNT${NC}"
echo -e "   ${WHITE}保留文件: $KEPT_COUNT${NC}"

# 转换总大小为MB
TOTAL_SIZE_MB=$((TOTAL_SIZE / 1024 / 1024))
echo -e "   ${WHITE}总大小: ${TOTAL_SIZE_MB}MB${NC}"

if [ $DELETED_COUNT -gt 0 ]; then
    echo -e "\n${GREEN}🎉 日志清理完成！删除了 $DELETED_COUNT 个旧日志文件${NC}"
else
    echo -e "\n${GREEN}✅ 没有需要清理的旧日志文件${NC}"
fi

# 显示当前日志目录状态
echo -e "\n${CYAN}📁 当前日志目录状态:${NC}"
CURRENT_LOGS=$(find "$LOGS_DIR" -name "*.log*" -type f)
if [ -n "$CURRENT_LOGS" ]; then
    echo "$CURRENT_LOGS" | while read -r log; do
        if [ -f "$log" ]; then
            SIZE_KB=$(( $(stat -c%s "$log" 2>/dev/null || echo "0") / 1024 ))
            MOD_TIME=$(stat -c%y "$log" 2>/dev/null | cut -d' ' -f1,2 | cut -d'.' -f1)
            echo -e "   ${WHITE}- $(basename "$log"): ${SIZE_KB}KB (修改时间: $MOD_TIME)${NC}"
        fi
    done
else
    echo -e "   ${WHITE}- 没有日志文件${NC}"
fi
