package com.consolefire.xpenser.cloud.auth.data.repository;

import com.consolefire.xpenser.cloud.auth.data.entity.AuthorizationConsent;
import com.consolefire.xpenser.cloud.auth.data.entity.AuthorizationConsent.AuthorizationConsentId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationConsentRepository extends JpaRepository<AuthorizationConsent, AuthorizationConsentId> {

    Optional<AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId,
        String principalName);

    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}