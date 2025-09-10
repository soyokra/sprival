package com.soyokra.sprival.app.data.elasticsearch.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.soyokra.sprival.app.data.elasticsearch.document.ProductDocument;
import com.soyokra.sprival.app.data.elasticsearch.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Elasticsearch产品服务类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "sprival.elasticsearch.enabled", havingValue = "true",
        matchIfMissing = true)
public class ElasticsearchProductService {

    @Autowired
    private ProductRepository productRepository;


    /**
     * 保存产品
     */
    public ProductDocument save(ProductDocument product) {
        log.info("保存产品: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * 批量保存产品
     */
    public Iterable<ProductDocument> saveAll(List<ProductDocument> products) {
        log.info("批量保存产品，数量: {}", products.size());
        return productRepository.saveAll(products);
    }

    /**
     * 根据ID查找产品
     */
    public Optional<ProductDocument> findById(String id) {
        return productRepository.findById(id);
    }

    /**
     * 根据名称搜索产品
     */
    public List<ProductDocument> findByName(String name) {
        log.info("根据名称搜索产品: {}", name);
        return productRepository.findByNameContaining(name);
    }

    /**
     * 根据分类搜索产品
     */
    public List<ProductDocument> findByCategory(String category) {
        log.info("根据分类搜索产品: {}", category);
        return productRepository.findByCategory(category);
    }

    /**
     * 根据品牌搜索产品
     */
    public List<ProductDocument> findByBrand(String brand) {
        log.info("根据品牌搜索产品: {}", brand);
        return productRepository.findByBrand(brand);
    }

    /**
     * 根据价格范围搜索产品
     */
    public List<ProductDocument> findByPriceRange(Double minPrice, Double maxPrice) {
        log.info("根据价格范围搜索产品: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * 分页搜索产品
     */
    public Page<ProductDocument> findByNameWithPage(String name, int page, int size) {
        log.info("分页搜索产品: {}, 页码: {}, 大小: {}", name, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContaining(name, pageable);
    }

    /**
     * 使用自定义查询搜索产品
     */
    public List<ProductDocument> findByNameAndStatus(String name, String status) {
        log.info("根据名称和状态搜索产品: {}, {}", name, status);
        return productRepository.findByNameAndStatus(name, status);
    }

    /**
     * 搜索所有产品
     */
    public List<ProductDocument> findAll() {
        log.info("搜索所有产品");
        return (List<ProductDocument>) productRepository.findAll();
    }

    /**
     * 删除产品
     */
    public void deleteById(String id) {
        log.info("删除产品: {}", id);
        productRepository.deleteById(id);
    }

    /**
     * 删除产品
     */
    public void delete(ProductDocument product) {
        log.info("删除产品: {}", product.getName());
        productRepository.delete(product);
    }

    /**
     * 使用原生查询搜索产品（简化版本）
     */
    public List<ProductDocument> searchWithNativeQuery(String keyword) {
        log.info("使用原生查询搜索产品: {}", keyword);
        // 简化实现，直接使用Repository方法
        return productRepository.findByNameContaining(keyword);
    }

    /**
     * 获取产品总数
     */
    public long count() {
        return productRepository.count();
    }

    /**
     * 检查产品是否存在
     */
    public boolean existsById(String id) {
        return productRepository.existsById(id);
    }
}
