#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Kafka 日志测试脚本
用于测试向 Kafka 发送日志消息，模拟 Spring Boot 应用的日志输出

功能：
1. 连接到 localhost:9092 的 Kafka
2. 向 application-logs topic 发送各种类型的日志消息
3. 支持结构化日志、异常日志、MDC 日志等
4. 提供批量发送和性能测试功能

依赖：
pip install kafka-python requests

作者：AI Assistant
创建时间：2025-01-22
"""

import json
import time
import random
import logging
import traceback
from datetime import datetime
from typing import Dict, List, Optional, Any
from dataclasses import dataclass, asdict

try:
    from kafka import KafkaProducer
    from kafka.errors import KafkaError
except ImportError:
    print("错误：请安装 kafka-python 库")
    print("运行：pip install kafka-python")
    exit(1)

try:
    import requests
except ImportError:
    print("错误：请安装 requests 库")
    print("运行：pip install requests")
    exit(1)


@dataclass
class LogMessage:
    """日志消息数据结构"""
    timestamp: str
    level: str
    loggerName: str
    threadName: str
    message: str
    hostname: str = "test-host"
    application: str = "sprival-test"
    log_source: str = "kafka"
    index_prefix: str = "sprival-logs"
    throwable: Optional[str] = None
    mdc: Optional[Dict[str, str]] = None


class KafkaLogTester:
    """Kafka 日志测试器"""
    
    def __init__(self, bootstrap_servers: str = "localhost:9092", topic: str = "application-logs"):
        """
        初始化 Kafka 日志测试器
        
        Args:
            bootstrap_servers: Kafka 服务器地址
            topic: 目标主题名称
        """
        self.bootstrap_servers = bootstrap_servers
        self.topic = topic
        self.producer = None
        self.stats = {
            "sent": 0,
            "failed": 0,
            "start_time": None,
            "end_time": None
        }
        
        # 配置日志
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s'
        )
        self.logger = logging.getLogger(__name__)
    
    def connect(self) -> bool:
        """
        连接到 Kafka
        
        Returns:
            bool: 连接是否成功
        """
        try:
        self.producer = KafkaProducer(
            bootstrap_servers=self.bootstrap_servers,
            value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8'),
            key_serializer=lambda k: k.encode('utf-8') if k else None,
            retries=3,
            retry_backoff_ms=1000,
            request_timeout_ms=30000,
            api_version=(0, 10, 0)
        )
            self.logger.info(f"✓ 成功连接到 Kafka: {self.bootstrap_servers}")
            return True
        except Exception as e:
            self.logger.error(f"✗ 连接 Kafka 失败: {e}")
            return False
    
    def disconnect(self):
        """断开 Kafka 连接"""
        if self.producer:
            self.producer.close()
            self.logger.info("✓ 已断开 Kafka 连接")
    
    def create_log_message(self, level: str, message: str, **kwargs) -> LogMessage:
        """
        创建日志消息
        
        Args:
            level: 日志级别 (INFO, DEBUG, WARN, ERROR)
            message: 日志消息
            **kwargs: 其他字段
            
        Returns:
            LogMessage: 日志消息对象
        """
        return LogMessage(
            timestamp=datetime.now().isoformat(),
            level=level,
            loggerName=kwargs.get('loggerName', 'com.soyokra.sprival.test'),
            threadName=kwargs.get('threadName', f'thread-{random.randint(1000, 9999)}'),
            message=message,
            hostname=kwargs.get('hostname', 'test-host'),
            application=kwargs.get('application', 'sprival-test'),
            log_source=kwargs.get('log_source', 'kafka'),
            index_prefix=kwargs.get('index_prefix', 'sprival-logs'),
            throwable=kwargs.get('throwable'),
            mdc=kwargs.get('mdc')
        )
    
    def send_log(self, log_message: LogMessage, key: Optional[str] = None) -> bool:
        """
        发送单条日志消息
        
        Args:
            log_message: 日志消息对象
            key: 消息键（可选）
            
        Returns:
            bool: 发送是否成功
        """
        if not self.producer:
            self.logger.error("✗ 未连接到 Kafka")
            return False
        
        try:
            future = self.producer.send(
                self.topic,
                value=asdict(log_message),
                key=key
            )
            # 等待发送完成
            record_metadata = future.get(timeout=10)
            self.stats["sent"] += 1
            self.logger.debug(f"✓ 日志已发送到分区 {record_metadata.partition}, 偏移量 {record_metadata.offset}")
            return True
        except KafkaError as e:
            self.logger.error(f"✗ 发送日志失败: {e}")
            self.stats["failed"] += 1
            return False
        except Exception as e:
            self.logger.error(f"✗ 发送日志时发生未知错误: {e}")
            self.stats["failed"] += 1
            return False
    
    def test_basic_logs(self, count: int = 5) -> int:
        """
        测试基本日志
        
        Args:
            count: 发送数量
            
        Returns:
            int: 成功发送的数量
        """
        self.logger.info(f"开始测试基本日志，发送 {count} 条消息...")
        success_count = 0
        
        for i in range(count):
            message = f"测试基本日志消息 #{i+1} - {datetime.now().strftime('%H:%M:%S')}"
            log_msg = self.create_log_message("INFO", message)
            
            if self.send_log(log_msg):
                success_count += 1
                time.sleep(0.1)  # 避免发送过快
        
        self.logger.info(f"✓ 基本日志测试完成，成功发送 {success_count}/{count} 条")
        return success_count
    
    def test_error_logs(self, count: int = 3) -> int:
        """
        测试错误日志
        
        Args:
            count: 发送数量
            
        Returns:
            int: 成功发送的数量
        """
        self.logger.info(f"开始测试错误日志，发送 {count} 条消息...")
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
            log_msg = self.create_log_message(
                "ERROR", 
                f"系统错误: {error_msg}",
                throwable=f"java.lang.RuntimeException: {error_msg}\n\tat com.soyokra.sprival.TestClass.testMethod(TestClass.java:123)"
            )
            
            if self.send_log(log_msg):
                success_count += 1
                time.sleep(0.1)
        
        self.logger.info(f"✓ 错误日志测试完成，成功发送 {success_count}/{count} 条")
        return success_count
    
    def test_mdc_logs(self, count: int = 3) -> int:
        """
        测试 MDC 日志
        
        Args:
            count: 发送数量
            
        Returns:
            int: 成功发送的数量
        """
        self.logger.info(f"开始测试 MDC 日志，发送 {count} 条消息...")
        success_count = 0
        
        for i in range(count):
            user_id = f"user-{random.randint(1000, 9999)}"
            request_id = f"req-{int(time.time() * 1000)}"
            
            log_msg = self.create_log_message(
                "INFO",
                f"用户操作日志 #{i+1}",
                mdc={
                    "userId": user_id,
                    "requestId": request_id,
                    "sessionId": f"session-{random.randint(10000, 99999)}",
                    "ipAddress": f"192.168.1.{random.randint(1, 254)}"
                }
            )
            
            if self.send_log(log_msg):
                success_count += 1
                time.sleep(0.1)
        
        self.logger.info(f"✓ MDC 日志测试完成，成功发送 {success_count}/{count} 条")
        return success_count
    
    def test_structured_logs(self, count: int = 3) -> int:
        """
        测试结构化日志
        
        Args:
            count: 发送数量
            
        Returns:
            int: 成功发送的数量
        """
        self.logger.info(f"开始测试结构化日志，发送 {count} 条消息...")
        success_count = 0
        
        actions = ["login", "logout", "view", "create", "update", "delete"]
        
        for i in range(count):
            action = random.choice(actions)
            duration = random.randint(10, 1000)
            
            log_msg = self.create_log_message(
                "INFO",
                f"用户执行操作: {action}",
                mdc={
                    "action": action,
                    "duration": str(duration),
                    "resource": f"/api/{action}",
                    "status": "success" if random.random() > 0.1 else "failed"
                }
            )
            
            if self.send_log(log_msg):
                success_count += 1
                time.sleep(0.1)
        
        self.logger.info(f"✓ 结构化日志测试完成，成功发送 {success_count}/{count} 条")
        return success_count
    
    def test_performance(self, count: int = 100) -> Dict[str, Any]:
        """
        性能测试
        
        Args:
            count: 发送数量
            
        Returns:
            Dict[str, Any]: 性能统计信息
        """
        self.logger.info(f"开始性能测试，发送 {count} 条消息...")
        
        start_time = time.time()
        success_count = 0
        
        for i in range(count):
            message = f"性能测试消息 #{i+1}"
            log_msg = self.create_log_message("INFO", message)
            
            if self.send_log(log_msg):
                success_count += 1
            
            # 每 10 条消息显示一次进度
            if (i + 1) % 10 == 0:
                self.logger.info(f"已发送 {i+1}/{count} 条消息")
        
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
        
        self.logger.info(f"✓ 性能测试完成")
        self.logger.info(f"  总消息数: {count}")
        self.logger.info(f"  成功消息: {success_count}")
        self.logger.info(f"  失败消息: {count - success_count}")
        self.logger.info(f"  耗时: {duration:.2f} 秒")
        self.logger.info(f"  吞吐量: {performance_stats['messages_per_second']:.2f} 消息/秒")
        self.logger.info(f"  成功率: {performance_stats['success_rate']:.2f}%")
        
        return performance_stats
    
    def run_comprehensive_test(self) -> Dict[str, Any]:
        """
        运行综合测试
        
        Returns:
            Dict[str, Any]: 测试结果统计
        """
        self.logger.info("==========================================")
        self.logger.info("开始 Kafka 日志综合测试")
        self.logger.info("==========================================")
        
        self.stats["start_time"] = time.time()
        
        # 各种类型的日志测试
        test_results = {
            "basic_logs": self.test_basic_logs(5),
            "error_logs": self.test_error_logs(3),
            "mdc_logs": self.test_mdc_logs(3),
            "structured_logs": self.test_structured_logs(3),
            "performance_test": self.test_performance(50)
        }
        
        self.stats["end_time"] = time.time()
        
        # 生成测试报告
        self.generate_test_report(test_results)
        
        return test_results
    
    def generate_test_report(self, test_results: Dict[str, Any]):
        """
        生成测试报告
        
        Args:
            test_results: 测试结果
        """
        self.logger.info("==========================================")
        self.logger.info("测试报告")
        self.logger.info("==========================================")
        
        total_duration = self.stats["end_time"] - self.stats["start_time"]
        
        self.logger.info(f"测试总耗时: {total_duration:.2f} 秒")
        self.logger.info(f"总发送消息: {self.stats['sent']}")
        self.logger.info(f"总失败消息: {self.stats['failed']}")
        self.logger.info(f"成功率: {(self.stats['sent'] / (self.stats['sent'] + self.stats['failed']) * 100):.2f}%")
        
        self.logger.info("\n各测试项目结果:")
        for test_name, result in test_results.items():
            if isinstance(result, dict):
                self.logger.info(f"  {test_name}: {result.get('successful_messages', 0)} 条消息")
            else:
                self.logger.info(f"  {test_name}: {result} 条消息")
        
        self.logger.info("\n访问信息:")
        self.logger.info("  Kafka: localhost:9092")
        self.logger.info("  Topic: application-logs")
        self.logger.info("  Kibana: http://localhost:5601")
        self.logger.info("  Elasticsearch: http://localhost:9200")
        
        self.logger.info("==========================================")


def check_kafka_connection(bootstrap_servers: str = "localhost:9092") -> bool:
    """
    检查 Kafka 连接
    
    Args:
        bootstrap_servers: Kafka 服务器地址
        
    Returns:
        bool: 连接是否成功
    """
    try:
        producer = KafkaProducer(
            bootstrap_servers=bootstrap_servers,
            request_timeout_ms=5000,
            api_version=(0, 10, 1)
        )
        producer.close()
        return True
    except Exception as e:
        print(f"✗ 无法连接到 Kafka: {e}")
        return False


def main():
    """主函数"""
    print("==========================================")
    print("Kafka 日志测试脚本")
    print("==========================================")
    
    # 检查 Kafka 连接
    if not check_kafka_connection():
        print("请确保 Kafka 服务正在运行 (localhost:9092)")
        print("启动命令: cd docker/sprival-logging && docker-compose up -d")
        return
    
    # 创建测试器
    tester = KafkaLogTester()
    
    try:
        # 连接到 Kafka
        if not tester.connect():
            return
        
        # 运行综合测试
        tester.run_comprehensive_test()
        
    except KeyboardInterrupt:
        print("\n用户中断测试")
    except Exception as e:
        print(f"测试过程中发生错误: {e}")
        traceback.print_exc()
    finally:
        # 断开连接
        tester.disconnect()
        print("\n测试完成")


if __name__ == "__main__":
    main()
