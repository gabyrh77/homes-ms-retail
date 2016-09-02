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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Created by goropeza on 24/08/16.
 */
@Entity
@Table(name = "product",
    uniqueConstraints = @UniqueConstraint(name = "unique_product_sku_store", columnNames = {"sku", "store_id"}))
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Size(min = 5, max = 10)
    @Pattern(regexp = "[A-Za-z0-9]*")
    @Column(name = "sku", nullable = false)
    @NotNull
    private String sku;

    @Size(min = 1, max = 50)
    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Size(min = 1, max = 200)
    @Column(name = "description", nullable = false)
    @NotNull
    private String description;

    @Column(name = "price", length = 15, precision = 2, nullable = false)
    @NotNull
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @NotNull
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

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }
}
