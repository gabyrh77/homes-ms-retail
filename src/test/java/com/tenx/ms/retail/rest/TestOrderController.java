package com.tenx.ms.retail.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.order.rest.dto.Order;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by goropeza on 28/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@ActiveProfiles(Profiles.TEST_NOAUTH)
public class TestOrderController extends AbstractIntegrationTest {
    @Autowired
    private ObjectMapper mapper;

    private static final String API_VERSION = RestConstants.VERSION_ONE;

    public static final String REQUEST_URI = "%s" + API_VERSION + "/orders/";

    private final RestTemplate template = new TestRestTemplate();

    @Value("classpath:assets/new_order.json")
    private File newOrderRequest;

    @Value("classpath:assets/new_order_detail_null.json")
    private File newOrderDetailNullRequest;

    @Value("classpath:assets/new_order_product_id_null.json")
    private File newOrderProductIdNullRequest;

    @Value("classpath:assets/new_order_backorder.json")
    private File newOrderBackorderRequest;

    @Value("classpath:assets/new_stock.json")
    private File newStockRequest;

    @Value("classpath:assets/new_product.json")
    private File newProductRequest;

    @Value("classpath:assets/new_store.json")
    private File newStoreRequest;

    @Value("classpath:assets/new_client.json")
    private File newClientRequest;

    @Value("classpath:assets/new_order_errors.json")
    private File newOrderErrorsRequest;

    @Value("classpath:assets/new_order_client_invalid.json")
    private File newOrderClientInvalidRequest;

