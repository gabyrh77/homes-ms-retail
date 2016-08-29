package com.tenx.ms.retail.store.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by goropeza on 25/08/16.
 */
@ApiModel("Store")
public class Store {

    @ApiModelProperty(value = "Store Id", readOnly = true)
    private Long storeId;

    @NotNull
    @ApiModelProperty(value = "Store Name", required = true)
    private String name;

    public Store() {}

    public Store(String name) {
        this.name = name;
    }

    public Store(Long storeId, String name) {
        this.storeId = storeId;
        this.name = name;
    }

    public Long getStoreId(){
        return  this.storeId;
    }

    public String getName(){
        return this.name;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
