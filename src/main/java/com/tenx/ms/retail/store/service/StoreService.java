package com.tenx.ms.retail.store.service;

import com.tenx.ms.retail.store.util.StoreConverter;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import com.tenx.ms.retail.store.rest.dto.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by goropeza on 25/08/16.
 */
@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreConverter storeConverter;

    public Store findStoreById(Long id) {
        if (id != null) {
            return storeConverter.repositoryToApiModel(storeRepository.findOne(id));
        }
        return null;
    }

    public Store findStoreByName(String name) {
        if (name != null) {
            return storeConverter.repositoryToApiModel(storeRepository.findByName(name));
        }
        return null;
    }

    public List<Store> findAllStores() {
        return storeRepository.findAll().stream().map(storeConverter.entityToStore).
            collect(Collectors.toList());
    }

    public Store insertStore(Store store) {
        StoreEntity entity = storeConverter.apiModelToRepository(store);
        if (entity != null) {
            entity = storeRepository.save(entity);
        }
        return storeConverter.repositoryToApiModel(entity);
    }

    public Store updateStore(Long storeId, Store store) {
        StoreEntity entity = storeRepository.findOne(storeId);
        if (entity != null) {
            entity.setName(store.getName());
            entity = storeRepository.save(entity);
            return storeConverter.repositoryToApiModel(entity);
        } else {
            throw new NoSuchElementException();
        }
    }

    public void deleteStore(Long id) {
        storeRepository.delete(id);
    }

    public boolean exists(Long id) {
        return storeRepository.exists(id);
    }
}
