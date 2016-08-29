package com.tenx.ms.retail.stock.service;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.repository.StockRepository;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import com.tenx.ms.retail.stock.util.StockConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by goropeza on 26/08/16.
 */
@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockConverter stockConverter;

    public void upsertStock(Long productId, Stock stock) {
        ProductEntity product = productRepository.findOne(productId);
        StockEntity entity = stockConverter.apiModelToRepository(product, stock);
        stockRepository.save(entity);
    }

    public Stock findStockByProduct(Long productId) {
        ProductEntity product = productRepository.findOne(productId);
        StockEntity stockEntity = stockRepository.findByProduct(product);
        if (stockEntity != null) {
            return stockConverter.repositoryToApiModel(stockEntity);
        } else {
            return new Stock(productId, product.getStore().getId(), 0L);
        }
    }
}