    @Test
    public void createOrderNotFound() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "99", FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Store not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createOrderClientInvalid() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseStock = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, responseStock.getStatusCode());

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newOrderClientInvalidRequest), HttpMethod.POST);
        assertEquals("Order not created, client not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createOrderAndClientDataOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseStock = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, responseStock.getStatusCode());

        ResponseEntity<String> responseClient = getJSONResponse(template, String.format(TestClientController.REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created", HttpStatus.CREATED, responseClient.getStatusCode());
        ResourceCreated responseClientId = mapper.readValue(responseClient.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newOrderRequest), HttpMethod.POST);
        assertEquals("Order created", HttpStatus.CREATED, response.getStatusCode());
        Order orderResponse = mapper.readValue(response.getBody(), Order.class);
        assertEquals("Order created correct order id", orderResponse.getOrderId(), Long.valueOf(1));
        assertEquals("Order created correct store id", orderResponse.getStoreId().intValue(), responseStoreId.getId());
        assertEquals("Order created correct client id", orderResponse.getClientId().intValue(), responseClientId.getId());

        assertEquals("Order created correct client id", orderResponse.getClient().getClientId(), orderResponse.getClientId());
        assertEquals("Order created correct client first name", orderResponse.getClient().getFirstName(), "Guy");
        assertEquals("Order created correct client last name", orderResponse.getClient().getLastName(), "Roger");
        assertEquals("Order created correct client email", orderResponse.getClient().getEmail(), "client@domian.co");
        assertEquals("Order created correct client phone", orderResponse.getClient().getPhone(), "5512345678");
    }

    @Test
    @FlywayTest
    public void createOrderAndUpdateStockOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseStock = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, responseStock.getStatusCode());

        ResponseEntity<String> responseClient = getJSONResponse(template, String.format(TestClientController.REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created", HttpStatus.CREATED, responseClient.getStatusCode());
        ResourceCreated responseClientId = mapper.readValue(responseClient.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newOrderRequest), HttpMethod.POST);
        assertEquals("Order created", HttpStatus.CREATED, response.getStatusCode());
        Order orderResponse = mapper.readValue(response.getBody(), Order.class);
        assertEquals("Order created correct order id", orderResponse.getOrderId(), Long.valueOf(1));
        assertEquals("Order created correct store id", orderResponse.getStoreId().intValue(), responseStoreId.getId());
        assertEquals("Order created correct client id", orderResponse.getClientId().intValue(), responseClientId.getId());

        assertEquals("Order created correct detail", orderResponse.getProducts().size(), 1);
        assertEquals("Order created correct product id", orderResponse.getProducts().get(0).getProductId(), Long.valueOf(1));
        assertEquals("Order created correct product count", orderResponse.getProducts().get(0).getCount(), Long.valueOf(10));

        assertEquals("Order created correct detail backorder", orderResponse.getBackorder().size(), 0);
        assertEquals("Order created correct detail errors", orderResponse.getErrors().size(), 0);

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
        assertEquals("Stock get OK correct count", responseGetBody.getCount(), Long.valueOf(40));
    }

    @Test
    @FlywayTest
    public void createOrderBackorderAndUpdateStockOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseStock = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, responseStock.getStatusCode());

        ResponseEntity<String> responseClient = getJSONResponse(template, String.format(TestClientController.REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created", HttpStatus.CREATED, responseClient.getStatusCode());
        ResourceCreated responseClientId = mapper.readValue(responseClient.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newOrderBackorderRequest), HttpMethod.POST);
        assertEquals("Order created", HttpStatus.CREATED, response.getStatusCode());
        Order orderResponse = mapper.readValue(response.getBody(), Order.class);
        assertEquals("Order created correct order id", orderResponse.getOrderId(), Long.valueOf(1));
        assertEquals("Order created correct store id", orderResponse.getStoreId().intValue(), responseStoreId.getId());
        assertEquals("Order created correct client id", orderResponse.getClientId().intValue(), responseClientId.getId());
        assertEquals("Order created correct detail ordered", orderResponse.getProducts().size(), 1);
        assertEquals("Order created correct ordered product id", orderResponse.getProducts().get(0).getProductId(), Long.valueOf(1));
        assertEquals("Order created correct ordered product count", orderResponse.getProducts().get(0).getCount(), Long.valueOf(50));

        assertEquals("Order created correct detail backordered", orderResponse.getBackorder().size(), 1);
        assertEquals("Order created correct backordered product id", orderResponse.getBackorder().get(0).getProductId(), Long.valueOf(1));
        assertEquals("Order created correct backordered product count", orderResponse.getBackorder().get(0).getCount(), Long.valueOf(10));

        assertEquals("Order created correct detail errors", orderResponse.getErrors().size(), 0);

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
        assertEquals("Stock get OK correct count", responseGetBody.getCount(), Long.valueOf(0));
    }

    @Test
    @FlywayTest
    public void createOrderWithErrorsOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseStock = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, responseStock.getStatusCode());

        ResponseEntity<String> responseClient = getJSONResponse(template, String.format(TestClientController.REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created", HttpStatus.CREATED, responseClient.getStatusCode());
        ResourceCreated responseClientId = mapper.readValue(responseClient.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newOrderErrorsRequest), HttpMethod.POST);
        assertEquals("Order created", HttpStatus.CREATED, response.getStatusCode());
        Order orderResponse = mapper.readValue(response.getBody(), Order.class);
        assertEquals("Order created correct order id", orderResponse.getOrderId(), Long.valueOf(1));
        assertEquals("Order created correct store id", orderResponse.getStoreId().intValue(), responseStoreId.getId());
        assertEquals("Order created correct client id", orderResponse.getClientId().intValue(), responseClientId.getId());
        assertEquals("Order created correct detail ordered", orderResponse.getProducts().size(), 1);
        assertEquals("Order created correct ordered product id", orderResponse.getProducts().get(0).getProductId(), Long.valueOf(1));
        assertEquals("Order created correct ordered product count", orderResponse.getProducts().get(0).getCount(), Long.valueOf(25));
        assertEquals("Order created correct detail backorder", orderResponse.getBackorder().size(), 0);

        assertEquals("Order created correct detail errors", orderResponse.getErrors().size(), 1);
        assertEquals("Order created correct error product id", orderResponse.getErrors().get(0).getProductId(), Long.valueOf(10));
        assertEquals("Order created correct error product count", orderResponse.getErrors().get(0).getCount(), Long.valueOf(25));

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
        assertEquals("Stock get OK correct count", responseGetBody.getCount(), Long.valueOf(25));
    }

    @Test
    @FlywayTest
    public void createOrderDetailNull() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseStock = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, responseStock.getStatusCode());

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newOrderDetailNullRequest), HttpMethod.POST);
        assertEquals("Order not created, details null", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createOrderDetailProductIdNull() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseStock = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, responseStock.getStatusCode());

        ResponseEntity<String> responseClient = getJSONResponse(template, String.format(TestClientController.REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created", HttpStatus.CREATED, responseClient.getStatusCode());
        ResourceCreated responseClientId = mapper.readValue(responseClient.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newOrderProductIdNullRequest), HttpMethod.POST);
        assertEquals("Order created", HttpStatus.CREATED, response.getStatusCode());

        Order orderResponse = mapper.readValue(response.getBody(), Order.class);
        assertEquals("Order created correct order id", orderResponse.getOrderId(), Long.valueOf(1));
        assertEquals("Order created correct store id", orderResponse.getStoreId().intValue(), responseStoreId.getId());
        assertEquals("Order created correct client id", orderResponse.getClientId().intValue(), responseClientId.getId());
        assertEquals("Order created correct detail ordered", orderResponse.getProducts().size(), 1);
        assertEquals("Order created correct ordered product id", orderResponse.getProducts().get(0).getProductId(), Long.valueOf(1));
        assertEquals("Order created correct ordered product count", orderResponse.getProducts().get(0).getCount(), Long.valueOf(10));
        assertEquals("Order created correct detail backorder", orderResponse.getBackorder().size(), 0);

        assertEquals("Order created correct detail errors", orderResponse.getErrors().size(), 1);
        assertNull("Order created correct error product id", orderResponse.getErrors().get(0).getProductId());
        assertEquals("Order created correct error product count", orderResponse.getErrors().get(0).getCount(), Long.valueOf(10));

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(TestStockController.REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
        assertEquals("Stock get OK correct count", responseGetBody.getCount(), Long.valueOf(40));
    }
}
