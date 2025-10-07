package com.consolefire.xpenser.expenseimport.service;

import com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto;
import java.io.File;
import java.io.InputStream;

public interface ReceiptImportService {

    ReceiptImportDto importFrom(byte[] bytes);

    ReceiptImportDto importFrom(File file);

    ReceiptImportDto importFrom(InputStream inputStream);

}
