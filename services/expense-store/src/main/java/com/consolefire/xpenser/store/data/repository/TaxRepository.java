package com.consolefire.xpenser.store.data.repository; // Updated package

import com.consolefire.xpenser.store.data.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<TaxEntity, Long> {
}