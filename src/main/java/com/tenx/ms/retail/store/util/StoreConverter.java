package com.tenx.ms.retail.store.util;

import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.rest.dto.Store;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Created by goropeza on 25/08/16.
 */
@Component
public class StoreConverter {

    public Store repositoryToApiModel(StoreEntity storeEntity) {
        return new Store(storeEntity.getId(), storeEntity.getName());
    }

    public StoreEntity apiModelToRepository(Store store) {
        return new StoreEntity(store.getName());
    }

    public Function<StoreEntity, Store> entityToStore = x -> new
        Store(x.getId(), x.getName());
}
