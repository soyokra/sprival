#!/bin/bash

# Kafka 验证脚本
echo "=========================================="
echo "验证 Kafka 服务状态"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查 Kafka 容器状态
echo -e "${YELLOW}检查 Kafka 容器状态...${NC}"
docker ps | grep sprival-logging-kafka-1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Kafka 容器正在运行${NC}"
else
    echo -e "${RED}✗ Kafka 容器未运行${NC}"
    exit 1
fi

# 检查 Kafka 主题
echo -e "${YELLOW}检查 Kafka 主题...${NC}"
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# 创建 application-logs 主题（如果不存在）
echo -e "${YELLOW}确保 application-logs 主题存在...${NC}"
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic application-logs --partitions 1 --replication-factor 1 --if-not-exists

# 验证主题创建
echo -e "${YELLOW}验证主题列表...${NC}"
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# 测试生产者
echo -e "${YELLOW}测试 Kafka 生产者...${NC}"
echo "test-message-$(date +%s)" | docker exec -i sprival-logging-kafka-1 /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic application-logs

# 测试消费者
echo -e "${YELLOW}测试 Kafka 消费者（5秒超时）...${NC}"
timeout 5s docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic application-logs --from-beginning --max-messages 1

echo ""
echo "=========================================="
echo -e "${GREEN}Kafka 验证完成！${NC}"
echo "=========================================="
echo ""
echo "可用的 Kafka 命令："
echo "1. 查看主题列表："
echo "   docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list"
echo ""
echo "2. 发送测试消息："
echo "   echo 'test-message' | docker exec -i sprival-logging-kafka-1 /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic application-logs"
echo ""
echo "3. 消费消息："
echo "   docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic application-logs --from-beginning"
echo ""
echo "4. 查看主题详情："
echo "   docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic application-logs"
