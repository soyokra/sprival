## 

spring-autoconfigure-metadata.properties
```text
org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration=
org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration.ConditionalOnClass=org.springframework.amqp.rabbit.annotation.EnableRabbit
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration=
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration$MessagingTemplateConfiguration=
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration$MessagingTemplateConfiguration.ConditionalOnClass=org.springframework.amqp.rabbit.core.RabbitMessagingTemplate
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.ConditionalOnClass=com.rabbitmq.client.Channel,org.springframework.amqp.rabbit.core.RabbitTemplate
```

spring.factories
```text
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
```
