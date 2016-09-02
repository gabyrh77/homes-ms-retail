package com.tenx.ms.retail.order.repository;

import com.tenx.ms.retail.order.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by goropeza on 28/08/16.
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
