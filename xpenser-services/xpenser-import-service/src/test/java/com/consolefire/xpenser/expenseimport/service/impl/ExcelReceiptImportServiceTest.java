package com.consolefire.xpenser.expenseimport.service.impl;

import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.CardDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.CouponDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.ItemDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.MetadataDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.TaxDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.TaxUnitDto;
import static com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto.UnitValueDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto;
import com.consolefire.xpenser.expenseimport.service.ReceiptImportService;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExcelReceiptImportServiceTest {

    private ReceiptImportService service;
    private File testFile;
    private final String TEST_FILE_PATH = "test-data/Sample-receipt-Aldi.xlsx";

    @BeforeEach
    void setUp() {
        // Initialize the service implementation
        service = new ExcelReceiptImportService();

        // Load the test file from the classpath resources directory
        // In a standard Maven/Gradle project, this file path corresponds to src/test/resources/test-data/Sample-receipt-Aldi.xlsx
        ClassLoader classLoader = getClass().getClassLoader();
        // The file was uploaded as CSV, but the test assumes an XLSX file can be provided later.
        // For testing purposes, we assume the test-data/Sample-receipt-Aldi.xlsx exists in resources
        testFile = new File(Objects.requireNonNull(classLoader.getResource(TEST_FILE_PATH)).getFile());
    }

    // --- Core Test Case ---

    @Test
    void importFrom_File_ShouldSuccessfullyParseAllFields() throws Exception {
        // ACT
        ReceiptImportDto actualDto = service.importFrom(testFile);

        // ASSERT
        assertThat(actualDto).isNotNull();

        // 1. Assert Header Fields
        assertThat(actualDto.getIdentifier()).isEqualTo("546546-8768768k-jhkj");
        assertThat(actualDto.getMarketPlaceCode()).isEqualTo("ALDI_SUD");
        assertThat(actualDto.getStoreCode()).isEqualTo("Mering-12345");
        assertThat(actualDto.getCurrency()).isEqualTo("EURO");
        // Using BigDecimal comparison is safer for currency, but Double.compare works for this simple case.
        assertThat(actualDto.getTotal()).isEqualTo(12.23);
        assertThat(actualDto.getTimestamp()).isEqualTo("2025-02-20T08:22:00");

        // 2. Assert Metadata
        assertThat(actualDto.getMetadata())
            .extracting(
                MetadataDto::getCountryCode,
                MetadataDto::getTerminalNumber,
                MetadataDto::getStoreNumber,
                MetadataDto::getSerialNumber
            ).containsExactly("DE", "123", "324", "432");

        // 3. Assert Payments (Complex List)
        assertThat(actualDto.getPayments()).hasSize(3);

        // Payment 1: CARD
        assertThat(actualDto.getPayments().get(0))
            .hasFieldOrPropertyWithValue("mode", "CARD")
            .hasFieldOrPropertyWithValue("amount", 12.34)
            .hasFieldOrPropertyWithValue("currency", "EURO");
        assertThat(actualDto.getPayments().get(0).getCard())
            .isNotNull()
            .extracting(CardDto::getType, CardDto::getNumber)
            .containsExactly("GIRO_CARD", "898989");
        assertThat(actualDto.getPayments().get(0).getCoupon()).isNull(); // Should be null for CARD

        // Payment 2: CASH
        assertThat(actualDto.getPayments().get(1))
            .hasFieldOrPropertyWithValue("mode", "CASH")
            .hasFieldOrPropertyWithValue("amount", 2.34);
        assertThat(actualDto.getPayments().get(1).getCard()).isNull();
        assertThat(actualDto.getPayments().get(1).getCoupon()).isNull();

        // Payment 3: COUPON
        assertThat(actualDto.getPayments().get(2))
            .hasFieldOrPropertyWithValue("mode", "COUPON")
            .hasFieldOrPropertyWithValue("amount", 12.34);
        assertThat(actualDto.getPayments().get(2).getCoupon())
            .isNotNull()
            .extracting(CouponDto::getType, CouponDto::getIdentifier)
            .containsExactly("RECYCLE", "AC989");
        assertThat(actualDto.getPayments().get(2).getCard()).isNull();

        // 4. Assert Taxes
        assertThat(actualDto.getTax().getTotal()).isEqualTo(1.23);
        assertThat(actualDto.getTax().getTaxes()).hasSize(1);

        TaxDto tax = actualDto.getTax().getTaxes().get(0);
        assertThat(tax)
            .hasFieldOrPropertyWithValue("type", "VAT")
            .hasFieldOrPropertyWithValue("taxableAmount", 12.22)
            .hasFieldOrPropertyWithValue("amount", 1.23)
            .hasFieldOrPropertyWithValue("itemCategory", "GENERAL");
        assertThat(tax.getTaxUnit())
            .extracting(TaxUnitDto::getValue, TaxUnitDto::getUnit)
            .containsExactly(0.07, "PERCENT");

        // 5. Assert Items
        assertThat(actualDto.getItems()).hasSize(2);

        // Item 1
        ItemDto item1 = actualDto.getItems().get(0);
        assertThat(item1)
            .hasFieldOrPropertyWithValue("code", "12334")
            .hasFieldOrPropertyWithValue("title", "Jjhg jGH kjhg")
            .hasFieldOrPropertyWithValue("quantity", 2.0)
            .hasFieldOrPropertyWithValue("price", 2.0)
            .hasFieldOrPropertyWithValue("categoryCode", "A");
        assertThat(item1.getUnitValue())
            .extracting(UnitValueDto::getValue, UnitValueDto::getUnitCode)
            .containsExactly(1.0, "KG");

        // Item 2
        ItemDto item2 = actualDto.getItems().get(1);
        assertThat(item2)
            .hasFieldOrPropertyWithValue("code", "Auy987987ss")
            .hasFieldOrPropertyWithValue("title", "Yuyiuyiy ")
            .hasFieldOrPropertyWithValue("quantity", 4.0)
            .hasFieldOrPropertyWithValue("price", 4.8)
            .hasFieldOrPropertyWithValue("categoryCode", "B");
        assertThat(item2.getUnitValue())
            .extracting(UnitValueDto::getValue, UnitValueDto::getUnitCode)
            .containsExactly(1.2, "P");
    }

    // --- Other Interface Method Tests ---

    @Test
    void importFrom_InputStream_ShouldSuccessfullyParseAllFields() throws Exception {
        // ARRANGE
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(TEST_FILE_PATH)) {
            // ACT
            ReceiptImportDto actualDto = service.importFrom(is);

            // ASSERT (Simple check to ensure data flowed through)
            assertThat(actualDto).isNotNull();
            assertThat(actualDto.getIdentifier()).isEqualTo("546546-8768768k-jhkj");
            assertThat(actualDto.getItems()).hasSize(2);
        }
    }

    @Test
    void importFrom_ByteArray_ShouldSuccessfullyParseAllFields() throws Exception {
        // ARRANGE
        byte[] fileBytes;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(TEST_FILE_PATH)) {
            fileBytes = is.readAllBytes();
        }

        // ACT
        ReceiptImportDto actualDto = service.importFrom(fileBytes);

        // ASSERT
        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getTotal()).isEqualTo(12.23);
    }

    @Test
    void importFrom_File_ShouldThrowRuntimeExceptionOnFileNotFound() {
        // ARRANGE
        File nonExistentFile = new File("non-existent-data/missing.xlsx");

        // ACT & ASSERT
        // The service implementation wraps IOException in a RuntimeException
        assertThrows(RuntimeException.class, () -> service.importFrom(nonExistentFile));
    }
}