package com.tenx.ms.retail.order.rest.dto;

import com.tenx.ms.commons.validation.constraints.EnumValid;
import com.tenx.ms.retail.order.util.OrderStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by goropeza on 28/08/16.
 */
@ApiModel("Order")
public class Order {

    @ApiModelProperty(value = "Order Id", readOnly = true)
    private Long orderId;

    @ApiModelProperty(value = "Store Id", readOnly = true)
    private Long storeId;

    @ApiModelProperty(value = "Created date", readOnly = true)
    private Date createdDate;

    @ApiModelProperty(value = "Order status", readOnly = true)
    @EnumValid(enumClass = OrderStatusEnum.class)
    private String status;

    @ApiModelProperty(value = "Ordered products", required = true)
    @NotNull
    private List<OrderDetail> products;

    @ApiModelProperty(value = "Client first name", required = true)
    @NotNull
    private String firstName;

    @ApiModelProperty(value = "Client last name", required = true)
    @NotNull
    private String lastName;

    @ApiModelProperty(value = "Client email", required = true)
    @NotNull
    private String email;

    @ApiModelProperty(value = "Client phone", required = true)
    @NotNull
    private String phone;

    @ApiModelProperty(value = "Backordered products", readOnly = true)
    private List<OrderDetail> backorder;

    @ApiModelProperty(value = "Not found products", readOnly = true)
    private List<OrderDetail> errors;

    public Order() {}

    public Order(Long orderId, Long storeId, Date createdDate, String status) {
        this.orderId = orderId;
        this.storeId = storeId;
        this.createdDate = createdDate;
        this.status = status;
        products = new ArrayList<>();
        backorder = new ArrayList<>();
        errors = new ArrayList<>();
    }

    public Long getStoreId() {
        return storeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderDetail> getProducts() {
        return products;
    }

    public void setProducts(List<OrderDetail> products) {
        this.products = products;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<OrderDetail> getBackorder() {
        return backorder;
    }

    public void setBackorder(List<OrderDetail> backorder) {
        this.backorder = backorder;
    }

    public List<OrderDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<OrderDetail> errors) {
        this.errors = errors;
    }
}
