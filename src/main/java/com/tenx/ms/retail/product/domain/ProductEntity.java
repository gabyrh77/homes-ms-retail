package com.tenx.ms.retail.product.domain;

import com.tenx.ms.retail.store.domain.StoreEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.UniqueConstraint;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.GenerationType;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Created by goropeza on 24/08/16.
 */
@Entity
@Table(name = "product",
    uniqueConstraints = @UniqueConstraint(name = "unique_product_sku_store", columnNames = {"product_sku", "product_store_id"})
)
public class ProductEntity {

    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private StoreEntity store;

    public ProductEntity() {}

    public ProductEntity(String sku, String name, String description, BigDecimal price, StoreEntity store) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.store = store;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    public Long getId() {
        return id;
    }


    @Size(min = 5, max = 10)
    @Pattern(regexp = "[A-Za-z0-9]*")
    @Column(name = "product_sku", nullable = false)
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Size(min = 1, max = 50)
    @Column(name = "product_name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(min = 1, max = 200)
    @Column(name = "product_description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "product_price", length = 15, precision = 2, nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @ManyToOne
    @JoinColumn(name = "product_store_id", nullable = false)
    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }
}
