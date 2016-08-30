package com.tenx.ms.retail.repository;

import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.client.repository.ClientRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by goropeza on 28/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@Transactional
public class TestClientRepository {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void saveClientOK() {
        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robbin", "5554321110");
        client = clientRepository.save(client);
        assertTrue("Client inserted", (long) client.getId() > 0);
    }

    @Test
    public void saveClientFailEmailNull() {
        ClientEntity client = new ClientEntity(null, "Alex", "Robbin", "5554321110");
        try {
            client = clientRepository.save(client);
        } catch (DataIntegrityViolationException ex) {
            assertEquals("Data integrity violation thrown", ex.getClass(), DataIntegrityViolationException.class);
        }
        assertEquals("Client not inserted", null, client.getId());
    }

    @Test
    public void saveClientFailPhoneInvalid() {
        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robbin", "555110");
        try {
            client = clientRepository.save(client);
        } catch (ConstraintViolationException ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertEquals("Client not inserted", null, client.getId());
    }

    @Test
    public void saveClientFailEmailInvalid() {
        ClientEntity client = new ClientEntity("clientemailfake", "Alex", "Robbin", "5554321110");
        try {
            client = clientRepository.save(client);
        } catch (ConstraintViolationException ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertEquals("Client not inserted", null, client.getId());
    }

    @Test
    public void saveClientFailFirstNameInvalid() {
        ClientEntity client = new ClientEntity("client@domain.co", "Alex1", "Robbin", "555110");
        try {
            client = clientRepository.save(client);
        } catch (ConstraintViolationException ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertEquals("Client not inserted", null, client.getId());
    }

    @Test
    public void saveClientFailLastNameInvalid() {
        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robb3n", "5554321110");
        try {
            client = clientRepository.save(client);
        } catch (ConstraintViolationException ex) {
            assertEquals("Constraint violation thrown", ex.getClass(), ConstraintViolationException.class);
        }
        assertEquals("Client not inserted", null, client.getId());
    }

    @Test
    public void getAndUpdateClientOK() {
        ClientEntity client = new ClientEntity("client@domain.co", "Alex", "Robbin", "5554321110");
        client = clientRepository.save(client);
        Long id =  client.getId();
        client = clientRepository.findById(id).get();
        assertEquals("Client found", id, client.getId());

        client.setFirstName("Alexander");
        client.setLastName("Robbin");
        client.setPhone("5555123456");
        client = clientRepository.save(client);
        assertEquals("Client updated", id, client.getId());
        assertEquals("Client first name is correct", client.getFirstName(), "Alexander");
    }
}
