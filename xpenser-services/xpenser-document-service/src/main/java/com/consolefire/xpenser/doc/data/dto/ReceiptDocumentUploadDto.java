package com.consolefire.xpenser.doc.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDocumentUploadDto {

    private String receiptId;
    private String fileChecksum;
    private byte[] fileContent;
    private String fileName;

}
