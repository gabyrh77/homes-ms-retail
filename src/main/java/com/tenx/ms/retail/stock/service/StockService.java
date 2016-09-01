package com.tenx.ms.retail.stock.service;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.repository.StockRepository;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import com.tenx.ms.retail.stock.util.StockConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

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

    public Stock upsertStock(Long storeId, Long productId, Stock stock) {
        StockEntity stockEntity;
        Optional<ProductEntity> result = productRepository.findById(productId);
        if (result.isPresent() && result.get().getStore().getId().equals(storeId)) {
            Optional<StockEntity> resultStock = stockRepository.findByProduct(result.get());
            if (resultStock.isPresent()) {
                stockEntity = resultStock.get();
                stockEntity.setExistence(stock.getCount());
            } else {
                stockEntity = stockConverter.apiModelToRepository(result.get(), stock);
            }
            stockEntity = stockRepository.save(stockEntity);
            return stockConverter.repositoryToApiModel(stockEntity);
        } else {
            throw new NoSuchElementException("Product not found");
        }
    }

    public Stock findStockByProduct(Long storeId, Long productId) {
        Optional<ProductEntity> result = productRepository.findById(productId);
        if (result.isPresent() && result.get().getStore().getId().equals(storeId)) {
            Optional<StockEntity> resultStock = stockRepository.findByProduct(result.get());
            if (resultStock.isPresent()) {
                return stockConverter.repositoryToApiModel(resultStock.get());
            } else {
                return new Stock(productId, storeId, 0L);
            }
        } else {
            throw new NoSuchElementException("Product not found");
        }
    }
}
