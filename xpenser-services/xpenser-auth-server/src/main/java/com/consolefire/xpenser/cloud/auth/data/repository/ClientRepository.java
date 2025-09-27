package com.consolefire.xpenser.cloud.auth.data.repository;

import com.consolefire.xpenser.cloud.auth.data.entity.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByClientId(String clientId);
}
