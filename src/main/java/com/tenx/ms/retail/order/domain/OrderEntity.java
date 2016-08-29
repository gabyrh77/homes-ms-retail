package com.tenx.ms.retail.order.domain;

import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.order.util.OrderStatusConverter;
import com.tenx.ms.retail.order.util.OrderStatusEnum;
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
import java.util.Date;

/**
 * Created by goropeza on 28/08/16.
 */
@Entity
@Table(name = "order_table")
public class OrderEntity {
    private Long id;
    private StoreEntity store;
    private ClientEntity client;
    private Date createdDate;
    private String status;

    public OrderEntity() {}

    public OrderEntity(StoreEntity store, ClientEntity client) {
        this.store = store;
        this.client = client;
        this.createdDate = new Date();
        this.status = OrderStatusEnum.ORDERED.toJson();
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    public Long getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "order_store_id", nullable = false)
    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    @ManyToOne
    @JoinColumn(name = "order_client_id", nullable = false)
    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    @Column(name = "order_created_date", nullable = false)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "order_status", nullable = false)
    @Convert(converter = OrderStatusConverter.class)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
