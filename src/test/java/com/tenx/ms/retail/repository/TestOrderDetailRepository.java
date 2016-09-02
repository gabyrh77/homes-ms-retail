package com.tenx.ms.retail.repository;

import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.client.repository.ClientRepository;
import com.tenx.ms.retail.order.domain.OrderDetailEntity;
import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.repository.OrderDetailRepository;
import com.tenx.ms.retail.order.repository.OrderRepository;
import com.tenx.ms.retail.order.domain.enums.OrderDetailStatusEnum;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by goropeza on 28/08/16.
 */
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@Transactional
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class TestOrderDetailRepository extends AbstractIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    @FlywayTest
    public void createOrderDetailOK() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("123bc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        product = productRepository.save(product);
        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robbin", "5554321110");
        client = clientRepository.save(client);

        OrderEntity order = new OrderEntity(store, client);
        order = orderRepository.save(order);

        OrderDetailEntity orderDetail = new OrderDetailEntity(order, product, OrderDetailStatusEnum.ORDERED, 5L);
        orderDetail = orderDetailRepository.save(orderDetail);
        assertTrue("Order detail created", (long) orderDetail.getId() > 0);

        OrderDetailEntity orderDetailBackorder = new OrderDetailEntity(order, product, OrderDetailStatusEnum.BACKORDERED, 5L);
        orderDetailBackorder = orderDetailRepository.save(orderDetailBackorder);
        assertTrue("Order detail created backorder", (long) orderDetailBackorder.getId() > 0);
    }

    @Test
    @FlywayTest
    public void createOrderDetailDuplicatedFail() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("123bc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        product = productRepository.save(product);
        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robbin", "5554321110");
        client = clientRepository.save(client);

        OrderEntity order = new OrderEntity(store, client);
        order = orderRepository.save(order);

        OrderDetailEntity orderDetail = new OrderDetailEntity(order, product, OrderDetailStatusEnum.ORDERED, 5L);
        orderDetail = orderDetailRepository.save(orderDetail);
        assertTrue("Order detail created backorder", (long) orderDetail.getId() > 0);

        OrderDetailEntity orderDetailDuplicated = new OrderDetailEntity(order, product, OrderDetailStatusEnum.ORDERED, 10L);
        try {
            orderDetailDuplicated = orderDetailRepository.save(orderDetailDuplicated);
        } catch (Exception ex) {
            assertEquals("Data integrity violation thrown", ex.getClass(), DataIntegrityViolationException.class);
        }
        assertEquals("Order detail not created", null, orderDetailDuplicated.getId());
    }

    @Test
    @FlywayTest
    public void createOrderDetailFailOrderNull() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ProductEntity product = new ProductEntity("123bc", "Shoes", "Beautiful shoes", BigDecimal.valueOf(50.00), store);
        product = productRepository.save(product);

        OrderDetailEntity orderDetail = new OrderDetailEntity(null, product, OrderDetailStatusEnum.ORDERED, 5L);
        try {
            orderDetail = orderDetailRepository.save(orderDetail);
        } catch (Exception ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertEquals("Order detail not created",null, orderDetail.getId());
    }

    @Test
    @FlywayTest
    public void createOrderDetailFailProductNull() {
        StoreEntity store = new StoreEntity("Adidas");
        store = storeRepository.save(store);
        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robbin", "5554321110");
        client = clientRepository.save(client);

        OrderEntity order = new OrderEntity(store, client);
        order = orderRepository.save(order);

        OrderDetailEntity orderDetail = new OrderDetailEntity(order, null, OrderDetailStatusEnum.ORDERED, 5L);
        try {
            orderDetail = orderDetailRepository.save(orderDetail);
        } catch (Exception ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertEquals("Order detail not created", null, orderDetail.getId());
    }
}
