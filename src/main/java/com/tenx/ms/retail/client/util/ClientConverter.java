package com.tenx.ms.retail.client.util;

import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.client.rest.dto.Client;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Created by goropeza on 30/08/16.
 */
@Component
public class ClientConverter {

    public Client repositoryToApiModel(ClientEntity entity) {
        return new Client(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getPhone());
    }

    public ClientEntity apiModelToRepository(Client client) {
        return new ClientEntity(client.getEmail(), client.getFirstName(), client.getLastName(), client.getPhone());
    }

    public Function<ClientEntity, Client> entityToClient = entity -> new
        Client(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getPhone());
}
