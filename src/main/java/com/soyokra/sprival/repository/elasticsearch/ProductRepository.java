package com.soyokra.sprival.repository.elasticsearch;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import com.soyokra.sprival.entity.elasticsearch.ProductDocument;

/**
 * 产品文档Repository
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Repository
public interface ProductRepository extends ElasticsearchRepository<ProductDocument, String> {

    /**
     * 根据名称搜索产品
     */
    List<ProductDocument> findByNameContaining(String name);

    /**
     * 根据分类搜索产品
     */
    List<ProductDocument> findByCategory(String category);

    /**
     * 根据品牌搜索产品
     */
    List<ProductDocument> findByBrand(String brand);

    /**
     * 根据价格范围搜索产品
     */
    List<ProductDocument> findByPriceBetween(Double minPrice, Double maxPrice);

    /**
     * 根据状态搜索产品
     */
    List<ProductDocument> findByStatus(String status);

    /**
     * 根据名称和分类搜索产品
     */
    List<ProductDocument> findByNameContainingAndCategory(String name, String category);

    /**
     * 根据名称模糊搜索产品（分页）
     */
    Page<ProductDocument> findByNameContaining(String name, Pageable pageable);

    /**
     * 使用自定义查询搜索产品
     */
    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}, {\"term\": {\"status\": \"?1\"}}]}}")
    List<ProductDocument> findByNameAndStatus(String name, String status);

    /**
     * 搜索未删除的产品
     */
    List<ProductDocument> findByDeletedFalse();

    /**
     * 根据分类和价格范围搜索产品
     */
    List<ProductDocument> findByCategoryAndPriceBetween(String category, Double minPrice,
            Double maxPrice);
}
