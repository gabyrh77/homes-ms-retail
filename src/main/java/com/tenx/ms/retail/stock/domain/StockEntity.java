package com.tenx.ms.retail.stock.domain;

import com.tenx.ms.retail.product.domain.ProductEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.GenerationType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * Created by goropeza on 26/08/16.
 */
@Entity
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(name = "unique_product_stock", columnNames = {"product_id"}))
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private ProductEntity product;

    @Column(name = "product_count", nullable = false)
    @NotNull
    private Long existence;

    public StockEntity() {}

    public StockEntity(ProductEntity stockProduct, Long existence) {
        this.product = stockProduct;
        this.existence = existence;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public Long getExistence() {
        return existence;
    }

    public void setExistence(Long existence) {
        this.existence = existence;
    }

}
