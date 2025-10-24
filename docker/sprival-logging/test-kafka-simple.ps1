# PowerShell 脚本测试 Kafka
Write-Host "=========================================="
Write-Host "测试 Kafka 连接和消息发送"
Write-Host "=========================================="

# 1. 检查 Kafka 容器状态
Write-Host "检查 Kafka 容器状态..."
docker ps | Select-String "sprival-logging-kafka-1"

# 2. 列出主题
Write-Host "`n列出 Kafka 主题..."
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# 3. 创建测试消息文件
Write-Host "`n创建测试消息..."
$testMessage = "test-log-message-$(Get-Date -Format 'yyyyMMdd-HHmmss')"
Write-Host "测试消息: $testMessage"

# 4. 发送消息到 Kafka
Write-Host "`n发送消息到 Kafka..."
$testMessage | docker exec -i sprival-logging-kafka-1 /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic application-logs

# 5. 等待一下
Start-Sleep -Seconds 2

# 6. 消费消息
Write-Host "`n消费消息（5秒超时）..."
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic application-logs --from-beginning --max-messages 1 --timeout-ms 5000

Write-Host "`n=========================================="
Write-Host "Kafka 测试完成！"
Write-Host "=========================================="
