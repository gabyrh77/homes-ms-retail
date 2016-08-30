package com.tenx.ms.retail.client.util;

import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.client.rest.dto.Client;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Created by goropeza on 30/08/16.
 */
@Service
public class ClientConverter {

    public Client repositoryToApiModel(ClientEntity entity) {
        if (entity != null) {
            return new Client(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getPhone());
        }
        return null;
    }

    public ClientEntity apiModelToRepository(Client client) {
        if (client != null) {
            return new ClientEntity(client.getEmail(), client.getFirstName(), client.getLastName(), client.getPhone());
        }
        return null;
    }

    public Function<ClientEntity, Client> entityToClient = entity -> new
        Client(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getPhone());
}
