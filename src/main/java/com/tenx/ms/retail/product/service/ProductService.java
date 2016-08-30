package com.tenx.ms.retail.product.service;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.product.rest.dto.Product;
import com.tenx.ms.retail.product.util.ProductConverter;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by goropeza on 26/08/16.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductConverter productConverter;

    public boolean existsInStore(Long productId, Long storeId) {
        StoreEntity store = storeRepository.findOne(storeId);
        if (store != null) {
            ProductEntity productExists = productRepository.findByIdAndStore(productId, store);
            return productExists != null;
        }
        return false;
    }

    public void deleteProduct(Long productId) {
        productRepository.delete(productId);
    }

    public Product createInStore(Product product, Long storeId) {
        StoreEntity store = storeRepository.findOne(storeId);
        if (store == null) {
            throw new NoSuchElementException();
        }

        ProductEntity newEntity = productConverter.apiModelToRepository(store, product);
        if (newEntity != null) {
            newEntity = productRepository.save(newEntity);
        }
        return productConverter.repositoryToApiModel(newEntity);
    }

    public Product updateProductInStore(Long storeId, Long productId, Product product) {
        ProductEntity productEntity = productRepository.findOne(productId);
        if (productEntity == null || !storeId.equals(productEntity.getStore().getId())) {
            throw new NoSuchElementException();
        }

        productEntity.setSku(product.getSku());
        productEntity.setName(product.getName());
        productEntity.setDescription(product.getDescription());
        productEntity.setPrice(product.getPrice());
        productEntity = productRepository.save(productEntity);
        return productConverter.repositoryToApiModel(productEntity);
    }

    public List<Product> findAllProductsByStore(Long storeId) {
        StoreEntity store = storeRepository.findOne(storeId);
        if (store == null) {
            throw new NoSuchElementException();
        }

        return productRepository.findByStore(store).stream().map(productConverter.entityToProduct).collect(Collectors.toList());
    }

    public Product findProductByIdAndStore(Long productId, Long storeId) {
        StoreEntity store = storeRepository.findOne(storeId);
        if (store == null) {
            throw new NoSuchElementException();
        }

        ProductEntity productEntity = productRepository.findByIdAndStore(productId, store);
        if (productEntity == null) {
            throw new NoSuchElementException();
        }
        return productConverter.repositoryToApiModel(productEntity);
    }

    public Product findProductByNameAndStore(String name, Long storeId) {
        StoreEntity store = storeRepository.findOne(storeId);
        if (store == null) {
            throw new NoSuchElementException();
        }
        ProductEntity productEntity = productRepository.findByNameAndStore(name, store);
        if (productEntity == null) {
            throw new NoSuchElementException();
        }
        return productConverter.repositoryToApiModel(productEntity);
    }
}
