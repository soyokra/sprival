#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
使用 Kafka 自带工具的测试脚本
通过调用 Kafka 自带的 console-producer 和 console-consumer 来测试

使用方法：
python test_kafka_shell.py

依赖：
subprocess (Python 内置)
"""

import json
import time
import subprocess
import random
from datetime import datetime
from typing import Dict, List


class KafkaShellTester:
    """使用 Kafka 自带工具的测试器"""
    
    def __init__(self, container_name: str = "sprival-logging-kafka-1", topic: str = "application-logs"):
        """
        初始化测试器
        
        Args:
            container_name: Kafka 容器名称
            topic: 目标主题名称
        """
        self.container_name = container_name
        self.topic = topic
        self.stats = {
            "sent": 0,
            "failed": 0,
            "start_time": None,
            "end_time": None
        }
    
    def send_message(self, message: str) -> bool:
        """
        发送消息到 Kafka
        
        Args:
            message: 要发送的消息
            
        Returns:
            bool: 发送是否成功
        """
        try:
            # 使用 Kafka 自带的 console-producer
            cmd = [
                "docker", "exec", "-i", self.container_name,
                "/opt/kafka/bin/kafka-console-producer.sh",
                "--bootstrap-server", "localhost:9092",
                "--topic", self.topic
            ]
            
            result = subprocess.run(
                cmd,
                input=message,
                text=True,
                capture_output=True,
                timeout=10
            )
            
            if result.returncode == 0:
                self.stats["sent"] += 1
                print(f"✓ 消息已发送: {message[:50]}...")
                return True
            else:
                print(f"✗ 发送失败: {result.stderr}")
                self.stats["failed"] += 1
                return False
                
        except subprocess.TimeoutExpired:
            print("✗ 发送超时")
            self.stats["failed"] += 1
            return False
        except Exception as e:
            print(f"✗ 发送错误: {e}")
            self.stats["failed"] += 1
            return False
    
    def create_log_message(self, level: str, message: str, **kwargs) -> str:
        """
        创建日志消息 JSON 字符串
        
        Args:
            level: 日志级别
            message: 日志消息
            **kwargs: 其他字段
            
        Returns:
            str: JSON 格式的日志消息
        """
        log_data = {
            "timestamp": datetime.now().isoformat(),
            "level": level,
            "loggerName": kwargs.get('loggerName', 'com.soyokra.sprival.test'),
            "threadName": kwargs.get('threadName', f'thread-{random.randint(1000, 9999)}'),
            "message": message,
            "hostname": kwargs.get('hostname', 'test-host'),
            "application": kwargs.get('application', 'sprival-test'),
            "log_source": kwargs.get('log_source', 'kafka'),
            "index_prefix": kwargs.get('index_prefix', 'sprival-logs')
        }
        
        # 添加可选字段
        if 'throwable' in kwargs:
            log_data['throwable'] = kwargs['throwable']
        if 'mdc' in kwargs:
            log_data['mdc'] = kwargs['mdc']
        
        return json.dumps(log_data, ensure_ascii=False)
    
    def test_basic_logs(self, count: int = 5) -> int:
        """测试基本日志"""
        print(f"开始测试基本日志，发送 {count} 条消息...")
        success_count = 0
        
        for i in range(count):
            message = f"测试基本日志消息 #{i+1} - {datetime.now().strftime('%H:%M:%S')}"
            log_json = self.create_log_message("INFO", message)
            
            if self.send_message(log_json):
                success_count += 1
                time.sleep(0.1)
        
        print(f"✓ 基本日志测试完成，成功发送 {success_count}/{count} 条")
        return success_count
    
    def test_error_logs(self, count: int = 3) -> int:
        """测试错误日志"""
        print(f"开始测试错误日志，发送 {count} 条消息...")
        success_count = 0
        
        error_messages = [
            "数据库连接失败",
            "用户认证异常", 
            "文件读取错误",
            "网络请求超时",
            "内存不足异常"
        ]
        
        for i in range(count):
            error_msg = random.choice(error_messages)
            log_json = self.create_log_message(
                "ERROR",
                f"系统错误: {error_msg}",
                throwable=f"java.lang.RuntimeException: {error_msg}\n\tat com.soyokra.sprival.TestClass.testMethod(TestClass.java:123)"
            )
            
            if self.send_message(log_json):
                success_count += 1
                time.sleep(0.1)
        
        print(f"✓ 错误日志测试完成，成功发送 {success_count}/{count} 条")
        return success_count
    
    def test_mdc_logs(self, count: int = 3) -> int:
        """测试 MDC 日志"""
        print(f"开始测试 MDC 日志，发送 {count} 条消息...")
        success_count = 0
        
        for i in range(count):
            user_id = f"user-{random.randint(1000, 9999)}"
            request_id = f"req-{int(time.time() * 1000)}"
            
            log_json = self.create_log_message(
                "INFO",
                f"用户操作日志 #{i+1}",
                mdc={
                    "userId": user_id,
                    "requestId": request_id,
                    "sessionId": f"session-{random.randint(10000, 99999)}",
                    "ipAddress": f"192.168.1.{random.randint(1, 254)}"
                }
            )
            
            if self.send_message(log_json):
                success_count += 1
                time.sleep(0.1)
        
        print(f"✓ MDC 日志测试完成，成功发送 {success_count}/{count} 条")
        return success_count
    
    def test_structured_logs(self, count: int = 3) -> int:
        """测试结构化日志"""
        print(f"开始测试结构化日志，发送 {count} 条消息...")
        success_count = 0
        
        actions = ["login", "logout", "view", "create", "update", "delete"]
        
        for i in range(count):
            action = random.choice(actions)
            duration = random.randint(10, 1000)
            
            log_json = self.create_log_message(
                "INFO",
                f"用户执行操作: {action}",
                mdc={
                    "action": action,
                    "duration": str(duration),
                    "resource": f"/api/{action}",
                    "status": "success" if random.random() > 0.1 else "failed"
                }
            )
            
            if self.send_message(log_json):
                success_count += 1
                time.sleep(0.1)
        
        print(f"✓ 结构化日志测试完成，成功发送 {success_count}/{count} 条")
        return success_count
    
    def test_performance(self, count: int = 20) -> Dict:
        """性能测试"""
        print(f"开始性能测试，发送 {count} 条消息...")
        
        start_time = time.time()
        success_count = 0
        
        for i in range(count):
            message = f"性能测试消息 #{i+1}"
            log_json = self.create_log_message("INFO", message)
            
            if self.send_message(log_json):
                success_count += 1
            
            # 每 5 条消息显示一次进度
            if (i + 1) % 5 == 0:
                print(f"已发送 {i+1}/{count} 条消息")
        
        end_time = time.time()
        duration = end_time - start_time
        
        performance_stats = {
            "total_messages": count,
            "successful_messages": success_count,
            "failed_messages": count - success_count,
            "duration_seconds": duration,
            "messages_per_second": success_count / duration if duration > 0 else 0,
            "success_rate": (success_count / count) * 100 if count > 0 else 0
        }
        
        print(f"✓ 性能测试完成")
        print(f"  总消息数: {count}")
        print(f"  成功消息: {success_count}")
        print(f"  失败消息: {count - success_count}")
        print(f"  耗时: {duration:.2f} 秒")
        print(f"  吞吐量: {performance_stats['messages_per_second']:.2f} 消息/秒")
        print(f"  成功率: {performance_stats['success_rate']:.2f}%")
        
        return performance_stats
    
    def run_comprehensive_test(self) -> Dict:
        """运行综合测试"""
        print("==========================================")
        print("开始 Kafka 日志综合测试 (使用 Shell 工具)")
        print("==========================================")
        
        self.stats["start_time"] = time.time()
        
        # 各种类型的日志测试
        test_results = {
            "basic_logs": self.test_basic_logs(5),
            "error_logs": self.test_error_logs(3),
            "mdc_logs": self.test_mdc_logs(3),
            "structured_logs": self.test_structured_logs(3),
            "performance_test": self.test_performance(20)
        }
        
        self.stats["end_time"] = time.time()
        
        # 生成测试报告
        self.generate_test_report(test_results)
        
        return test_results
    
    def generate_test_report(self, test_results: Dict):
        """生成测试报告"""
        print("==========================================")
        print("测试报告")
        print("==========================================")
        
        total_duration = self.stats["end_time"] - self.stats["start_time"]
        
        print(f"测试总耗时: {total_duration:.2f} 秒")
        print(f"总发送消息: {self.stats['sent']}")
        print(f"总失败消息: {self.stats['failed']}")
        print(f"成功率: {(self.stats['sent'] / (self.stats['sent'] + self.stats['failed']) * 100):.2f}%")
        
        print("\n各测试项目结果:")
        for test_name, result in test_results.items():
            if isinstance(result, dict):
                print(f"  {test_name}: {result.get('successful_messages', 0)} 条消息")
            else:
                print(f"  {test_name}: {result} 条消息")
        
        print("\n访问信息:")
        print("  Kafka: localhost:9092")
        print("  Topic: application-logs")
        print("  Kibana: http://localhost:5601")
        print("  Elasticsearch: http://localhost:9200")
        
        print("==========================================")
    
    def verify_messages(self, count: int = 5) -> int:
        """验证消息是否成功发送"""
        print(f"验证最近 {count} 条消息...")
        
        try:
            cmd = [
                "docker", "exec", self.container_name,
                "/opt/kafka/bin/kafka-console-consumer.sh",
                "--bootstrap-server", "localhost:9092",
                "--topic", self.topic,
                "--from-beginning",
                "--max-messages", str(count),
                "--timeout-ms", "5000"
            ]
            
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=10)
            
            if result.returncode == 0:
                lines = result.stdout.strip().split('\n')
                valid_lines = [line for line in lines if line.strip()]
                print(f"✓ 找到 {len(valid_lines)} 条消息")
                
                # 显示最后几条消息
                for i, line in enumerate(valid_lines[-3:], 1):
                    print(f"  消息 {i}: {line[:100]}...")
                
                return len(valid_lines)
            else:
                print(f"✗ 验证失败: {result.stderr}")
                return 0
                
        except Exception as e:
            print(f"✗ 验证错误: {e}")
            return 0


def main():
    """主函数"""
    print("==========================================")
    print("Kafka 日志测试脚本 (Shell 版本)")
    print("==========================================")
    
    # 创建测试器
    tester = KafkaShellTester()
    
    try:
        # 运行综合测试
        tester.run_comprehensive_test()
        
        # 验证消息
        print("\n验证消息发送结果...")
        tester.verify_messages(10)
        
    except KeyboardInterrupt:
        print("\n用户中断测试")
    except Exception as e:
        print(f"测试过程中发生错误: {e}")
    
    print("\n测试完成")


if __name__ == "__main__":
    main()
