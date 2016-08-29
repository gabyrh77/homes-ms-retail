package com.tenx.ms.retail.repository;

import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Created by goropeza on 28/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@Transactional
public class TestProductRepository {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void insertAndGetProductOK() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("123bc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        product = productRepository.save(product);

        assertTrue("Product created", (long) product.getId() > 0);
        assertEquals("Product retrieved", productRepository.findByStore(store).size(), 1);
    }

    @Test
    public void saveProductFailStoreNull() {
        ProductEntity product = new ProductEntity("123bc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), null);
        try {
            product = productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            assertEquals("Data integrity violation thrown", ex.getClass(), DataIntegrityViolationException.class);
        }
        assertNull("Product not created", product.getId());
    }

    @Test
    public void saveProductFailSkuNull() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity(null, "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        try {
            product = productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            assertEquals("Data integrity violation thrown", ex.getClass(), DataIntegrityViolationException.class);
        }
        assertNull("Product not created", product.getId());
    }

    @Test
    public void saveProductFailSkuLong() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("12345678abc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        try {
            product = productRepository.save(product);
        } catch (ConstraintViolationException ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertNull("Product not created", product.getId());
    }

    @Test
    public void saveProductFailSkuSpecialCharacters() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("123*abc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        try {
            product = productRepository.save(product);
        } catch (ConstraintViolationException ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertNull("Product not created", product.getId());
    }

    @Test
    public void saveProductFailSkuShort() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("123", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        try {
            product = productRepository.save(product);
        } catch (ConstraintViolationException ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertNull("Product not created", product.getId());
    }

    @Test
    public void deleteProductOK() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        assertTrue("Store created", store.getId() > 0);
        ProductEntity product = new ProductEntity("123bc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        product = productRepository.save(product);
        assertTrue("Product created", product.getId() > 0);
        Long productId = product.getId();
        productRepository.delete(productId);
        assertNull("Product deleted", productRepository.findOne(productId));
    }
}
