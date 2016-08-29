package com.tenx.ms.retail.repository;

import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.client.repository.ClientRepository;
import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.repository.OrderRepository;
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
public class TestOrderRepository {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void createOrderOK() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);

        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robbin", "5554321110");
        client = clientRepository.save(client);

        OrderEntity order = new OrderEntity(store, client);
        order = orderRepository.save(order);
        assertTrue("Order created", (long) order.getId() > 0);
    }

    @Test
    public void saveOrderFailStoreNull() {
        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robbin", "5554321110");
        client = clientRepository.save(client);

        OrderEntity order = new OrderEntity(null, client);
        try {
            order = orderRepository.save(order);
        } catch (DataIntegrityViolationException ex) {
            assertEquals("Data integrity violation thrown", ex.getClass(), DataIntegrityViolationException.class);
        }
        assertNull("Order not created", order.getId());
    }

    @Test
    public void saveOrderFailClientNull() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);

        OrderEntity order = new OrderEntity(store, null);
        try {
            order = orderRepository.save(order);
        } catch (DataIntegrityViolationException ex) {
            assertEquals("Data integrity violation thrown", ex.getClass(), DataIntegrityViolationException.class);
        }
        assertNull("Order not created", order.getId());
    }
}
