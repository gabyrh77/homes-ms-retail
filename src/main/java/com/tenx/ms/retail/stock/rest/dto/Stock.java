package com.tenx.ms.retail.stock.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by goropeza on 27/08/16.
 */
@ApiModel("Stock")
public class Stock {
    @ApiModelProperty(value = "Product Id", readOnly = true)
    private Long productId;

    @ApiModelProperty(value = "Store Id", readOnly = true)
    private Long storeId;

    @ApiModelProperty(value = "Stock count", required = true)
    @NotNull
    private Long count;

    public Stock() {}

    public Stock(Long productId, Long storeId, Long count) {
        this.productId = productId;
        this.storeId = storeId;
        this.count = count;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
