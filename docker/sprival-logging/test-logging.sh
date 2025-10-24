#!/bin/bash

# 日志集成测试脚本
# 用于测试 Spring -> Kafka -> Logstash -> Elasticsearch -> Kibana 的完整流程

echo "=========================================="
echo "开始测试 Kafka + ELK 日志集成"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查服务状态
check_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}检查 $service_name 服务状态...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "http://localhost:$port" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $service_name 服务已启动 (端口: $port)${NC}"
            return 0
        fi
        
        echo -e "${YELLOW}等待 $service_name 启动... ($attempt/$max_attempts)${NC}"
        sleep 2
        ((attempt++))
    done
    
    echo -e "${RED}✗ $service_name 服务启动失败${NC}"
    return 1
}

# 检查 Kafka 主题
check_kafka_topic() {
    echo -e "${YELLOW}检查 Kafka 主题...${NC}"
    
    # 等待 Kafka 完全启动
    sleep 10
    
    # 检查主题是否存在
    docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list | grep -q "application-logs"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Kafka 主题 'application-logs' 已存在${NC}"
    else
        echo -e "${YELLOW}创建 Kafka 主题 'application-logs'...${NC}"
        docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic application-logs --partitions 1 --replication-factor 1
        echo -e "${GREEN}✓ Kafka 主题创建完成${NC}"
    fi
}

# 测试日志发送
test_log_sending() {
    echo -e "${YELLOW}测试日志发送...${NC}"
    
    # 启动应用（如果未启动）
    echo -e "${YELLOW}启动 Spring Boot 应用...${NC}"
    cd ../../ && mvn spring-boot:run > /dev/null 2>&1 &
    APP_PID=$!
    
    # 等待应用启动
    sleep 15
    
    # 测试各种日志接口
    echo -e "${YELLOW}测试基本日志...${NC}"
    curl -s "http://localhost:8338/api/test/logging/basic" > /dev/null
    
    echo -e "${YELLOW}测试异常日志...${NC}"
    curl -s "http://localhost:8338/api/test/logging/exception" > /dev/null
    
    echo -e "${YELLOW}测试MDC日志...${NC}"
    curl -s "http://localhost:8338/api/test/logging/mdc?userId=test-user-123" > /dev/null
    
    echo -e "${YELLOW}测试结构化日志...${NC}"
    curl -s "http://localhost:8338/api/test/logging/structured?action=test-action" > /dev/null
    
    echo -e "${YELLOW}测试批量日志...${NC}"
    curl -s "http://localhost:8338/api/test/logging/batch?count=5" > /dev/null
    
    # 停止应用
    kill $APP_PID 2>/dev/null
    echo -e "${GREEN}✓ 日志发送测试完成${NC}"
}

# 检查 Elasticsearch 索引
check_elasticsearch_index() {
    echo -e "${YELLOW}检查 Elasticsearch 索引...${NC}"
    
    # 等待日志处理
    sleep 10
    
    # 检查索引是否存在
    local indices=$(curl -s "http://localhost:9200/_cat/indices?v" | grep "sprival-logs")
    
    if [ -n "$indices" ]; then
        echo -e "${GREEN}✓ Elasticsearch 索引已创建${NC}"
        echo "$indices"
    else
        echo -e "${YELLOW}等待索引创建...${NC}"
        sleep 5
        local indices=$(curl -s "http://localhost:9200/_cat/indices?v" | grep "sprival-logs")
        if [ -n "$indices" ]; then
            echo -e "${GREEN}✓ Elasticsearch 索引已创建${NC}"
            echo "$indices"
        else
            echo -e "${RED}✗ Elasticsearch 索引未找到${NC}"
        fi
    fi
}

# 检查 Kibana 数据
check_kibana_data() {
    echo -e "${YELLOW}检查 Kibana 数据...${NC}"
    
    # 等待数据索引
    sleep 5
    
    # 查询日志数据
    local count=$(curl -s "http://localhost:9200/sprival-logs-*/_count" | grep -o '"count":[0-9]*' | cut -d':' -f2)
    
    if [ -n "$count" ] && [ "$count" -gt 0 ]; then
        echo -e "${GREEN}✓ Kibana 中发现 $count 条日志记录${NC}"
    else
        echo -e "${YELLOW}等待数据索引...${NC}"
        sleep 10
        local count=$(curl -s "http://localhost:9200/sprival-logs-*/_count" | grep -o '"count":[0-9]*' | cut -d':' -f2)
        if [ -n "$count" ] && [ "$count" -gt 0 ]; then
            echo -e "${GREEN}✓ Kibana 中发现 $count 条日志记录${NC}"
        else
            echo -e "${RED}✗ Kibana 中未找到日志数据${NC}"
        fi
    fi
}

# 显示访问信息
show_access_info() {
    echo ""
    echo "=========================================="
    echo -e "${GREEN}日志集成测试完成！${NC}"
    echo "=========================================="
    echo ""
    echo "访问地址："
    echo -e "${YELLOW}Kibana:${NC} http://localhost:5601"
    echo -e "${YELLOW}Elasticsearch:${NC} http://localhost:9200"
    echo -e "${YELLOW}Logstash:${NC} http://localhost:9600"
    echo -e "${YELLOW}Kafka:${NC} localhost:9092"
    echo ""
    echo "测试接口："
    echo -e "${YELLOW}基本日志:${NC} http://localhost:8338/api/test/logging/basic"
    echo -e "${YELLOW}异常日志:${NC} http://localhost:8338/api/test/logging/exception"
    echo -e "${YELLOW}MDC日志:${NC} http://localhost:8338/api/test/logging/mdc?userId=test-user"
    echo -e "${YELLOW}结构化日志:${NC} http://localhost:8338/api/test/logging/structured?action=test"
    echo -e "${YELLOW}批量日志:${NC} http://localhost:8338/api/test/logging/batch?count=10"
    echo -e "${YELLOW}配置信息:${NC} http://localhost:8338/api/test/logging/config"
    echo ""
    echo "在 Kibana 中创建索引模式："
    echo "1. 访问 http://localhost:5601"
    echo "2. 进入 Stack Management > Index Patterns"
    echo "3. 创建索引模式：sprival-logs-*"
    echo "4. 选择时间字段：@timestamp"
    echo ""
}

# 主执行流程
main() {
    echo "开始检查服务状态..."
    
    # 检查各个服务
    check_service "Elasticsearch" 9200 || exit 1
    check_service "Kibana" 5601 || exit 1
    check_service "Logstash" 9600 || exit 1
    
    # 检查 Kafka 主题
    check_kafka_topic
    
    # 测试日志发送
    test_log_sending
    
    # 检查 Elasticsearch 索引
    check_elasticsearch_index
    
    # 检查 Kibana 数据
    check_kibana_data
    
    # 显示访问信息
    show_access_info
}

# 执行主流程
main "$@"
