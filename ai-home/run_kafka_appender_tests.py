#!/usr/bin/env python3
"""
KafkaAppender 测试运行脚本
"""

import subprocess
import sys
import os
from pathlib import Path

def run_command(cmd, description):
    """运行命令并显示结果"""
    print(f"\n{'='*60}")
    print(f"运行: {description}")
    print(f"命令: {cmd}")
    print(f"{'='*60}")
    
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True, encoding='utf-8')
        print(f"返回码: {result.returncode}")
        
        if result.stdout:
            print("标准输出:")
            print(result.stdout)
        
        if result.stderr:
            print("错误输出:")
            print(result.stderr)
        
        return result.returncode == 0
    except Exception as e:
        print(f"执行命令时出错: {e}")
        return False

def main():
    """主函数"""
    print("KafkaAppender 测试套件")
    print("=" * 60)
    
    # 检查是否在项目根目录
    if not Path("pom.xml").exists():
        print("错误: 请在项目根目录下运行此脚本")
        sys.exit(1)
    
    # 1. 编译项目
    print("\n1. 编译项目...")
    if not run_command("mvn clean compile", "编译项目"):
        print("编译失败，退出测试")
        sys.exit(1)
    
    # 2. 运行单元测试
    print("\n2. 运行单元测试...")
    test_classes = [
        "com.soyokra.sprival.support.logging.KafkaAppenderTest",
        "com.soyokra.sprival.support.logging.KafkaAppenderEnhancedTest"
    ]
    
    for test_class in test_classes:
        cmd = f"mvn test -Dtest={test_class}"
        run_command(cmd, f"运行单元测试: {test_class}")
    
    # 3. 运行集成测试（需要Kafka环境）
    print("\n3. 运行集成测试...")
    print("注意: 集成测试需要Kafka环境，如果没有Kafka环境，测试会跳过")
    
    integration_tests = [
        "com.soyokra.sprival.support.logging.KafkaAppenderIntegrationTest"
    ]
    
    for test_class in integration_tests:
        cmd = f"mvn test -Dtest={test_class} -Dkafka.integration.test=true"
        run_command(cmd, f"运行集成测试: {test_class}")
    
    # 4. 运行性能测试（需要Kafka环境）
    print("\n4. 运行性能测试...")
    print("注意: 性能测试需要Kafka环境，如果没有Kafka环境，测试会跳过")
    
    performance_tests = [
        "com.soyokra.sprival.support.logging.KafkaAppenderPerformanceTest"
    ]
    
    for test_class in performance_tests:
        cmd = f"mvn test -Dtest={test_class} -Dkafka.performance.test=true"
        run_command(cmd, f"运行性能测试: {test_class}")
    
    # 5. 生成测试报告
    print("\n5. 生成测试报告...")
    run_command("mvn surefire-report:report", "生成Surefire测试报告")
    
    print("\n" + "="*60)
    print("测试完成!")
    print("="*60)
    print("\n测试报告位置:")
    print("- Surefire报告: target/site/surefire-report.html")
    print("- 测试结果: target/surefire-reports/")
    
    print("\n运行特定测试的方法:")
    print("1. 单元测试: mvn test -Dtest=KafkaAppenderEnhancedTest")
    print("2. 集成测试: mvn test -Dtest=KafkaAppenderIntegrationTest -Dkafka.integration.test=true")
    print("3. 性能测试: mvn test -Dtest=KafkaAppenderPerformanceTest -Dkafka.performance.test=true")

if __name__ == "__main__":
    main()
