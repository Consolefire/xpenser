package com.consolefire.tools.mdm.data.repository;

import com.consolefire.tools.mdm.data.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

}
