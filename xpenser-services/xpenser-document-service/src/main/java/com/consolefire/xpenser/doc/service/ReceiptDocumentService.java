package com.consolefire.xpenser.doc.service;

import com.consolefire.xpenser.doc.data.dto.ReceiptDocumentDto;
import com.consolefire.xpenser.doc.data.dto.ReceiptDocumentUploadDto;

public interface ReceiptDocumentService {

    ReceiptDocumentDto createReceiptDocument(ReceiptDocumentUploadDto receiptDocumentUploadDto);

}
