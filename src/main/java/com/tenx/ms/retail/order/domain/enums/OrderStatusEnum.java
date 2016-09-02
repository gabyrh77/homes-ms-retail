package com.tenx.ms.retail.order.domain.enums;


import com.tenx.ms.commons.rest.BaseValueNameEnum;
import com.tenx.ms.commons.util.EnumUtil;

/**
 * Created by goropeza on 28/08/16.
 */
public enum OrderStatusEnum implements BaseValueNameEnum<OrderStatusEnum> {
    ORDERED(1, "ORDERED"), PACKING(2, "PACKING"), SHIPPED(3, "SHIPPED"), INVALID(EnumUtil.INVALID_ENUM_VALUE, EnumUtil.INVALID_ENUM_MSG);
    private int value;
    private String label;

    OrderStatusEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String toJson() {
        return label;
    }

    @Override
    public OrderStatusEnum getInvalidEnum() {
        return INVALID;
    }

}
