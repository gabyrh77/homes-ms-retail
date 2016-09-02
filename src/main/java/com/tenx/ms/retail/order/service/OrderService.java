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
import com.tenx.ms.retail.order.domain.enums.OrderDetailStatusEnum;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.repository.StockRepository;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Order createOrder(Long storeId, Order order) {
        ClientEntity client;
        StoreEntity store;
        ProductEntity product;

        Optional<StoreEntity> resultStore = storeRepository.findById(storeId);
        if (!resultStore.isPresent()) {
            throw new NoSuchElementException("Store not found");
        } else {
            store = resultStore.get();
        }

        List<OrderDetail> details = order.getProducts();
        if (details == null || details.isEmpty()) {
            throw new NullArgumentException("Order products");
        }

        if (order.getClientId() == null) {
            throw new NullArgumentException("clientId");
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
        Order resultOrder = orderConverter.repositoryToApiModel(newOrder);

        for(OrderDetail detail: details) {
            long productId = detail != null && detail.getProductId() != null ? detail.getProductId() : 0;
            Optional<ProductEntity> resultProduct = productId > 0 ? productRepository.findById(productId) : Optional.<ProductEntity>empty();
            if (!resultProduct.isPresent() || !resultProduct.get().getStore().getId().equals(storeId) || detail == null ||
                detail.getCount() == null  || detail.getCount() <= 0) {
                resultOrder.getErrors().add(detail);
            } else {
                product = resultProduct.get();
                Optional<StockEntity> resultStock = stockRepository.findByProduct(product);
                long existence = resultStock.isPresent() ? resultStock.get().getExistence() : 0;
                if (existence >= detail.getCount()) {
                    createOrderDetail(resultOrder, detail, newOrder, product, OrderDetailStatusEnum.ORDERED);
                    decreaseStock(resultStock.get(), detail.getCount());
                } else {
                    if (existence > 0) {
                        long backordered = detail.getCount() - existence;
                        OrderDetail detailOrdered = new OrderDetail(product.getId(), existence);
                        createOrderDetail(resultOrder, detailOrdered, newOrder, product, OrderDetailStatusEnum.ORDERED);
                        decreaseStock(resultStock.get(), detailOrdered.getCount());
                        OrderDetail detailBackordered = new OrderDetail(product.getId(), backordered);
                        createOrderDetail(resultOrder, detailBackordered, newOrder, product, OrderDetailStatusEnum.BACKORDERED);
                    } else {
                        createOrderDetail(resultOrder, detail, newOrder, product, OrderDetailStatusEnum.BACKORDERED);
                    }
                }
            }
        }
        return resultOrder;
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
