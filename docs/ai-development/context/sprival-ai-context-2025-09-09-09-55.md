# Sprival椤圭洰AI缂栫▼涓婁笅鏂?

**鐢熸垚鏃堕棿**: 2025-09-09 09:55:02  
**鐗堟湰**: 0.0.1  
**鐢ㄩ€?*: AI缂栫▼鍓嶉」鐩幇鐘朵簡瑙?

## 馃幆 椤圭洰姒傝堪

Sprival鏄竴涓猄pring Boot缁勪欢闆嗘垚妯℃澘椤圭洰锛屾彁渚涗簡瀹屾暣鐨勫井鏈嶅姟寮€鍙戝熀纭€璁炬柦锛屽寘鎷暟鎹闂€佺紦瀛樸€佹秷鎭槦鍒椼€丠TTP瀹㈡埛绔€佺洃鎺х瓑缁勪欢銆?

## 馃搳 椤圭洰鐘舵€佹€昏

### 鎶€鏈爤
- **妗嗘灦**: Spring Boot 2.7.18 + Java 8
- **浜戝師鐢?*: Spring Cloud 2021.0.8
- **鏋勫缓**: Maven
- **鏈嶅姟鍣?*: Jetty
- **缂栫爜**: UTF-8

### 缁勪欢瀹屾垚搴?
- 鉁?HTTP Server (Jetty + 闄愭祦)
- 鉁?MySQL (MyBatis-Plus + 澶氭暟鎹簮)
- 鉁?Redis (Spring Cache + Redisson)
- 鉁?ClickHouse (鍒嗘瀽鏁版嵁搴?
- 鉁?MongoDB (鏂囨。鏁版嵁搴?
- 鉁?RabbitMQ (娑堟伅闃熷垪)
- 鉁?Kafka (娑堟伅闃熷垪)
- 鉁?HTTP Client (Feign + 瀹归敊)

### 鏋舵瀯鐗圭偣
- **缁勪欢鍖?*: 姣忎釜缁勪欢鐙珛閰嶇疆鍜屽仴搴锋鏌?
- **鐩戞帶鍙嬪ソ**: 闆嗘垚Actuator + Micrometer + Prometheus
- **瀹瑰櫒鍖?*: 瀹屾暣鐨凞ocker鏀寔
- **璺ㄥ钩鍙?*: UTF-8缂栫爜鏀寔

## 馃殌 蹇€熷紑濮?

### 鍚姩椤圭洰
`ash
# 鏂瑰紡1: Maven鍚姩
mvn spring-boot:run

# 鏂瑰紡2: 鑴氭湰鍚姩 (Windows)
start-utf8.bat

# 鏂瑰紡3: Docker鍚姩
docker-compose up
`

### 璁块棶绔偣
- **搴旂敤**: http://localhost:8338/api
- **鍋ュ悍妫€鏌?*: http://localhost:8338/api/actuator/health
- **鐩戞帶鎸囨爣**: http://localhost:8338/api/actuator/metrics

## 馃搧 椤圭洰缁撴瀯

`
sprival/
鈹溾攢鈹€ src/main/java/com/soyokra/sprival/
鈹?  鈹溾攢鈹€ SprivalApplication.java          # 涓诲簲鐢ㄧ被
鈹?  鈹溾攢鈹€ config/                          # 閰嶇疆绫?(8涓粍浠?
鈹?  鈹溾攢鈹€ client/                          # Feign瀹㈡埛绔?
鈹?  鈹溾攢鈹€ service/                         # 涓氬姟鏈嶅姟
鈹?  鈹斺攢鈹€ controller/                      # 鎺у埗鍣?
鈹溾攢鈹€ src/main/resources/
鈹?  鈹溾攢鈹€ application.properties           # 搴旂敤閰嶇疆
鈹?  鈹溾攢鈹€ redisson.yml                     # Redisson閰嶇疆
鈹?  鈹斺攢鈹€ spy.properties                   # P6Spy閰嶇疆
鈹溾攢鈹€ dockers/                             # Docker閰嶇疆
鈹溾攢鈹€ docs/                                # 椤圭洰鏂囨。
鈹斺攢鈹€ scripts/                             # 鑴氭湰鏂囦欢
`

## 馃敡 寮€鍙戣鑼?

### 鍛藉悕瑙勮寖
- **閰嶇疆绫?*: Sprival + 缁勪欢鍚?+ Configuration
- **灞炴€х被**: Sprival + 缁勪欢鍚?+ Properties
- **鍋ュ悍妫€鏌?*: Sprival + 缁勪欢鍚?+ HealthIndicator

### 閰嶇疆妯″紡
- **閰嶇疆绫?*: @Configuration + @Bean
- **灞炴€х粦瀹?*: @ConfigurationProperties
- **鏉′欢閰嶇疆**: @ConditionalOnClass/@ConditionalOnBean

### 鍋ュ悍妫€鏌?
- 鎵€鏈夌粍浠堕兘瀹炵幇HealthIndicator
- 缁熶竴鐨勫紓甯稿鐞嗗拰闄嶇骇绛栫暐
- 闆嗘垚鍒癝pring Boot Actuator

## 馃摎 鏂囨。璧勬簮

- **椤圭洰鏂囨。**: docs/README.md
- **AI寮€鍙戣鑼?*: docs/ai-development/
- **缁勪欢鏂囨。**: docs/spring-*/
- **绯荤粺鐜**: docs/SYSTEM-ENVIRONMENT.md
- **缂栫爜瑙勮寖**: docs/ENCODING-STANDARDS.md

## 鈿狅笍 娉ㄦ剰浜嬮」

1. **鐗堟湰鍏煎**: 纭繚渚濊禆鐗堟湰涓嶴pring Boot 2.7.18鍏煎
2. **缂栫爜闂**: 鎵€鏈夋枃浠朵娇鐢║TF-8缂栫爜
3. **閰嶇疆鍐茬獊**: 娉ㄦ剰缁勪欢闂撮厤缃啿绐?
4. **鎬ц兘浼樺寲**: 鍚堢悊閰嶇疆杩炴帴姹犲拰瓒呮椂鍙傛暟
5. **瀹夊叏鑰冭檻**: 鏁忔劅閰嶇疆浣跨敤鐜鍙橀噺

## 馃幆 AI缂栫▼寤鸿

1. **鐞嗚В鏋舵瀯**: 鍏堜簡瑙ｆ暣浣撴灦鏋勫拰缁勪欢鍏崇郴
2. **閬靛惊瑙勮寖**: 鎸夌収椤圭洰鍛藉悕鍜岄厤缃鑼冨紑鍙?
3. **鍋ュ悍妫€鏌?*: 鏂扮粍浠跺繀椤诲疄鐜板仴搴锋鏌?
4. **鐩戞帶闆嗘垚**: 鑰冭檻鐩戞帶鍜屾寚鏍囨敹闆?
5. **鏂囨。鏇存柊**: 鍙婃椂鏇存柊鐩稿叧鏂囨。

---

*姝ゆ枃妗ｇ敱鑴氭湰鑷姩鐢熸垚锛岃瀹氭湡鏇存柊浠ヤ繚鎸佸噯纭€?
