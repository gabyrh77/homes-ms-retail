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
import java.util.Optional;
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

    public void deleteProduct(Long storeId, Long productId) {
        Optional<ProductEntity> result = productRepository.findById(productId);
        if (!result.isPresent() || !storeId.equals(result.get().getStore().getId())) {
            throw new NoSuchElementException("Product not found");
        }
        productRepository.delete(productId);
    }

    public Product createInStore(Product product, Long storeId) {
        Optional<StoreEntity> result = storeRepository.findById(storeId);
        if (!result.isPresent()) {
            throw new NoSuchElementException("Store not found");
        } else {
            ProductEntity newEntity = productConverter.apiModelToRepository(result.get(), product);
            newEntity = productRepository.save(newEntity);
            return productConverter.repositoryToApiModel(newEntity);
        }
    }

    public Product updateProductInStore(Long storeId, Long productId, Product product) {
        Optional<ProductEntity> result = productRepository.findById(productId);
        if (!result.isPresent() || !storeId.equals(result.get().getStore().getId())) {
            throw new NoSuchElementException("Product not found");
        } else {
            ProductEntity productEntity = result.get();
            productEntity.setSku(product.getSku());
            productEntity.setName(product.getName());
            productEntity.setDescription(product.getDescription());
            productEntity.setPrice(product.getPrice());
            productEntity = productRepository.save(productEntity);
            return productConverter.repositoryToApiModel(productEntity);
        }
    }

    public List<Product> findAllProductsByStore(Long storeId) {
        Optional<StoreEntity> result = storeRepository.findById(storeId);
        if (!result.isPresent()) {
            throw new NoSuchElementException("Store not found");
        }
        return productRepository.findByStore(result.get()).stream().map(productConverter.entityToProduct).collect(Collectors.toList());
    }

    public Product findProductByIdAndStore(Long productId, Long storeId) {
        Optional<StoreEntity> result = storeRepository.findById(storeId);
        if (!result.isPresent()) {
            throw new NoSuchElementException("Store not found");
        }

        Optional<ProductEntity> resultProduct = productRepository.findByIdAndStore(productId, result.get());
        if (!resultProduct.isPresent()) {
            throw new NoSuchElementException("Product not found");
        }
        return productConverter.repositoryToApiModel(resultProduct.get());
    }

    public Product findProductByNameAndStore(String name, Long storeId) {
        Optional<StoreEntity> result = storeRepository.findById(storeId);
        if (!result.isPresent()) {
            throw new NoSuchElementException("Store not found");
        }
        Optional<ProductEntity> resultProduct = productRepository.findByNameAndStore(name, result.get());
        if (!resultProduct.isPresent()) {
            throw new NoSuchElementException("Product not found");
        }
        return productConverter.repositoryToApiModel(resultProduct.get());
    }
}
