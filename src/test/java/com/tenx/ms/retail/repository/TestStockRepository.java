package com.tenx.ms.retail.repository;

import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.repository.StockRepository;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by goropeza on 28/08/16.
 */

@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@Transactional
public class TestStockRepository extends AbstractIntegrationTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @FlywayTest
    public void upsertStockOK() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("123bc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        product = productRepository.save(product);

        StockEntity stock = new StockEntity(product, 100L);
        stock = stockRepository.save(stock);
        assertTrue("Stock created", (long) stock.getId() > 0);
        Long stockId = stock.getId();

        stock.setExistence(50L);
        stock = stockRepository.save(stock);
        assertEquals("Stock updated", stock.getId(), stockId);
        assertEquals("Stock existence correct", stock.getExistence(), Long.valueOf(50));
    }

    @Test
    @FlywayTest
    public void saveStockFail() {
        StockEntity stock = new StockEntity(null, 100L);
        try {
            stock = stockRepository.save(stock);
        } catch (DataIntegrityViolationException ex) {
            assertEquals("Data integrity violation thrown", ex.getClass(), DataIntegrityViolationException.class);
        }
        assertEquals("Stock not created", null, stock.getId());
    }

    @Test
    @FlywayTest
    public void getStockByProductOK() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("123bc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        product = productRepository.save(product);
        Long productId = product.getId();

        StockEntity stock = new StockEntity(product, 100L);
        stock = stockRepository.save(stock);
        assertTrue("Stock created", stock.getId() > 0);

        stock = stockRepository.findByProduct(product).get();
        assertEquals("Stock updated", stock.getProduct().getId(), productId);
        assertEquals("Stock existence correct", stock.getExistence(), Long.valueOf(100));
    }
}
