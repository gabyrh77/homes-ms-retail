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

/**
 * Created by goropeza on 28/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@ActiveProfiles(Profiles.TEST_NOAUTH)
public class TestStockController extends AbstractIntegrationTest {
    @Autowired
    private ObjectMapper mapper;

    private static final String API_VERSION = RestConstants.VERSION_ONE;

    public static final String REQUEST_URI = "%s" + API_VERSION + "/stock/";

    private final RestTemplate template = new TestRestTemplate();

    @Value("classpath:assets/new_stock.json")
    private File newStockRequest;

    @Value("classpath:assets/new_stock_count_null.json")
    private File newStockCountNullRequest;

    @Value("classpath:assets/new_product.json")
    private File newProductRequest;

    @Value("classpath:assets/new_store.json")
    private File newStoreRequest;

    @Test
    public void updateStockNotFound() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "99/99", FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Store or product not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createAndGetStockOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock created", HttpStatus.OK, response.getStatusCode());

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
        assertEquals("Stock get OK correct count", responseGetBody.getCount(), Long.valueOf(50));
    }

    @Test
    @FlywayTest
    public void getEmptyStockOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseGet.getStatusCode());
        Stock responseGetBody = mapper.readValue(responseGet.getBody(), Stock.class);
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
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "/9" ,
            null, HttpMethod.GET);
        assertEquals("Stock get product not found", HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }

    @Test
    @FlywayTest
    public void getStockProductWrongStoreNotFound() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9/" + responseProductId.getId(),
            null, HttpMethod.GET);
        assertEquals("Stock get wrong store", HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createStockProductNotFound() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "/5",
            FileUtils.readFileToString(newStockRequest), HttpMethod.POST);
        assertEquals("Stock not created, product not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createStockCountNull() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseProduct = getJSONResponse(template, String.format(TestProductController.REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, responseProduct.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(responseProduct.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(),
            FileUtils.readFileToString(newStockCountNullRequest), HttpMethod.POST);
        assertEquals("Stock not created, stock count null", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }
}
