package com.tenx.ms.retail.store.util;

import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.rest.dto.Store;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Created by goropeza on 25/08/16.
 */
@Service
public class StoreConverter {

    public Store repositoryToApiModel(StoreEntity storeEntity) {
        if (storeEntity != null) {
            return new Store(storeEntity.getId(), storeEntity.getName());
        }
        return null;
    }

    public StoreEntity apiModelToRepository(Store store) {
        if (store != null) {
            return new StoreEntity(store.getName());
        }
        return null;
    }

    public Function<StoreEntity, Store> entityToStore = x -> new
        Store(x.getId(), x.getName());
}