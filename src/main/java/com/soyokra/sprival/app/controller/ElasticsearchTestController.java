package com.soyokra.sprival.app.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.app.data.elasticsearch.document.ProductDocument;
import com.soyokra.sprival.app.data.elasticsearch.service.ElasticsearchProductService;
import lombok.extern.slf4j.Slf4j;

/**
 * Elasticsearch测试控制器 用于测试Elasticsearch功能
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@ConditionalOnProperty(name = "sprival.elasticsearch.enabled", havingValue = "true",
        matchIfMissing = true)
@RestController
@RequestMapping("/api/elasticsearch")
public class ElasticsearchTestController {

    @Autowired
    private ElasticsearchProductService productService;

    /**
     * 测试Elasticsearch连接
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();

        try {
            long count = productService.count();
            response.put("success", true);
            response.put("message", "Elasticsearch连接正常");
            response.put("productCount", count);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Elasticsearch连接测试失败", e);
            response.put("success", false);
            response.put("message", "Elasticsearch连接失败: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 创建测试产品
     */
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductDocument product) {
        Map<String, Object> response = new HashMap<>();

        try {
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            ProductDocument savedProduct = productService.save(product);

            response.put("success", true);
            response.put("message", "产品创建成功");
            response.put("product", savedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("创建产品失败", e);
            response.put("success", false);
            response.put("message", "创建产品失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据ID获取产品
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<ProductDocument> product = productService.findById(id);
            if (product.isPresent()) {
                response.put("success", true);
                response.put("product", product.get());
            } else {
                response.put("success", false);
                response.put("message", "产品不存在");
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取产品失败", e);
            response.put("success", false);
            response.put("message", "获取产品失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据名称搜索产品
     */
    @GetMapping("/products/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (page > 0) {
                // 分页搜索
                Page<ProductDocument> products =
                        productService.findByNameWithPage(keyword, page - 1, size);
                response.put("success", true);
                response.put("products", products.getContent());
                response.put("totalElements", products.getTotalElements());
                response.put("totalPages", products.getTotalPages());
                response.put("currentPage", page);
                response.put("pageSize", size);
            } else {
                // 普通搜索
                List<ProductDocument> products = productService.findByName(keyword);
                response.put("success", true);
                response.put("products", products);
                response.put("totalElements", products.size());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("搜索产品失败", e);
            response.put("success", false);
            response.put("message", "搜索产品失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据分类搜索产品
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(
            @PathVariable String category) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<ProductDocument> products = productService.findByCategory(category);
            response.put("success", true);
            response.put("products", products);
            response.put("totalElements", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("根据分类搜索产品失败", e);
            response.put("success", false);
            response.put("message", "根据分类搜索产品失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据价格范围搜索产品
     */
    @GetMapping("/products/price")
    public ResponseEntity<Map<String, Object>> getProductsByPriceRange(
            @RequestParam Double minPrice, @RequestParam Double maxPrice) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<ProductDocument> products = productService.findByPriceRange(minPrice, maxPrice);
            response.put("success", true);
            response.put("products", products);
            response.put("totalElements", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("根据价格范围搜索产品失败", e);
            response.put("success", false);
            response.put("message", "根据价格范围搜索产品失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取所有产品
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<ProductDocument> products = productService.findAll();
            response.put("success", true);
            response.put("products", products);
            response.put("totalElements", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取所有产品失败", e);
            response.put("success", false);
            response.put("message", "获取所有产品失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            productService.deleteById(id);
            response.put("success", true);
            response.put("message", "产品删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除产品失败", e);
            response.put("success", false);
            response.put("message", "删除产品失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 创建测试数据
     */
    @PostMapping("/test-data")
    public ResponseEntity<Map<String, Object>> createTestData() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 创建测试产品数据
            ProductDocument product1 = new ProductDocument();
            product1.setName("iPhone 15 Pro");
            product1.setDescription("苹果最新旗舰手机，配备A17 Pro芯片");
            product1.setCategory("手机");
            product1.setBrand("Apple");
            product1.setPrice(new BigDecimal("7999.00"));
            product1.setStock(100);
            product1.setStatus("active");
            product1.setCreateTime(LocalDateTime.now());
            product1.setUpdateTime(LocalDateTime.now());
            product1.setDeleted(false);

            ProductDocument product2 = new ProductDocument();
            product2.setName("MacBook Pro 16英寸");
            product2.setDescription("苹果专业级笔记本电脑，适合开发者和设计师");
            product2.setCategory("电脑");
            product2.setBrand("Apple");
            product2.setPrice(new BigDecimal("15999.00"));
            product2.setStock(50);
            product2.setStatus("active");
            product2.setCreateTime(LocalDateTime.now());
            product2.setUpdateTime(LocalDateTime.now());
            product2.setDeleted(false);

            ProductDocument product3 = new ProductDocument();
            product3.setName("AirPods Pro");
            product3.setDescription("苹果无线降噪耳机");
            product3.setCategory("耳机");
            product3.setBrand("Apple");
            product3.setPrice(new BigDecimal("1999.00"));
            product3.setStock(200);
            product3.setStatus("active");
            product3.setCreateTime(LocalDateTime.now());
            product3.setUpdateTime(LocalDateTime.now());
            product3.setDeleted(false);

            // 保存测试数据
            productService.save(product1);
            productService.save(product2);
            productService.save(product3);

            response.put("success", true);
            response.put("message", "测试数据创建成功");
            response.put("createdCount", 3);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("创建测试数据失败", e);
            response.put("success", false);
            response.put("message", "创建测试数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
