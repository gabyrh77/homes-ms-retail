package com.tenx.ms.retail.order.service;

import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.client.repository.ClientRepository;
import com.tenx.ms.retail.order.domain.OrderDetailEntity;
import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.repository.OrderDetailRepository;
import com.tenx.ms.retail.order.repository.OrderRepository;
import com.tenx.ms.retail.order.rest.dto.Order;
import com.tenx.ms.retail.order.rest.dto.OrderDetail;
import com.tenx.ms.retail.order.util.OrderConverter;
import com.tenx.ms.retail.order.util.OrderDetailStatusEnum;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.repository.StockRepository;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by goropeza on 28/08/16.
 */
@Service
public class OrderService {
    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProductRepository productRepository;

    @SuppressWarnings("PMD")
    public Order createOrder(Long storeId, Order order) {
        StoreEntity store = storeRepository.findOne(storeId);
        if (store == null) {
            throw new NoSuchElementException();
        }

        List<OrderDetail> details = order.getProducts();
        if (details == null || details.isEmpty()) {
            throw new ConstraintViolationException("Empty order details", null);
        }

        ClientEntity client = clientRepository.findByEmail(order.getEmail());
        if (client != null) {
            client.setPhone(order.getPhone());
            client.setFirstName(order.getFirstName());
            client.setLastName(order.getLastName());
        } else {
            client = new ClientEntity(order.getEmail(), order.getFirstName(), order.getLastName(), order.getPhone());
        }
        client = clientRepository.save(client);

        OrderEntity newOrder = new OrderEntity(store, client);
        newOrder = orderRepository.save(newOrder);
        Order result = orderConverter.repositoryToApiModel(newOrder);
        ProductEntity product;
        for(OrderDetail detail: details) {
            product = null;
            if (detail != null && detail.getProductId() != null && detail.getCount() != null) {
                product = productRepository.findOne(detail.getProductId());
            }
            if (product == null || !product.getStore().getId().equals(storeId)) {
                result.getErrors().add(detail);
            } else {
                StockEntity stockProduct = stockRepository.findByProduct(product);
                if (stockProduct != null && stockProduct.getExistence() >= detail.getCount()) {
                    createOrderDetail(result, detail, newOrder, product, OrderDetailStatusEnum.ORDERED);
                    decreaseStock(stockProduct, detail.getCount());
                } else {
                    long existence = stockProduct == null ? 0: stockProduct.getExistence();
                    if (existence > 0) {
                        long backordered = detail.getCount() - existence;
                        OrderDetail detailOrdered = new OrderDetail(product.getId(), existence);
                        createOrderDetail(result, detailOrdered, newOrder, product, OrderDetailStatusEnum.ORDERED);
                        decreaseStock(stockProduct, detailOrdered.getCount());
                        OrderDetail detailBackordered = new OrderDetail(product.getId(), backordered);
                        createOrderDetail(result, detailBackordered, newOrder, product, OrderDetailStatusEnum.BACKORDERED);
                    } else {
                        createOrderDetail(result, detail, newOrder, product, OrderDetailStatusEnum.BACKORDERED);
                    }
                }
            }
        }
        return result;
    }

    private void createOrderDetail(Order result, OrderDetail detail, OrderEntity orderEntity, ProductEntity productEntity, OrderDetailStatusEnum status) {
        OrderDetailEntity detailEntity = new OrderDetailEntity(orderEntity, productEntity, status, detail.getCount());
        try {
            orderDetailRepository.save(detailEntity);
            if (status.equals(OrderDetailStatusEnum.ORDERED)) {
                result.getProducts().add(detail);
            } else {
                result.getBackorder().add(detail);
            }
        } catch (DataIntegrityViolationException ex) {
            detailEntity = orderDetailRepository.findByOrderAndProductAndStatus(orderEntity, productEntity, status);
            detailEntity.setCount(detailEntity.getCount() + detail.getCount());
            orderDetailRepository.save(detailEntity);
            result.getProducts().stream().filter(x -> x.getProductId().equals(productEntity.getId())).forEach(x -> x.setCount(x.getCount() + detail.getCount()));
        }
    }

    private void decreaseStock(StockEntity stock, long count) {
        stock.setExistence(stock.getExistence() - count);
        stockRepository.save(stock);
    }
}
