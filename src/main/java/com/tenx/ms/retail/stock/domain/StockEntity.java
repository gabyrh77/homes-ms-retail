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

/**
 * Created by goropeza on 26/08/16.
 */
@Entity
@Table(name = "stock")
public class StockEntity {

    private Long id;
    private ProductEntity stockProduct;
    private Long existence;

    public StockEntity() {}

    public StockEntity(ProductEntity stockProduct, Long existence) {
        this.stockProduct = stockProduct;
        this.existence = existence;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stock_id")
    public Long getId() {
        return id;
    }

    @OneToOne
    @JoinColumn(name = "stock_product_id", unique = true, nullable = false)
    public ProductEntity getProduct() {
        return stockProduct;
    }

    public void setProduct(ProductEntity product) {
        this.stockProduct = product;
    }

    @Column(name = "stock_count", nullable = false)
    public Long getExistence() {
        return existence;
    }

    public void setExistence(Long existence) {
        this.existence = existence;
    }


}
