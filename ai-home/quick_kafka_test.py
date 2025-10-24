#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
快速 Kafka 日志测试脚本
简化版本，用于快速验证 Kafka 连接和消息发送

使用方法：
python quick_kafka_test.py

依赖：
pip install kafka-python
"""

import json
import time
from datetime import datetime
from kafka import KafkaProducer
from kafka.errors import KafkaError


def test_kafka_connection():
    """测试 Kafka 连接和消息发送"""
    print("==========================================")
    print("快速 Kafka 日志测试")
    print("==========================================")
    
    # 配置
    bootstrap_servers = "localhost:9092"
    topic = "application-logs"
    
    try:
        # 创建生产者
        print("正在连接到 Kafka...")
        producer = KafkaProducer(
            bootstrap_servers=bootstrap_servers,
            value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8'),
            retries=3,
            request_timeout_ms=10000,
            api_version=(0, 9, 0)
        )
        print("✓ 成功连接到 Kafka")
        
        # 创建测试日志消息
        test_message = {
            "timestamp": datetime.now().isoformat(),
            "level": "INFO",
            "loggerName": "com.soyokra.sprival.test",
            "threadName": "main",
            "message": f"快速测试消息 - {datetime.now().strftime('%H:%M:%S')}",
            "hostname": "test-host",
            "application": "sprival-test",
            "log_source": "kafka",
            "index_prefix": "sprival-logs"
        }
        
        # 发送消息
        print("正在发送测试消息...")
        future = producer.send(topic, value=test_message)
        record_metadata = future.get(timeout=10)
        
        print(f"✓ 消息已发送到分区 {record_metadata.partition}, 偏移量 {record_metadata.offset}")
        print(f"✓ 消息内容: {test_message['message']}")
        
        # 关闭生产者
        producer.close()
        print("✓ 连接已关闭")
        
        print("\n==========================================")
        print("测试完成！")
        print("==========================================")
        print("访问信息:")
        print("  Kafka: localhost:9092")
        print("  Topic: application-logs")
        print("  Kibana: http://localhost:5601")
        print("  Elasticsearch: http://localhost:9200")
        
        return True
        
    except KafkaError as e:
        print(f"✗ Kafka 错误: {e}")
        return False
    except Exception as e:
        print(f"✗ 未知错误: {e}")
        return False


if __name__ == "__main__":
    success = test_kafka_connection()
    if not success:
        print("\n故障排除:")
        print("1. 确保 Kafka 服务正在运行")
        print("2. 检查端口 9092 是否可访问")
        print("3. 运行: cd docker/sprival-logging && docker-compose up -d")
        exit(1)
