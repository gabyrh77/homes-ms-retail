package com.tenx.ms.retail.stock.repository;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.stock.domain.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by goropeza on 27/08/16.
 */
public interface StockRepository extends JpaRepository<StockEntity, Long> {
    StockEntity findByProduct(ProductEntity product);
}
