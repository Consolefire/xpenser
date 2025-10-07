package com.consolefire.xpenser.expenseimport.service.impl;

import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.CardDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.CouponDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.ItemDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.MetadataDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.PaymentDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.TaxDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.TaxSectionDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.TaxUnitDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.UnitValueDto;

import com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto;
import com.consolefire.xpenser.expenseimport.service.ReceiptImportService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelReceiptImportService implements ReceiptImportService {

    @Override
    public ReceiptImportDto importFrom(byte[] bytes) {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            return convertStreamToDto(is);
        } catch (IOException e) {
            throw new RuntimeException("Error importing from byte array", e);
        }
    }

    @Override
    public ReceiptImportDto importFrom(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return convertStreamToDto(is);
        } catch (IOException e) {
            throw new RuntimeException("Error importing from file: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public ReceiptImportDto importFrom(InputStream inputStream) {
        try {
            return convertStreamToDto(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error importing from InputStream", e);
        }
    }

    // --- Helper Methods (Copied from previous steps) ---

    private static String getCellValue(Sheet sheet, int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            return "";
        }
        // Use Cell for reading, DataFormatter for string conversion
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(row.getCell(colNum));
    }

    private static double safeParseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // --- Core Conversion Logic ---

    private ReceiptImportDto convertStreamToDto(InputStream inputStream) throws IOException {

        ReceiptImportDto receipt = new ReceiptImportDto();

        // Use WorkbookFactory for general file types, or XSSFWorkbook for .xlsx
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // --- 1. Fixed Header Mapping ---

            MetadataDto metadata = new MetadataDto();
            metadata.setCountryCode(getCellValue(sheet, 0, 2));      // C1 (Row 1, Col C)
            metadata.setTerminalNumber(getCellValue(sheet, 2, 2));  // C3
            metadata.setStoreNumber(getCellValue(sheet, 3, 2));      // C4
            metadata.setSerialNumber(getCellValue(sheet, 4, 2));    // C5
            receipt.setMetadata(metadata);

            receipt.setIdentifier(getCellValue(sheet, 1, 2));          // C2
            receipt.setMarketPlaceCode(getCellValue(sheet, 5, 2));     // C6
            receipt.setStoreCode(getCellValue(sheet, 6, 2));           // C7
            receipt.setCurrency(getCellValue(sheet, 9, 2));            // C10
            receipt.setTotal(safeParseDouble(getCellValue(sheet, 10, 2))); // C11

            String dateStr = getCellValue(sheet, 7, 2);
            String timeStr = getCellValue(sheet, 8, 2);
            receipt.setTimestamp(dateStr + "T" + timeStr);

            // --- 2. Payments Section (Starts Row 14, Index 13) ---

            List<PaymentDto> payments = new ArrayList<>();
            for (int i = 13; i <= sheet.getLastRowNum(); i++) {
                String mode = getCellValue(sheet, i, 1); // Col B

                if (mode.isEmpty() || mode.equalsIgnoreCase("Taxes")) {
                    break;
                }

                PaymentDto payment = new PaymentDto();
                payment.setMode(mode);
                payment.setAmount(safeParseDouble(getCellValue(sheet, i, 2)));  // C#
                payment.setCurrency(getCellValue(sheet, i, 3));                 // D#

                if (mode.equalsIgnoreCase("CARD")) {
                    CardDto card = new CardDto();
                    card.setType(getCellValue(sheet, i, 4));        // E#
                    card.setNumber(getCellValue(sheet, i, 5));      // F#
                    payment.setCard(card);
                } else if (mode.equalsIgnoreCase("COUPON")) {
                    CouponDto coupon = new CouponDto();
                    coupon.setType(getCellValue(sheet, i, 4));      // E#
                    coupon.setIdentifier(getCellValue(sheet, i, 5)); // F#
                    payment.setCoupon(coupon);
                }
                payments.add(payment);
            }
            receipt.setPayments(payments);

            // --- 3. Taxes Section (Starts Row 20, Index 19) ---

            TaxSectionDto taxSection = new TaxSectionDto();
            taxSection.setTotal(safeParseDouble(getCellValue(sheet, 17, 2))); // C18

            List<TaxDto> taxes = new ArrayList<>();
            for (int i = 19; i <= sheet.getLastRowNum(); i++) {
                String taxType = getCellValue(sheet, i, 1); // Col B

                if (taxType.isEmpty() || taxType.equalsIgnoreCase("Items")) {
                    break;
                }

                TaxDto taxEntry = new TaxDto();
                taxEntry.setType(taxType);

                TaxUnitDto taxUnit = new TaxUnitDto();
                taxUnit.setValue(safeParseDouble(getCellValue(sheet, i, 2))); // C20
                taxUnit.setUnit("PERCENT");
                taxEntry.setTaxUnit(taxUnit);

                taxEntry.setTaxableAmount(safeParseDouble(getCellValue(sheet, i, 3))); // D20
                taxEntry.setAmount(safeParseDouble(getCellValue(sheet, i, 4)));        // E20
                taxEntry.setItemCategory("GENERAL");

                taxes.add(taxEntry);
            }
            taxSection.setTaxes(taxes);
            receipt.setTax(taxSection);

            // --- 4. Items Section (Starts Row 24, Index 23) ---

            List<ItemDto> items = new ArrayList<>();
            for (int i = 23; i <= sheet.getLastRowNum(); i++) {
                String code = getCellValue(sheet, i, 1); // Col B

                if (code.isEmpty()) {
                    break;
                }

                ItemDto item = new ItemDto();
                item.setCode(code);                                         // B#
                item.setTitle(getCellValue(sheet, i, 2));                   // C#
                item.setQuantity(safeParseDouble(getCellValue(sheet, i, 3))); // D#
                item.setPrice(safeParseDouble(getCellValue(sheet, i, 6)));    // G#
                item.setCategoryCode(getCellValue(sheet, i, 7));            // H#

                UnitValueDto unitValue = new UnitValueDto();
                unitValue.setValue(safeParseDouble(getCellValue(sheet, i, 4))); // E#
                unitValue.setUnitCode(getCellValue(sheet, i, 5));               // F#
                item.setUnitValue(unitValue);

                items.add(item);
            }
            receipt.setItems(items);

            return receipt;

        } catch (Exception e) {
            // Rethrow as an unchecked exception for service layer handling
            throw new IOException("Failed to parse Excel content.", e);
        }
    }
}