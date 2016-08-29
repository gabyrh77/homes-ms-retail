package com.tenx.ms.retail.order.domain;

import com.tenx.ms.retail.order.util.OrderDetailStatusEnum;
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

/**
 * Created by goropeza on 28/08/16.
 */
@Entity
@Table(name = "order_detail", uniqueConstraints = @UniqueConstraint(name = "unique_order_product_status",
    columnNames = {"detail_order_id", "detail_product_id", "detail_status"}))
public class OrderDetailEntity {
    private Long id;
    private OrderEntity order;
    private ProductEntity product;
    private OrderDetailStatusEnum status;
    private Long count;

    public OrderDetailEntity() {}

    public OrderDetailEntity(OrderEntity order, ProductEntity product, OrderDetailStatusEnum status, Long count) {
        this.order = order;
        this.product = product;
        this.status = status;
        this.count = count;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "detail_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "detail_order_id", nullable = false)
    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @ManyToOne
    @JoinColumn(name = "detail_product_id", nullable = false)
    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "detail_status", nullable = false)
    public OrderDetailStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderDetailStatusEnum status) {
        this.status = status;
    }

    @Column(name = "detail_product_count", nullable = false)
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
