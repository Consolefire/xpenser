package com.consolefire.xpenser.doc.data.repository;

import com.consolefire.xpenser.doc.data.entity.ReceiptDocumentEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptDocumentRepository extends JpaRepository<ReceiptDocumentEntity, Long> {

    Optional<ReceiptDocumentEntity> findByReceiptId(String receiptId);

    Optional<ReceiptDocumentEntity> findByReceiptFile_Checksum(String checksum);

    Optional<ReceiptDocumentEntity> findByReceiptFile_FileId(String fileId);

}
