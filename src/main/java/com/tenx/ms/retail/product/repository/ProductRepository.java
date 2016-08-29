package com.tenx.ms.retail.product.repository;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.store.domain.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by goropeza on 24/08/16.
 */
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    ProductEntity findBySkuAndStore(String sku, StoreEntity store);
    ProductEntity findByNameAndStore(String name, StoreEntity store);
    ProductEntity findByIdAndStore(Long id, StoreEntity store);
    List<ProductEntity> findByStore(StoreEntity store);
}
