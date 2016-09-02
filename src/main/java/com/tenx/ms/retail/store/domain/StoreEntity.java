package com.tenx.ms.retail.store.domain;


import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
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
@Table(name = "store", uniqueConstraints = @UniqueConstraint(name = "unique_store_name", columnNames = {"name"}))
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Size(min = 1, max = 50)
    @Column(nullable = false, name = "name")
    @NotNull
    private String name;

    public StoreEntity() {}

    public StoreEntity(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
