package com.tenx.ms.retail.product.util;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.rest.dto.Product;
import com.tenx.ms.retail.store.domain.StoreEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Created by goropeza on 26/08/16.
 */
@Component
public class ProductConverter {

    public Product repositoryToApiModel(ProductEntity productEntity) {
        return new Product(productEntity.getId(), productEntity.getStore().getId(), productEntity.getName(), productEntity.getDescription(),
                productEntity.getSku(), productEntity.getPrice());
    }

    public ProductEntity apiModelToRepository(StoreEntity store, Product product) {
        return new ProductEntity(product.getSku(), product.getName(), product.getDescription(),
            product.getPrice(), store);
    }

    public Function<ProductEntity, Product> entityToProduct = x -> new
        Product(x.getId(), x.getStore().getId(), x.getName(), x.getDescription(),
        x.getSku(), x.getPrice());
}
