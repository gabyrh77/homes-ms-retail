package com.tenx.ms.retail.client.repository;

import com.tenx.ms.retail.client.domain.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by goropeza on 28/08/16.
 */
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findById(Long id);
}
