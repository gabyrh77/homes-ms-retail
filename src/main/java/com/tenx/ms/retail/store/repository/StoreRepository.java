package com.tenx.ms.retail.store.repository;

import com.tenx.ms.retail.store.domain.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by goropeza on 24/08/16.
 */
@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    StoreEntity findByName(String name);
}
