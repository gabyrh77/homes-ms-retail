package com.tenx.ms.retail.order.util;

import com.tenx.ms.commons.util.EnumUtil;

import javax.persistence.AttributeConverter;

/**
 * Created by goropeza on 28/08/16.
 */
public class OrderStatusConverter implements AttributeConverter<String, Integer> {

    @Override
    public Integer convertToDatabaseColumn(String s) {
        OrderStatusEnum statusEnum = EnumUtil.getEnumFromString(OrderStatusEnum.class, s);
        return statusEnum == null ? null: statusEnum.getValue();
    }

    @Override
    public String convertToEntityAttribute(Integer integer) {
        if (integer != null) {
            OrderStatusEnum statusEnum = EnumUtil.getEnumFromInteger(OrderStatusEnum.class, integer);
            if (statusEnum != null) {
                statusEnum.toJson();
            }
        }
        return null;
    }
}
