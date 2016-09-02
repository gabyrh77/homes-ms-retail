package com.tenx.ms.retail.order.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by goropeza on 28/08/16.
 */
@ApiModel("OrderDetail")
public class OrderDetail {
    @ApiModelProperty(value = "Ordered product id", required = true)
    @NotNull
    private Long productId;

    @ApiModelProperty(value = "Ordered product count", required = true)
    @NotNull
    private Long count;

    public OrderDetail() {}

    public OrderDetail(Long productId, Long count) {
        this.productId = productId;
        this.count = count;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
