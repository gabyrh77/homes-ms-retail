package com.tenx.ms.retail.order.domain;

import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.order.util.OrderStatusConverter;
import com.tenx.ms.retail.order.domain.enums.OrderStatusEnum;
import com.tenx.ms.retail.store.domain.StoreEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.LocalDateTime;

/**
 * Created by goropeza on 28/08/16.
 */
@Entity
@Table(name = "order_table")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "status", nullable = false)
    @Convert(converter = OrderStatusConverter.class)
    private String status;

    public OrderEntity() {}

    public OrderEntity(StoreEntity store, ClientEntity client) {
        this.store = store;
        this.client = client;
        this.createdDate = LocalDateTime.now();
        this.status = OrderStatusEnum.ORDERED.toJson();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
