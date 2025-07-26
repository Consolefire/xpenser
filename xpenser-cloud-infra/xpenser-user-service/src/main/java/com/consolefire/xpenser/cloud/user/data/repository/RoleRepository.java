package com.consolefire.xpenser.cloud.user.data.repository;

import com.consolefire.xpenser.cloud.user.data.entity.Privilege;
import com.consolefire.xpenser.cloud.user.data.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    // Custom query to fetch roles for a given user ID
    @Query(nativeQuery = true, value = "SELECT r.* FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    // Custom query to fetch privileges for a given role ID
    @Query(nativeQuery = true, value = "SELECT p.* FROM privileges p JOIN role_privileges rp ON p.id = rp.privilege_id WHERE rp.role_id = :roleId")
    List<Privilege> findPrivilegesByRoleId(@Param("roleId") Long roleId);
}