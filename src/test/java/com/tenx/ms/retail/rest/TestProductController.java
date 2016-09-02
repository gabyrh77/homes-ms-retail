package com.tenx.ms.retail.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.product.rest.dto.Product;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by goropeza on 26/08/16.
 */

@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@ActiveProfiles(Profiles.TEST_NOAUTH)
public class TestProductController extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    private static final String API_VERSION = RestConstants.VERSION_ONE;

    public static final String REQUEST_URI = "%s" + API_VERSION + "/products/";

    private final RestTemplate template = new TestRestTemplate();

    @Value("classpath:rest/assets/new_product.json")
    private File newProductRequest;

    @Value("classpath:rest/assets/update_product.json")
    private File updateProductRequest;

    @Value("classpath:rest/assets/new_store.json")
    private File newStoreRequest;

    @Test
    public void getProductsByStoreIdNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", null, HttpMethod.GET);
        assertEquals("Product not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getProductInStoreByIdNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "999?id=9", null, HttpMethod.GET);
        assertEquals("Product by id not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getProductInStoreByNameNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "999?name=Adidas", null, HttpMethod.GET);
        assertEquals("Product by name not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteProductInStoreNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "999/99", null, HttpMethod.DELETE);
        assertEquals("Delete product not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateProductInStoreNotFound() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "999/99", FileUtils.readFileToString(updateProductRequest), HttpMethod.PUT);
        assertEquals("Update product not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createProductInStoreNull() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "99999", null, HttpMethod.POST);
        assertEquals("Create product bad request", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createAndGetProductInStoreOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(response.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "?productId=" + responseProductId.getId(), null, HttpMethod.GET);
        assertEquals("Product by id OK", HttpStatus.OK, responseGet.getStatusCode());
        Product responseBody = mapper.readValue(responseGet.getBody(), Product.class);
        assertEquals("Product by id Product id", responseBody.getProductId(), Long.valueOf(1));
        assertEquals("Get Product name", responseBody.getName(), "Shoes");
    }

    @Test
    @FlywayTest
    public void createProductInStoreDuplicated() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertEquals("Product created Product id", responseProductId.getId(), 1);

        ResponseEntity<String> responseDuplicated = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product duplicated not created", HttpStatus.PRECONDITION_FAILED, responseDuplicated.getStatusCode());

        Map responseBody = mapper.readValue(responseDuplicated.getBody(), new TypeReference<Map>() {});
        assertNotNull("Error body exists", responseBody);
        assertTrue("Exists errors", responseBody.containsKey("errors"));
        assertNotNull("Error list exists", responseBody.get("errors"));
        List<Map> errors = (List<Map>) responseBody.get("errors");
        assertEquals("Error list correct size", errors.size(), 1);

        assertEquals("Error list correct field", errors.get(0).get("object_name"), "sku");
        assertEquals("Error list correct message", errors.get(0).get("default_message"), "is unique and already exists");
    }

    @Test
    @FlywayTest
    public void updateProductInStoreOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(response.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseUpdate = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(), FileUtils.readFileToString(updateProductRequest), HttpMethod.PUT);
        assertEquals("Product update OK", HttpStatus.OK, responseUpdate.getStatusCode());
        Product responseBody = mapper.readValue(responseUpdate.getBody(), Product.class);

        assertEquals("Product updated correct Product id", responseBody.getProductId(), Long.valueOf(1));
        assertEquals("Product updated correct name", responseBody.getName(), "Adidas Shoes");
        assertEquals("Product updated correct description", responseBody.getDescription(), "Beautiful shoes");
        assertEquals("Product updated correct price", 0, BigDecimal.valueOf(150.00).compareTo(responseBody.getPrice()));
    }

    @Test
    @FlywayTest
    public void deleteProductInStoreOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());
        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);

        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseProductId = mapper.readValue(response.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseDelete = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "/" + responseProductId.getId(), null, HttpMethod.DELETE);
        assertEquals("Delete product OK", HttpStatus.NO_CONTENT, responseDelete.getStatusCode());

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId() + "?productId=" + responseProductId.getId(), null, HttpMethod.GET);
        assertEquals("Product by id NOT FOUND", HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }
}
