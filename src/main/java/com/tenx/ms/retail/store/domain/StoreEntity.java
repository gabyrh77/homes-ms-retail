package com.tenx.ms.retail.store.domain;


import javax.validation.constraints.Size;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * Created by goropeza on 24/08/16.
 */
@Entity
@Table(name = "store")
public class StoreEntity {

    private Long id;
    private String name;

    public StoreEntity() {}

    public StoreEntity(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "store_id")
    public Long getId() {
        return id;
    }

    @Size(min = 1, max = 50)
    @Column(unique = true, nullable = false, name = "store_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
