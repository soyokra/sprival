# 快速开始

## 拉取项目
```bash
git clone https://github.com/soyokra/sprival.git
```

## 启动服务

### 启动中间件服务
```
cd docker/sprival-middleware
docker compose up -d
```

### 启动日志服务
```
cd docker/sprival-logging
docker compose up setup
docker compose up -d
```

- Kibana：http://localhost:5601/
- ElasticSearch：http://localhost:9200/

### 启动监控服务
```
cd docker/sprival-monitoring
docker compose up setup
docker compose up -d
```

- Grafana：http://localhost:3000/
- Prometheus：http://localhost:9090/

## 启动应用部署服务
```
cd docker/sprival-deployment
docker compose up -d
```
- gitlab: http://localhost/

## 启动应用
```
cd sprival && mvn clean package
java -jar target/sprival-*.jar
```
http://localhost:8338/health
