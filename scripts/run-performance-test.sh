#!/bin/bash
# 性能测试运行脚本 (Linux/Mac)
# 用途：快速运行性能测试

# 参数配置
TEST_CLASS=${1:-"OrderInsertLoadTest"}
TEST_METHOD=${2:-""}
CONCURRENT_USERS=${3:-100}
DURATION_SECONDS=${4:-60}

echo "========================================"
echo "  Sprival 性能测试运行脚本"
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

# 清理旧的测试报告
echo ""
echo "【3】清理旧的测试报告..."
if [ -d "target/performance-reports" ]; then
    file_count=$(ls -1 target/performance-reports 2>/dev/null | wc -l)
    if [ $file_count -gt 0 ]; then
        echo "清理 $file_count 个旧报告文件..."
        rm -f target/performance-reports/*
    fi
fi

# 设置性能测试参数
echo ""
echo "【4】配置性能测试参数..."
echo "  测试类: $TEST_CLASS"
echo "  并发用户数: $CONCURRENT_USERS"
echo "  持续时间: $DURATION_SECONDS 秒"

# 构建测试命令
TEST_COMMAND="mvn clean test"
if [ -n "$TEST_METHOD" ]; then
    TEST_COMMAND="$TEST_COMMAND -Dtest=$TEST_CLASS#$TEST_METHOD"
    echo "  测试方法: $TEST_METHOD"
else
    TEST_COMMAND="$TEST_COMMAND -Dtest=$TEST_CLASS"
fi

# 添加系统属性
TEST_COMMAND="$TEST_COMMAND -Dperformance.test.concurrent-users=$CONCURRENT_USERS"
TEST_COMMAND="$TEST_COMMAND -Dperformance.test.duration-seconds=$DURATION_SECONDS"
TEST_COMMAND="$TEST_COMMAND -Dspring.profiles.active=performance"

# 启动性能测试
echo ""
echo "【5】启动性能测试..."
echo "执行命令: $TEST_COMMAND"
echo ""
echo "========================================"
echo ""

# 执行测试
eval $TEST_COMMAND
EXIT_CODE=$?

# 检查测试结果
echo ""
echo "========================================"
echo "  测试完成"
echo "========================================"
echo ""

if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ 性能测试执行成功"
    
    # 查看测试报告
    if [ -d "target/performance-reports" ]; then
        report_count=$(ls -1 target/performance-reports 2>/dev/null | wc -l)
        if [ $report_count -gt 0 ]; then
            echo ""
            echo "📊 生成的测试报告:"
            ls -1t target/performance-reports | head -5 | while read report; do
                echo "  - $report"
            done
            
            # 显示最新报告的内容
            latest_report=$(ls -1t target/performance-reports | head -1)
            if [ -n "$latest_report" ]; then
                echo ""
                echo "📈 最新测试报告内容:"
                echo "----------------------------------------"
                cat "target/performance-reports/$latest_report"
                echo "----------------------------------------"
            fi
        fi
    fi
else
    echo "❌ 性能测试执行失败"
    echo "请查看上方日志获取详细错误信息"
fi

echo ""
echo "测试报告目录: target/performance-reports/"
echo ""

# 使用示例
echo "========================================"
echo "  使用示例"
echo "========================================"
echo ""
echo "# 运行所有下单接口压力测试"
echo "./scripts/run-performance-test.sh"
echo ""
echo "# 运行固定并发测试，100并发，持续60秒"
echo "./scripts/run-performance-test.sh OrderInsertLoadTest \"\" 100 60"
echo ""
echo "# 运行特定测试方法"
echo "./scripts/run-performance-test.sh OrderInsertLoadTest testOrderInsertWithFixedConcurrency 100 60"
echo ""
echo "# 运行其他测试类"
echo "./scripts/run-performance-test.sh YourLoadTest"
echo ""

