package com.tenx.ms.retail.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by goropeza on 28/08/16.
 */

@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@ActiveProfiles(Profiles.TEST_NOAUTH)
public class TestStockController extends AbstractIntegrationTest {
    @Autowired
    private ObjectMapper mapper;

    private static final String API_VERSION = RestConstants.VERSION_ONE;

    public static final String REQUEST_URI = "%s" + API_VERSION + "/stock/";

    private final RestTemplate template = new TestRestTemplate();

    @Value("classpath:rest/assets/new_stock.json")
    private File newStockRequest;

    @Value("classpath:rest/assets/update_stock.json")
    private File updateStockRequest;

    @Value("classpath:rest/assets/new_stock_count_null.json")
    private File newStockCountNullRequest;

    @Value("classpath:rest/assets/new_product.json")
    private File newProductRequest;

    @Value("classpath:rest/assets/new_store.json")
    private File newStoreRequest;

    private Integer storeId;
    private Integer productId;

    @Before
    public void setUp() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);
        storeId = (Integer)responseStoreId.getId();
        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);
        productId = (Integer)responseProductId.getId();
    }

    @Test
    @FlywayTest
    public void updateStockNotFound() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "99/99", FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Store or product not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createAndGetStockOk() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, response.getStatusCode());

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
        assertEquals("Stock get OK correct count", responseGetBody.getCount(), Long.valueOf(50));
        assertEquals("Stock get OK correct product id", responseGetBody.getProductId().intValue(), productId.intValue());
        assertEquals("Stock get OK correct store id", responseGetBody.getStoreId().intValue(), storeId.intValue());
    }

    @Test
    @FlywayTest
    public void getEmptyStockOk() throws IOException {
        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
        assertEquals("Stock get OK correct product id", responseGetBody.getProductId().intValue(), productId.intValue());
        assertEquals("Stock get OK correct store id", responseGetBody.getStoreId().intValue(), storeId.intValue());
        assertEquals("Stock get OK correct count", responseGetBody.getCount(), Long.valueOf(0));
    }

    @Test
    @FlywayTest
    public void getStockNotFound() throws IOException {
        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath())  + "9/9" ,
            null, HttpMethod.GET);
        assertEquals("Stock get not found", HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }

    @Test
    @FlywayTest
    public void getStockProductNotFound() throws IOException {
        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/9" ,
            null, HttpMethod.GET);
        assertEquals("Stock get product not found", HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }

    @Test
    @FlywayTest
    public void getStockProductWrongStoreNotFound() throws IOException {
        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9/" + productId,
            null, HttpMethod.GET);
        assertEquals("Stock get wrong store", HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createStockProductNotFound() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/5",
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock not created, product not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createStockCountNull() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            FileUtils.readFileToString(newStockCountNullRequest), HttpMethod.POST);
        assertEquals("Stock not created, stock count null", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createAndUpdateStockOk() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, response.getStatusCode());

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
        assertEquals("Stock get OK correct count", responseGetBody.getCount(), Long.valueOf(50));

        ResponseEntity<String> responseUpdate = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            FileUtils.readFileToString(updateStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, responseUpdate.getStatusCode());

        ResponseEntity<String> responseGetUpdate = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            null, HttpMethod.GET);
        assertEquals("Stock get after update OK", HttpStatus.OK, responseGetUpdate.getStatusCode());
        Stock responseGetUpdateBody = mapper.readValue(responseGetUpdate.getBody(), Stock.class);
        assertEquals("Stock get OK correct count", responseGetUpdateBody.getCount(), Long.valueOf(10));
    }

    @Test
    @FlywayTest
    public void createStockInvalidCount() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId + "/" + productId,
            "{\"count\": \"FIFTY\"}", HttpMethod.POST);
        assertEquals("Stock not created bad request", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
