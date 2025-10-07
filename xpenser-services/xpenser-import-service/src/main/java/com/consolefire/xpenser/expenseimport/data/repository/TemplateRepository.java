package com.consolefire.xpenser.expenseimport.data.repository;

import com.consolefire.xpenser.expenseimport.data.entity.Template;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    boolean existsByName(String name);

    Optional<Template> findByName(@Param("name") String name);

}
