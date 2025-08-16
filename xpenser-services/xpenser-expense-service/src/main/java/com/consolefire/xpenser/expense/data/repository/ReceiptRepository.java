package com.consolefire.xpenser.expense.data.repository;

import com.consolefire.xpenser.expense.data.entiry.ReceiptEntity;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long> {

    Optional<ReceiptEntity> findByIdentifierAndMarketPlaceCodeAndStoreCode(@NotBlank String identifier,
        @NotBlank String marketPlaceCode, @NotBlank String storeCode);

}
