package com.tenx.ms.retail.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
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
 * Created by goropeza on 26/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@ActiveProfiles(Profiles.TEST_NOAUTH)
public class TestProductController extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    private static final String API_VERSION = RestConstants.VERSION_ONE;

    public static final String REQUEST_URI = "%s" + API_VERSION + "/products/";

    private final RestTemplate template = new TestRestTemplate();

    @Value("classpath:assets/new_product.json")
    private File newProductRequest;

    @Value("classpath:assets/new_store.json")
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
    public void createProductInStoreNull() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "99999", null, HttpMethod.POST);
        assertEquals("Create product bad request", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createProductInStoreOk() throws IOException {
        ResponseEntity<String> responseStore = getJSONResponse(template, String.format(TestStoreController.REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created", HttpStatus.CREATED, responseStore.getStatusCode());

        ResourceCreated responseStoreId = mapper.readValue(responseStore.getBody(), ResourceCreated.class);
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseStoreId.getId(),
            FileUtils.readFileToString(newProductRequest), HttpMethod.POST);
        assertEquals("Product created", HttpStatus.CREATED, response.getStatusCode());
    }
}
