package com.tenx.ms.retail.order.util;

import com.tenx.ms.retail.client.util.ClientConverter;
import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.rest.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by goropeza on 29/08/16.
 */
@Service
public class OrderConverter {
    @Autowired
    private ClientConverter clientConverter;

    public Order repositoryToApiModel(OrderEntity entity) {
        if (entity != null) {
            return new Order(entity.getId(), entity.getStore().getId(), clientConverter.repositoryToApiModel(entity.getClient()), entity.getCreatedDate(), entity.getStatus());
        }
        return null;
    }
}
