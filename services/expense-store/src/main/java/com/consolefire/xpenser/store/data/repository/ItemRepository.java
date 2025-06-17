package com.consolefire.xpenser.store.data.repository;

import com.consolefire.xpenser.store.data.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Marks this interface as a Spring Data JPA repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    // Example of a custom query method: find all items by a specific code
    List<ItemEntity> findByCode(String code);

    // Example of finding items belonging to a specific receipt
    List<ItemEntity> findByReceiptId(Long receiptId);
}