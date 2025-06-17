package com.consolefire.xpenser.store.data.repository; // Updated package

import com.consolefire.xpenser.store.data.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    // No specific finders needed for now, as Payment is typically nested
}