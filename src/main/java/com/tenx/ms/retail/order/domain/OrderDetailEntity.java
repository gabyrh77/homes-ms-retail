package com.tenx.ms.retail.order.domain;

import com.tenx.ms.retail.order.domain.enums.OrderDetailStatusEnum;
import com.tenx.ms.retail.product.domain.ProductEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.GeneratedValue;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;

/**
 * Created by goropeza on 28/08/16.
 */
@Entity
@Table(name = "order_detail", uniqueConstraints = @UniqueConstraint(name = "unique_order_product_status",
    columnNames = {"order_id", "product_id", "status"}))
public class OrderDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private ProductEntity product;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    @NotNull
    private OrderDetailStatusEnum status;

    @Column(name = "product_count", nullable = false)
    @NotNull
    private Long count;

    public OrderDetailEntity() {}

    public OrderDetailEntity(OrderEntity order, ProductEntity product, OrderDetailStatusEnum status, Long count) {
        this.order = order;
        this.product = product;
        this.status = status;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public OrderDetailStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderDetailStatusEnum status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
