package com.tenx.ms.retail.repository;

import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.retail.RetailServiceApp;
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by goropeza on 28/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@Transactional
public class TestStoreRepository {

    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void saveStoreOK() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        assertTrue("Store created", (long) store.getId() > 0);
    }

    @Test
    public void saveStoreFail() {
        StoreEntity store = new StoreEntity(null);
        try {
            store = storeRepository.save(store);
        } catch (DataIntegrityViolationException ex) {
            assertEquals("Data integrity violation thrown", ex.getClass(), DataIntegrityViolationException.class);
        }
        assertEquals("Store not created", null, store.getId());
    }

    @Test
    public void deleteStoreOK() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        assertTrue("Store created", (long) store.getId() > 0);
        Long storeId = store.getId();
        storeRepository.delete(storeId);
        assertNull("Store deleted", storeRepository.findOne(storeId));
    }
}
