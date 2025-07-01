## 客户端源码包
- org.springframework.amqp：amqp接口定义和公共支持类
- org.springframework.amqp.rabbit: rabbit实现包
- org.springframework.boot.actuate.amqp：rabbit健康检查
- org.springframework.boot.actuate.metrics.amqp：rabbit指标监控
- org.springframework.boot.actuate.autoconfigure.amqp：rabbitmq健康检查自动装配
- org.springframework.boot.autoconfigure.amqp：rabbit自动装配

rabbit自动装配通过spring.factories加载
```text
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
```

## 监控指标
####  健康检查
健康检查是通过使用rabbitTemplate尝试获取版本实现的
```java
public class RabbitHealthIndicator extends AbstractHealthIndicator {

	private final RabbitTemplate rabbitTemplate;

	public RabbitHealthIndicator(RabbitTemplate rabbitTemplate) {
		super("Rabbit health check failed");
		Assert.notNull(rabbitTemplate, "RabbitTemplate must not be null");
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		builder.up().withDetail("version", getVersion());
	}

	private String getVersion() {
		return this.rabbitTemplate
				.execute((channel) -> channel.getConnection().getServerProperties().get("version").toString());
	}

}
```


#### 指标监控
```java
public class RabbitMetrics implements MeterBinder {

	private final Iterable<Tag> tags;

	private final ConnectionFactory connectionFactory;

	/**
	 * Create a new meter binder recording the specified {@link ConnectionFactory}.
	 * @param connectionFactory the {@link ConnectionFactory} to instrument
	 * @param tags tags to apply to all recorded metrics
	 */
	public RabbitMetrics(ConnectionFactory connectionFactory, Iterable<Tag> tags) {
		Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
		this.connectionFactory = connectionFactory;
		this.tags = (tags != null) ? tags : Collections.emptyList();
	}

	@Override
	public void bindTo(MeterRegistry registry) {
		this.connectionFactory.setMetricsCollector(new MicrometerMetricsCollector(registry, "rabbitmq", this.tags));
	}

}
```

MetricsCollector => AbstractMetricsCollector
                                    => MicrometerMetricsCollector
                                    => NoOpMetricsCollector
                                    => StandardMetricsCollector
