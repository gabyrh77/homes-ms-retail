package com.tenx.ms.retail.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.store.rest.dto.Store;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by goropeza on 25/08/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@ActiveProfiles(Profiles.TEST_NOAUTH)
public class TestStoreController extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    private static final String API_VERSION = RestConstants.VERSION_ONE;

    public static final String REQUEST_URI = "%s" + API_VERSION + "/stores/";

    private final RestTemplate template = new TestRestTemplate();

    @Value("classpath:assets/new_store.json")
    private File newStoreRequest;

    @Value("classpath:assets/update_store.json")
    private File updateStoreRequest;

    @Value("classpath:assets/new_store_empty_name.json")
    private File newStoreEmptyNameRequest;

    @Value("classpath:assets/new_store_null_name.json")
    private File newStoreNullNameRequest;


    @Test
    public void getStoreByIdNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", null, HttpMethod.GET);
        assertEquals("Get a store not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getStoreByIdStringNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "1str", null, HttpMethod.GET);
        assertEquals("Get a store not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateStoreByIdNull() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", null, HttpMethod.PUT);
        assertEquals("Update store bad request", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void updateStoreByIdEmptyObject() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", "{}", HttpMethod.PUT);
        assertEquals("Update store not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateStoreByIdNotFound() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", FileUtils.readFileToString(newStoreRequest), HttpMethod.PUT);
        assertEquals("Update store not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAllStores() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), null, HttpMethod.GET);
        assertEquals("Get all stores ok", HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteStoreNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", null, HttpMethod.DELETE);
        assertEquals("Delete store not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createStoreNameNull() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreNullNameRequest), HttpMethod.POST);
        assertEquals("Create store validation name null", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void createStoreNameEmpty() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreEmptyNameRequest), HttpMethod.POST);
        assertEquals("Create store validation name empty", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void createStoreNameMaxCharacters() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), "{\"name\": \"Really long name to break the max characters limit of the database\"}", HttpMethod.POST);
        assertEquals("Create store validation name too long", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createAndGetStoreOK() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Store created response", (int)responseId.getId() > 0);

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseId.getId(), null, HttpMethod.GET);
        assertEquals("Get a store by id OK", HttpStatus.OK, responseGet.getStatusCode());
        Store store = mapper.readValue(responseGet.getBody(), Store.class);
        assertEquals("Store by id, correct id", Long.valueOf(1), store.getStoreId());
        assertEquals("Store by id, correct name", "Adidas", store.getName());
    }

    @Test
    @FlywayTest
    public void createStoreDuplicated() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Store created response", (int)responseId.getId() > 0);

        ResponseEntity<String> responseDuplicated = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store not created status", HttpStatus.PRECONDITION_FAILED, responseDuplicated.getStatusCode());

        ResponseEntity<String> responseGet = getJSONResponse(template, String.format(REQUEST_URI, basePath()), null, HttpMethod.GET);
        assertEquals("Get all stores ok", HttpStatus.OK, responseGet.getStatusCode());
        List<Store> stores = mapper.readValue(responseGet.getBody(), List.class);
        assertNotNull("Get all stores not null", stores);
        assertEquals("Get all stores correct size", stores.size(), 1);
    }

    @Test
    @FlywayTest
    public void updateStoreNameNull() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Store created response", (int)responseId.getId() > 0);

        ResponseEntity<String> responseUpdate = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseId.getId(), FileUtils.readFileToString(newStoreNullNameRequest), HttpMethod.PUT);
        assertEquals("Update store validation name null", HttpStatus.PRECONDITION_FAILED, responseUpdate.getStatusCode());
    }

    @Test
    @FlywayTest
    public void updateStoreNameEmpty() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Store created response", (int)responseId.getId() > 0);

        ResponseEntity<String> responseUpdate = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseId.getId(), FileUtils.readFileToString(newStoreEmptyNameRequest), HttpMethod.PUT);
        assertEquals("Update store validation name empty", HttpStatus.PRECONDITION_FAILED, responseUpdate.getStatusCode());
    }

    @Test
    @FlywayTest
    public void updateStoreOK() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Store created response", (int)responseId.getId() > 0);

        ResponseEntity<String> responseUpdate = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseId.getId(), FileUtils.readFileToString(updateStoreRequest), HttpMethod.PUT);
        assertEquals("Store updated status", HttpStatus.OK, responseUpdate.getStatusCode());
        Store responseBodyUpdate = mapper.readValue(responseUpdate.getBody(), Store.class);
        assertEquals("Store updated id", (int)responseId.getId(), responseBodyUpdate.getStoreId().intValue());
        assertEquals("Store updated name", responseBodyUpdate.getName(), "Adidas C.A.");
    }

    @Test
    @FlywayTest
    public void deleteStoreOK() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newStoreRequest), HttpMethod.POST);
        assertEquals("Store created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseDelete = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseId.getId(), null, HttpMethod.DELETE);
        assertEquals("Delete store not found", HttpStatus.NO_CONTENT, responseDelete.getStatusCode());
    }
}
