package com.tenx.ms.retail.stock.util;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Created by goropeza on 28/08/16.
 */
@Component
public class StockConverter {

    public Stock repositoryToApiModel(StockEntity stockEntity) {
        return new Stock(stockEntity.getProduct().getId(), stockEntity.getProduct().getStore().getId(), stockEntity.getExistence());
    }

    public StockEntity apiModelToRepository(ProductEntity productEntity, Stock stock) {
        return new StockEntity(productEntity, stock.getCount());
    }

    public Function<StockEntity, Stock> entityToStock = x -> new
        Stock(x.getProduct().getId(), x.getProduct().getStore().getId(), x.getExistence());
}
