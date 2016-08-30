package com.tenx.ms.retail.order.repository;

import com.tenx.ms.retail.order.domain.OrderDetailEntity;
import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.util.OrderDetailStatusEnum;
import com.tenx.ms.retail.product.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by goropeza on 28/08/16.
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    Optional<OrderDetailEntity> findByOrderAndProductAndStatus(OrderEntity order, ProductEntity productEntity, OrderDetailStatusEnum status);
}
