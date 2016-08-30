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
import java.util.Optional;

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
        ClientEntity client;
        StoreEntity store;

        Optional<StoreEntity> resultStore = storeRepository.findById(storeId);
        if (!resultStore.isPresent()) {
            throw new NoSuchElementException("Store not found");
        } else {
            store = resultStore.get();
        }

        List<OrderDetail> details = order.getProducts();
        if (details == null || details.isEmpty()) {
            throw new ConstraintViolationException("Empty order details", null);
        }

        if (order.getClientId() == null) {
            throw new ConstraintViolationException("Order must have a clientId", null);
        } else {
            Optional<ClientEntity> resultClient = clientRepository.findById(order.getClientId());
            if (!resultClient.isPresent()) {
                throw new NoSuchElementException("Client not found");
            } else {
                client = resultClient.get();
            }
        }

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
                Optional<StockEntity> resultStock = stockRepository.findByProduct(product);
                long existence = resultStock.isPresent() ? 0: resultStock.get().getExistence();
                if (existence >= detail.getCount()) {
                    createOrderDetail(result, detail, newOrder, product, OrderDetailStatusEnum.ORDERED);
                    decreaseStock(resultStock.get(), detail.getCount());
                } else {
                    if (existence > 0) {
                        long backordered = detail.getCount() - existence;
                        OrderDetail detailOrdered = new OrderDetail(product.getId(), existence);
                        createOrderDetail(result, detailOrdered, newOrder, product, OrderDetailStatusEnum.ORDERED);
                        decreaseStock(resultStock.get(), detailOrdered.getCount());
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
            Optional<OrderDetailEntity> resultDetail = orderDetailRepository.findByOrderAndProductAndStatus(orderEntity, productEntity, status);
            if (resultDetail.isPresent()) {
                detailEntity = resultDetail.get();
                detailEntity.setCount(detailEntity.getCount() + detail.getCount());
                orderDetailRepository.save(detailEntity);
                result.getProducts().stream().filter(x -> x.getProductId().equals(productEntity.getId())).forEach(x -> x.setCount(x.getCount() + detail.getCount()));
            } else {
                throw ex;
            }
        }
    }

    private void decreaseStock(StockEntity stock, long count) {
        stock.setExistence(stock.getExistence() - count);
        stockRepository.save(stock);
    }
}
