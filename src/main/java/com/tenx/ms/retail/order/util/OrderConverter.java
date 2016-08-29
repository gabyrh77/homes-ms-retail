package com.tenx.ms.retail.order.util;

import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.rest.dto.Order;
import org.springframework.stereotype.Service;

/**
 * Created by goropeza on 29/08/16.
 */
@Service
public class OrderConverter {
    public Order repositoryToApiModel(OrderEntity entity) {
        if (entity != null) {
            Order result = new Order(entity.getId(), entity.getStore().getId(), entity.getCreatedDate(), entity.getStatus());
            result.setFirstName(entity.getClient().getFirstName());
            result.setEmail(entity.getClient().getEmail());
            result.setLastName(entity.getClient().getLastName());
            result.setPhone(entity.getClient().getPhone());
            return result;
        }
        return null;
    }
}
