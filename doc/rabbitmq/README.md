## 服务端架构
![图片替代文本](rabbitmq-cluster.drawio.png)

rabbitmq最简化的高可用架构需要3个节点，队列选用quorum，三个节点的节点类型都设置为磁盘类型，持久化元数据，对队列性能没有影响

## 监控
