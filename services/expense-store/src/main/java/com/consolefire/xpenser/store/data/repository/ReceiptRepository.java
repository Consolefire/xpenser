package com.consolefire.xpenser.store.data.repository;

import com.consolefire.xpenser.store.data.entity.ReceiptEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long> {

    @Query("SELECT r FROM ReceiptEntity r WHERE r.identifier = :identifier AND r.marketPlaceCode = :marketPlaceCode AND r.storeCode = :storeCode")
    Optional<ReceiptEntity> findByUniqueKey(@Param("identifier") String identifier,
        @Param("marketPlaceCode") String marketPlaceCode,
        @Param("storeCode") String storeCode);
}