package com.tenx.ms.retail.stock.util;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Created by goropeza on 28/08/16.
 */
@Service
public class StockConverter {

    public Stock repositoryToApiModel(StockEntity stockEntity) {
        if (stockEntity != null) {
            return new Stock(stockEntity.getProduct().getId(), stockEntity.getProduct().getStore().getId(), stockEntity.getExistence());
        }
        return null;
    }

    public StockEntity apiModelToRepository(ProductEntity productEntity, Stock stock) {
        if (stock != null) {
            return new StockEntity(productEntity, stock.getCount());
        }
        return null;
    }

    public Function<StockEntity, Stock> entityToStock = x -> new
        Stock(x.getProduct().getId(), x.getProduct().getStore().getId(), x.getExistence());
}
