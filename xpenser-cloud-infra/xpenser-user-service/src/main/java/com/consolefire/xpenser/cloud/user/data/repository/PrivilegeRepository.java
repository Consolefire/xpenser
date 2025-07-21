package com.consolefire.xpenser.cloud.user.data.repository;

import com.consolefire.xpenser.cloud.user.data.entity.Privilege;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PrivilegeRepository extends ReactiveCrudRepository<Privilege, Long> {

    Mono<Privilege> findByName(String name);
}