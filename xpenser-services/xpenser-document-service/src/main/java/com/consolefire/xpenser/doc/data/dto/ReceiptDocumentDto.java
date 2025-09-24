package com.consolefire.xpenser.doc.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDocumentDto {

    private Long id;
    private String receiptId;
    private String fileIdentifier;
    private String fileName;
    private String fileUrl;
    private String fileChecksum;

}
