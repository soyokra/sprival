## 缁勪欢鐘舵€佺煩闃?

### 宸插畬鎴愮粍浠?鉁?

#### 1. HTTP Server (spring-http-server)
- **鐘舵€?*: 宸插畬鎴?
- **鎶€鏈爤**: Jetty + Guava RateLimiter
- **閰嶇疆绫?*: SprivalJettyCustomizer, SprivalRateLimiterConfiguration
- **鍔熻兘**: Web鏈嶅姟鍣?+ 鎺ュ彛闄愭祦
- **鏂囨。**: docs/spring-http-server/README.md

#### 2. MySQL (spring-mysql)
- **鐘舵€?*: 宸插畬鎴?
- **鎶€鏈爤**: MyBatis-Plus + Dynamic-Datasource + HikariCP + P6Spy
- **閰嶇疆绫?*: SprivalMybatisPlusConfiguration
- **鍔熻兘**: 鏁版嵁搴撹闂?+ 澶氭暟鎹簮 + SQL鐩戞帶
- **鏂囨。**: docs/spring-mysql/README.md

#### 3. Redis (spring-redis)
- **鐘舵€?*: 宸插畬鎴?
- **鎶€鏈爤**: Spring Cache + Spring Data Redis + Redisson
- **閰嶇疆绫?*: SprivalRedisConfiguration, SprivalRedisHealthIndicator
- **鍔熻兘**: 缂撳瓨 + 鍒嗗竷寮忛攣 + 鍋ュ悍妫€鏌?
- **鏂囨。**: docs/spring-redis/README.md
- **浼樺厛绾?*: Redisson > Spring Data Redis

#### 4. ClickHouse (spring-clickhouse)
- **鐘舵€?*: 宸插畬鎴?
- **鎶€鏈爤**: ClickHouse JDBC + MyBatis-Plus闆嗘垚
- **閰嶇疆绫?*: SprivalClickHouseDataSourceCreator
- **鍔熻兘**: 鍒嗘瀽鏁版嵁搴?+ 鏁版嵁婧愰泦鎴?
- **鏂囨。**: docs/spring-clickhouse/README.md

#### 5. MongoDB (spring-mongo)
- **鐘舵€?*: 宸插畬鎴?
- **鎶€鏈爤**: Spring Data MongoDB
- **閰嶇疆绫?*: SprivalMongoHealthIndicator
- **鍔熻兘**: 鏂囨。鏁版嵁搴?+ 鍋ュ悍妫€鏌?
- **鏂囨。**: docs/spring-mongo/README.md

#### 6. RabbitMQ (spring-rabbit)
- **鐘舵€?*: 宸插畬鎴?
- **鎶€鏈爤**: Spring AMQP
- **閰嶇疆绫?*: SprivalRabbitHealthIndicator
- **鍔熻兘**: 娑堟伅闃熷垪 + 鍋ュ悍妫€鏌?
- **鏂囨。**: docs/spring-rabbit/README.md

#### 7. Kafka (spring-kafka)
- **鐘舵€?*: 宸插畬鎴?
- **鎶€鏈爤**: Spring Kafka
- **閰嶇疆绫?*: SprivalKafkaProducerCustomizer, SprivalKafkaConsumerCustomizer
- **鍔熻兘**: 娑堟伅闃熷垪 + 鐩戞帶闆嗘垚
- **鏂囨。**: docs/spring-kafka/README.md

#### 8. HTTP Client (spring-http-client)
- **鐘舵€?*: 宸插畬鎴?
- **鎶€鏈爤**: Feign + OkHttp + Resilience4j + LoadBalancer + Micrometer
- **閰嶇疆绫?*: SprivalHttpClientConfiguration, SprivalHttpClientHealthIndicator
- **鍔熻兘**: 澹版槑寮廐TTP瀹㈡埛绔?+ 瀹归敊鏈哄埗 + 璐熻浇鍧囪　 + 鐩戞帶
- **鏂囨。**: docs/spring-http-client/README.md

### 缁勪欢渚濊禆鍏崇郴
- **spring-mysql** 鈫?**spring-clickhouse** (鏁版嵁婧愬熀纭€)
- **spring-redis** 鈫?**spring-cache** (缂撳瓨鍩虹)
- **http-server** 鈫?**ratelimiter** (闄愭祦闆嗘垚)
- **鎵€鏈夌粍浠?* 鈫?**monitoring** (鐩戞帶闆嗘垚)

