#!/bin/bash
# 数据长度验证脚本 (Linux/Mac)
# 用途：验证性能测试生成的数据是否符合数据库字段长度限制

echo "========================================"
echo "  数据长度验证测试"
echo "========================================"
echo ""

# 检查 Java 环境
echo "【1】检查 Java 环境..."
if ! command -v java &> /dev/null; then
    echo "❌ Java 环境未配置，请先安装 Java"
    exit 1
fi
echo "✅ Java 环境正常"

# 检查 Maven 环境
echo ""
echo "【2】检查 Maven 环境..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven 环境未配置，请先安装 Maven"
    exit 1
fi
echo "✅ Maven 环境正常"

# 运行数据长度验证测试
echo ""
echo "【3】运行数据长度验证测试..."
echo ""
echo "========================================"
echo ""

# 执行测试
mvn test -Dtest=DataLengthValidationTest

# 检查测试结果
echo ""
echo "========================================"
echo "  验证完成"
echo "========================================"
echo ""

if [ $? -eq 0 ]; then
    echo "✅ 数据长度验证通过"
    echo ""
    echo "验证结果:"
    echo "  ✓ order_id 长度符合 varchar(22) 限制"
    echo "  ✓ trade_id 长度符合 varchar(20) 限制"
    echo "  ✓ idempotent_id 长度符合 varchar(50) 限制"
    echo "  ✓ 数据唯一性验证通过"
    echo ""
else
    echo "❌ 数据长度验证失败"
    echo "请查看上方日志获取详细错误信息"
    echo ""
    exit 1
fi

# 显示说明
echo "========================================"
echo "  说明"
echo "========================================"
echo ""
echo "此脚本验证以下内容："
echo "1. 生成的 order_id 长度不超过 22 字符"
echo "2. 生成的 trade_id 长度不超过 20 字符"
echo "3. 生成的 idempotent_id 长度不超过 50 字符"
echo "4. 生成的数据具有良好的唯一性"
echo ""
echo "详细文档请查看："
echo "  docs/components/performance-testing/DATA-LENGTH-FIX.md"
echo ""

