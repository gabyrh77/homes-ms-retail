package com.tenx.ms.retail.client.repository;

import com.tenx.ms.retail.client.domain.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by goropeza on 28/08/16.
 */
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    ClientEntity findByEmail(String email);
}
