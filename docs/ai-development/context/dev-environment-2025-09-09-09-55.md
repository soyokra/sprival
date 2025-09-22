## 寮€鍙戠幆澧冧俊鎭?

### 椤圭洰缁撴瀯
`
sprival/
鈹溾攢鈹€ src/main/java/com/soyokra/sprival/
鈹?  鈹溾攢鈹€ SprivalApplication.java          # 涓诲簲鐢ㄧ被
鈹?  鈹溾攢鈹€ config/                          # 閰嶇疆绫荤洰褰?
鈹?  鈹?  鈹溾攢鈹€ http/                        # HTTP瀹㈡埛绔厤缃?
鈹?  鈹?  鈹溾攢鈹€ redis/                       # Redis閰嶇疆
鈹?  鈹?  鈹溾攢鈹€ mysql/                       # MySQL閰嶇疆
鈹?  鈹?  鈹溾攢鈹€ jetty/                       # Jetty閰嶇疆
鈹?  鈹?  鈹溾攢鈹€ kafka/                       # Kafka閰嶇疆
鈹?  鈹?  鈹溾攢鈹€ mongodb/                     # MongoDB閰嶇疆
鈹?  鈹?  鈹溾攢鈹€ rabbit/                      # RabbitMQ閰嶇疆
鈹?  鈹?  鈹溾攢鈹€ clickhouse/                  # ClickHouse閰嶇疆
鈹?  鈹?  鈹斺攢鈹€ ratelimiter/                 # 闄愭祦鍣ㄩ厤缃?
鈹?  鈹溾攢鈹€ client/                          # Feign瀹㈡埛绔?
鈹?  鈹溾攢鈹€ service/                         # 涓氬姟鏈嶅姟
鈹?  鈹斺攢鈹€ controller/                      # 鎺у埗鍣?
鈹溾攢鈹€ src/main/resources/
鈹?  鈹溾攢鈹€ application.properties           # 搴旂敤閰嶇疆
鈹?  鈹溾攢鈹€ redisson.yml                     # Redisson閰嶇疆
鈹?  鈹溾攢鈹€ spy.properties                   # P6Spy閰嶇疆
鈹?  鈹斺攢鈹€ mapper/                          # MyBatis鏄犲皠鏂囦欢
鈹溾攢鈹€ dockers/                             # Docker閰嶇疆
鈹溾攢鈹€ docs/                                # 椤圭洰鏂囨。
鈹斺攢鈹€ scripts/                             # 鑴氭湰鏂囦欢
`

### 鍚姩鏂瑰紡
1. **Maven鍚姩**: mvn spring-boot:run
2. **鑴氭湰鍚姩**: start-utf8.bat (Windows)
3. **Docker鍚姩**: docker-compose up

### 鐩戞帶绔偣
- **鍋ュ悍妫€鏌?*: http://localhost:8338/api/actuator/health
- **搴旂敤淇℃伅**: http://localhost:8338/api/actuator/info
- **鎸囨爣鐩戞帶**: http://localhost:8338/api/actuator/metrics
- **Prometheus**: http://localhost:8338/api/actuator/prometheus

