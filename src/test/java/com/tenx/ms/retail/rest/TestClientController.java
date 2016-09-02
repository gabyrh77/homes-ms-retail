package com.tenx.ms.retail.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.client.rest.dto.Client;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by goropeza on 25/08/16.
 */

@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@ActiveProfiles(Profiles.TEST_NOAUTH)
public class TestClientController extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    private static final String API_VERSION = RestConstants.VERSION_ONE;

    public static final String REQUEST_URI = "%s" + API_VERSION + "/clients/";

    private final RestTemplate template = new TestRestTemplate();

    @Value("classpath:rest/assets/new_client.json")
    private File newClientRequest;

    @Value("classpath:rest/assets/new_client_invalid_name.json")
    private File newClientInvalidNameRequest;

    @Value("classpath:rest/assets/update_client.json")
    private File updateClientRequest;

    @Value("classpath:rest/assets/new_client_empty_name.json")
    private File newClientEmptyNameRequest;

    @Value("classpath:rest/assets/new_client_null_name.json")
    private File newClientNullNameRequest;


    @Test
    public void getClientByIdNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", null, HttpMethod.GET);
        assertEquals("Get a client not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getClientByIdStringNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "1str", null, HttpMethod.GET);
        assertEquals("Get a client not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateClientByIdNull() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", null, HttpMethod.PUT);
        assertEquals("Update client bad request", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void updateClientByIdNotFound() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", FileUtils.readFileToString(newClientRequest), HttpMethod.PUT);
        assertEquals("Update client not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAllClients() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), null, HttpMethod.GET);
        assertEquals("Get all clients ok", HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteClientNotFound() {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + "9999", null, HttpMethod.DELETE);
        assertEquals("Delete client not found", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createClientNameNull() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientNullNameRequest), HttpMethod.POST);
        assertEquals("Create client validation name null", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());

        Map responseBody = mapper.readValue(response.getBody(), new TypeReference<Map>() {});
        assertNotNull("Error body exists", responseBody);
        assertTrue("Exists errors", responseBody.containsKey("errors"));
        assertNotNull("Error list exists", responseBody.get("errors"));
        List<Map> errors = (List<Map>) responseBody.get("errors");
        assertEquals("Error list correct size", errors.size(), 1);

        assertEquals("Error list correct field", errors.get(0).get("object_name"), "lastName");
        assertEquals("Error list correct message", errors.get(0).get("default_message"), "may not be null");
    }

    @Test
    public void createClientNameEmpty() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientEmptyNameRequest), HttpMethod.POST);
        assertEquals("Create client validation name empty", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());

        Map responseBody = mapper.readValue(response.getBody(), new TypeReference<Map>() {});
        assertNotNull("Error body exists", responseBody);
        assertTrue("Exists errors", responseBody.containsKey("errors"));
        assertNotNull("Error list exists", responseBody.get("errors"));
        List<Map> errors = (List<Map>) responseBody.get("errors");
        assertEquals("Error list correct size", errors.size(), 2);
        ArrayList<String> fields = new ArrayList<>();
        fields.add("firstName");
        fields.add("lastName");
        assertTrue("Error list correct field", fields.contains(errors.get(0).get("object_name").toString()));
        assertEquals("Error list correct message", errors.get(0).get("default_message"), "size must be between 1 and 50");

        assertTrue("Error list correct field", fields.contains(errors.get(1).get("object_name").toString()));
        assertEquals("Error list correct message", errors.get(1).get("default_message"), "size must be between 1 and 50");
    }


    @Test
    @FlywayTest
    public void createClientInvalidName() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientInvalidNameRequest), HttpMethod.POST);
        assertEquals("Client invalid name status", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
        Map responseBody = mapper.readValue(response.getBody(), new TypeReference<Map>() {});
        assertNotNull("Error body exists", responseBody);
        assertTrue("Exists errors", responseBody.containsKey("errors"));
        assertNotNull("Error list exists", responseBody.get("errors"));
        List<Map> errors = (List<Map>) responseBody.get("errors");
        assertEquals("Error list correct size", errors.size(), 1);

        assertEquals("Error list correct field", errors.get(0).get("object_name"), "firstName");
        assertEquals("Error list correct message", errors.get(0).get("default_message"), "must match \"[a-zA-Z]*\"");
    }

    @Test
    @FlywayTest
    public void createClientOK() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Client created response", (int)responseId.getId() > 0);
    }

    @Test
    @FlywayTest
    public void updateClientOK() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Client created response", (int)responseId.getId() > 0);

        ResponseEntity<String> responseUpdate = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseId.getId(), FileUtils.readFileToString(updateClientRequest), HttpMethod.PUT);
        assertEquals("Client updated status", HttpStatus.OK, responseUpdate.getStatusCode());
        Client responseBodyUpdate = mapper.readValue(responseUpdate.getBody(), Client.class);
        assertEquals("Client updated id", (int)responseId.getId(), responseBodyUpdate.getClientId().intValue());
        assertEquals("Client updated first name", responseBodyUpdate.getFirstName(), "Guy");
        assertEquals("Client updated last name", responseBodyUpdate.getLastName(), "Timon");
        assertEquals("Client updated phone", responseBodyUpdate.getPhone(), "5555345678");
    }

    @Test
    @FlywayTest
    public void updateClientInvalidName() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Client created response", (int)responseId.getId() > 0);

        ResponseEntity<String> responseUpdate = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseId.getId(), FileUtils.readFileToString(newClientInvalidNameRequest), HttpMethod.PUT);
        assertEquals("Client updated invalid name", HttpStatus.PRECONDITION_FAILED, responseUpdate.getStatusCode());

        Map responseBody = mapper.readValue(responseUpdate.getBody(), new TypeReference<Map>() {});
        assertNotNull("Error body exists", responseBody);
        assertTrue("Exists errors", responseBody.containsKey("errors"));
        assertNotNull("Error list exists", responseBody.get("errors"));
        List<Map> errors = (List<Map>) responseBody.get("errors");
        assertEquals("Error list correct size", errors.size(), 1);

        assertEquals("Error list correct field", errors.get(0).get("object_name"), "firstName");
        assertEquals("Error list correct message", errors.get(0).get("default_message"), "must match \"[a-zA-Z]*\"");

    }

    @Test
    @FlywayTest
    public void createClientEmailDuplicated() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);
        assertTrue("Client created response", (int)responseId.getId() > 0);

        ResponseEntity<String> responseDuplicated = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client not created", HttpStatus.PRECONDITION_FAILED, responseDuplicated.getStatusCode());

        Map responseBody = mapper.readValue(responseDuplicated.getBody(), new TypeReference<Map>() {});
        assertNotNull("Error body exists", responseBody);
        assertTrue("Exists errors", responseBody.containsKey("errors"));
        assertNotNull("Error list exists", responseBody.get("errors"));
        List<Map> errors = (List<Map>) responseBody.get("errors");
        assertEquals("Error list correct size", errors.size(), 1);

        assertEquals("Error list correct field", errors.get(0).get("object_name"), "email");
        assertEquals("Error list correct message", errors.get(0).get("default_message"), "is unique and already exists");

    }

    @Test
    @FlywayTest
    public void deleteClientOK() throws IOException {
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(newClientRequest), HttpMethod.POST);
        assertEquals("Client created status", HttpStatus.CREATED, response.getStatusCode());
        ResourceCreated responseId = mapper.readValue(response.getBody(), ResourceCreated.class);

        ResponseEntity<String> responseDelete = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + responseId.getId(), null, HttpMethod.DELETE);
        assertEquals("Client store not found", HttpStatus.NO_CONTENT, responseDelete.getStatusCode());
    }
}
