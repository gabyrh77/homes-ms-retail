package com.tenx.ms.retail.store.service;

import com.tenx.ms.retail.store.util.StoreConverter;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import com.tenx.ms.retail.store.rest.dto.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
        Optional<StoreEntity> result = storeRepository.findById(id);
        if (result.isPresent()) {
            return storeConverter.repositoryToApiModel(result.get());
        } else {
            throw new NoSuchElementException();
        }
    }

    public Store findStoreByName(String name) {
        Optional<StoreEntity> result = storeRepository.findByName(name);
        if (result.isPresent()) {
            return storeConverter.repositoryToApiModel(result.get());
        } else {
            throw new NoSuchElementException();
        }
    }

    public List<Store> findAllStores() {
        return storeRepository.findAll().stream().map(storeConverter.entityToStore).
            collect(Collectors.toList());
    }

    public Store insertStore(Store store) {
        StoreEntity entity = storeConverter.apiModelToRepository(store);
        entity = storeRepository.save(entity);
        return storeConverter.repositoryToApiModel(entity);
    }

    public Store updateStore(Long storeId, Store store) {
        Optional<StoreEntity> result = storeRepository.findById(storeId);
        if (result.isPresent()) {
            StoreEntity entity = result.get();
            entity.setName(store.getName());
            entity = storeRepository.save(entity);
            return storeConverter.repositoryToApiModel(entity);
        } else {
            throw new NoSuchElementException();
        }
    }

    public void deleteStore(Long id) {
        if(!exists(id)) {
            throw new NoSuchElementException();
        } else {
            storeRepository.delete(id);
        }
    }

    public boolean exists(Long id) {
        return storeRepository.exists(id);
    }
}
