package com.tenx.ms.retail.client.service;

import com.tenx.ms.retail.client.domain.ClientEntity;
import com.tenx.ms.retail.client.repository.ClientRepository;
import com.tenx.ms.retail.client.rest.dto.Client;
import com.tenx.ms.retail.client.util.ClientConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by goropeza on 30/08/16.
 */
@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientConverter clientConverter;

    public Client findClientById(Long clientId) {
        Optional<ClientEntity> result = clientRepository.findById(clientId);
        if (result.isPresent()) {
            return clientConverter.repositoryToApiModel(result.get());
        } else {
            throw new NoSuchElementException("Client not found");
        }
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll().stream().map(clientConverter.entityToClient).collect(Collectors.toList());
    }

    public void deleteClient(Long clientId) {
        if(!exists(clientId)) {
            throw new NoSuchElementException("Client not found");
        } else {
            clientRepository.delete(clientId);
        }
    }

    public boolean exists(Long clientId) {
        return clientRepository.exists(clientId);
    }

    public Client insertClient(Client client) {
        ClientEntity entity = clientConverter.apiModelToRepository(client);
        entity = clientRepository.save(entity);
        return clientConverter.repositoryToApiModel(entity);
    }

    public Client updateClient(Long storeId, Client client) {
        Optional<ClientEntity> result = clientRepository.findById(storeId);
        if (result.isPresent()) {
            ClientEntity entity = result.get();
            entity.setFirstName(client.getFirstName());
            entity.setLastName(client.getLastName());
            entity.setEmail(client.getEmail());
            entity.setPhone(client.getPhone());
            entity = clientRepository.save(entity);
            return clientConverter.repositoryToApiModel(entity);
        } else {
            throw new NoSuchElementException("Client not found");
        }
    }
}
