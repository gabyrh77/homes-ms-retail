package com.tenx.ms.retail.order.repository;

import com.tenx.ms.retail.order.domain.OrderDetailEntity;
import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.util.OrderDetailStatusEnum;
import com.tenx.ms.retail.product.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by goropeza on 28/08/16.
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    OrderDetailEntity findByOrderAndProductAndStatus(OrderEntity order, ProductEntity productEntity, OrderDetailStatusEnum status);
}
