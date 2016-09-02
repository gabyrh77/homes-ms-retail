package com.tenx.ms.retail.stock.repository;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.stock.domain.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by goropeza on 27/08/16.
 */
public interface StockRepository extends JpaRepository<StockEntity, Long> {
    Optional<StockEntity> findByProduct(ProductEntity product);
}
