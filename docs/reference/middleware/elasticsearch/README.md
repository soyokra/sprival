# Spring Elasticsearch 模块

## 概述

Spring Elasticsearch 模块提供了完整的 Elasticsearch 搜索引擎集成解决方案，包括文档存储、全文搜索、聚合分析、监控告警等功能。该模块基于 Spring Data Elasticsearch 和 Elasticsearch Java High Level REST Client，为 Sprival 项目提供高性能、高可用的搜索服务。

## 核心特性

- ✅ **文档存储**: 基于 Spring Data Elasticsearch 的文档存储和检索
- ✅ **全文搜索**: 支持中文分词、模糊搜索、精确匹配等多种搜索方式
- ✅ **聚合分析**: 支持统计、分组、范围等复杂聚合查询
- ✅ **连接池管理**: 高性能连接池配置和监控
- ✅ **健康检查**: 集群状态监控和健康检查
- ✅ **监控集成**: 与 Prometheus + Grafana 无缝集成
- ✅ **集群支持**: 支持单节点、集群、云服务等多种部署模式
- ✅ **安全认证**: 支持用户名密码、API Key、SSL 等认证方式

## 组件清单

### 核心组件
- [spring-boot-starter-data-elasticsearch](https://spring.io/projects/spring-data-elasticsearch) - Spring Data Elasticsearch 集成
- [elasticsearch-rest-high-level-client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/index.html) - Elasticsearch Java 高级 REST 客户端
- [elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html) - Elasticsearch 搜索引擎

### 功能组件
- **文档管理**: Spring Data Elasticsearch Repository
- **搜索服务**: 全文搜索、聚合查询、原生查询
- **连接池**: HTTP 连接池管理
- **健康检查**: 集群状态监控
- **监控指标**: Micrometer 指标收集

## 快速开始

### 1. 添加依赖

项目已在 `pom.xml` 中配置了所需依赖：

```xml
<!-- Spring Data Elasticsearch -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

### 2. 基础配置

在 `application.properties` 中配置 Elasticsearch 连接信息：

```properties
# 基础连接配置
spring.data.elasticsearch.repositories.enabled = true
spring.elasticsearch.rest.uris = http://localhost:9200
spring.elasticsearch.rest.connection-timeout = 5s
spring.elasticsearch.rest.read-timeout = 10s

# Sprival Elasticsearch 增强配置
sprival.elasticsearch.enabled = true
sprival.elasticsearch.cluster-name = sprival-cluster
sprival.elasticsearch.nodes = localhost:9200
sprival.elasticsearch.connect-timeout = 5000
sprival.elasticsearch.read-timeout = 10000
sprival.elasticsearch.max-connections = 100
sprival.elasticsearch.max-connections-per-route = 10
```

### 3. 创建文档实体

```java
@Data
@Document(indexName = "sprival_product")
public class ProductDocument {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    @Field(type = FieldType.Double)
    private BigDecimal price;
    
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

### 4. 创建 Repository

```java
@Repository
public interface ProductRepository extends ElasticsearchRepository<ProductDocument, String> {
    
    List<ProductDocument> findByNameContaining(String name);
    
    List<ProductDocument> findByCategory(String category);
    
    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}, {\"term\": {\"status\": \"?1\"}}]}}")
    List<ProductDocument> findByNameAndStatus(String name, String status);
}
```

### 5. 创建服务类

```java
@Service
public class ElasticsearchProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public ProductDocument save(ProductDocument product) {
        return productRepository.save(product);
    }
    
    public List<ProductDocument> findByName(String name) {
        return productRepository.findByNameContaining(name);
    }
}
```

## 配置详解

### 基础配置

#### 连接配置
```properties
# Elasticsearch 节点地址
sprival.elasticsearch.nodes = localhost:9200,localhost:9201,localhost:9202

# 连接超时时间（毫秒）
sprival.elasticsearch.connect-timeout = 5000

# 读取超时时间（毫秒）
sprival.elasticsearch.read-timeout = 10000

# 最大连接数
sprival.elasticsearch.max-connections = 100

# 每个路由的最大连接数
sprival.elasticsearch.max-connections-per-route = 10
```

#### 认证配置
```properties
# 用户名密码认证
sprival.elasticsearch.username = elastic
sprival.elasticsearch.password = workdock

# SSL 配置
sprival.elasticsearch.ssl = true
sprival.elasticsearch.verify-ssl = true
```

#### 索引配置
```properties
# 默认分片数
sprival.elasticsearch.index.number-of-shards = 1

# 默认副本数
sprival.elasticsearch.index.number-of-replicas = 1

# 刷新间隔
sprival.elasticsearch.index.refresh-interval = 1s

# 索引前缀
sprival.elasticsearch.index.prefix = sprival
```

## 使用示例

### 1. 文档操作

#### 保存文档
```java
@Autowired
private ProductRepository productRepository;

// 保存单个文档
ProductDocument product = new ProductDocument();
product.setName("iPhone 15 Pro");
product.setDescription("苹果最新旗舰手机");
product.setCategory("手机");
product.setPrice(new BigDecimal("7999.00"));
productRepository.save(product);

// 批量保存文档
List<ProductDocument> products = Arrays.asList(product1, product2, product3);
productRepository.saveAll(products);
```

#### 查询文档
```java
// 根据ID查询
Optional<ProductDocument> product = productRepository.findById("1");

// 根据名称模糊查询
List<ProductDocument> products = productRepository.findByNameContaining("iPhone");

// 根据分类查询
List<ProductDocument> phones = productRepository.findByCategory("手机");

// 自定义查询
List<ProductDocument> results = productRepository.findByNameAndStatus("iPhone", "active");
```

### 2. 搜索功能

#### 基础搜索
```java
@Service
public class SearchService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // 分页搜索
    public Page<ProductDocument> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContaining(keyword, pageable);
    }
    
    // 多条件搜索
    public List<ProductDocument> searchByCategoryAndPrice(String category, Double minPrice, Double maxPrice) {
        return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);
    }
}
```

## 监控和健康检查

### 1. 健康检查端点

访问健康检查端点：
```bash
# 应用健康检查
curl http://localhost:8338/api/actuator/health

# Elasticsearch 健康检查
curl http://localhost:8338/api/actuator/health/elasticsearch
```

### 2. 监控指标

#### 应用指标
- `elasticsearch.requests.total` - 请求总数
- `elasticsearch.requests.duration` - 请求耗时
- `elasticsearch.connections.active` - 活跃连接数
- `elasticsearch.connections.idle` - 空闲连接数

#### 集群指标
- `elasticsearch.cluster.status` - 集群状态
- `elasticsearch.cluster.nodes` - 节点数量
- `elasticsearch.cluster.shards` - 分片数量
- `elasticsearch.cluster.indices` - 索引数量

## 测试接口

项目提供了完整的测试接口，可以通过以下端点测试 Elasticsearch 功能：

### 1. 连接测试
```bash
GET /api/elasticsearch/test
```

### 2. 产品管理
```bash
# 创建产品
POST /api/elasticsearch/products
Content-Type: application/json

{
  "name": "iPhone 15 Pro",
  "description": "苹果最新旗舰手机",
  "category": "手机",
  "brand": "Apple",
  "price": 7999.00,
  "stock": 100,
  "status": "active"
}

# 搜索产品
GET /api/elasticsearch/products/search?keyword=iPhone&page=1&size=10

# 根据分类搜索
GET /api/elasticsearch/products/category/手机

# 根据价格范围搜索
GET /api/elasticsearch/products/price?minPrice=1000&maxPrice=5000

# 获取所有产品
GET /api/elasticsearch/products

# 删除产品
DELETE /api/elasticsearch/products/{id}
```

### 3. 创建测试数据
```bash
POST /api/elasticsearch/test-data
```

## 部署指南

### 1. Docker 部署

```yaml
# docker-compose.yml
version: '3.8'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.17.0
    container_name: sprival-elasticsearch
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms1g -Xmx1g"
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    networks:
      - sprival-network

volumes:
  elasticsearch_data:

networks:
  sprival-network:
    driver: bridge
```

## 更新历史

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2025-01-08 | 1.0 | 初始创建，完整的 Elasticsearch 集成方案 | AI Assistant |

## 相关链接

- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Spring Data Elasticsearch 文档](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [Elasticsearch Java API 文档](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/index.html)

---

*本文档提供了完整的 Elasticsearch 集成方案，包括配置、使用、监控和部署指南。*