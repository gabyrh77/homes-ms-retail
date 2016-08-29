package com.tenx.ms.retail.product.util;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.rest.dto.Product;
import com.tenx.ms.retail.store.domain.StoreEntity;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Created by goropeza on 26/08/16.
 */
@Service
public class ProductConverter {

    public Product repositoryToApiModel(ProductEntity productEntity) {
        if (productEntity != null) {
            return new Product(productEntity.getId(), productEntity.getStore().getId(), productEntity.getName(), productEntity.getDescription(),
                productEntity.getSku(), productEntity.getPrice());
        }
        return null;
    }

    public ProductEntity apiModelToRepository(StoreEntity store, Product product) {
        if (product != null) {
            return new ProductEntity(product.getSku(), product.getName(), product.getDescription(),
                product.getPrice(), store);
        }
        return null;
    }

    public Function<ProductEntity, Product> entityToProduct = x -> new
        Product(x.getId(), x.getStore().getId(), x.getName(), x.getDescription(),
        x.getSku(), x.getPrice());
}
