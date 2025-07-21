package com.consolefire.xpenser.cloud.user.data.repository;

import com.consolefire.xpenser.cloud.user.data.entity.Privilege;
import com.consolefire.xpenser.cloud.user.data.entity.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends ReactiveCrudRepository<Role, Long> {

    Mono<Role> findByName(String name);

    // Custom query to fetch roles for a given user ID
    @Query("SELECT r.* FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId")
    Flux<Role> findRolesByUserId(Long userId);

    // Custom query to fetch privileges for a given role ID
    @Query("SELECT p.* FROM privileges p JOIN role_privileges rp ON p.id = rp.privilege_id WHERE rp.role_id = :roleId")
    Flux<Privilege> findPrivilegesByRoleId(Long roleId);
}