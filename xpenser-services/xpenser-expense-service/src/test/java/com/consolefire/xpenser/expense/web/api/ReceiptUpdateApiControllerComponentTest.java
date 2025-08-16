package com.consolefire.xpenser.expense.web.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.consolefire.xpenser.expense.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.expense.data.dto.request.ReceiptUpdateRequestDto;
import com.consolefire.xpenser.expense.tc.TestContainerDatabaseInitializer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles({"ComponentTest", "TestContainerTests"})
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestContainerDatabaseInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReceiptUpdateApiControllerComponentTest {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE items RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE receipts RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE payments RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE tax_details RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE taxes RESTART IDENTITY CASCADE;");
    }

    // New tests for error scenarios

    @Test
    void testCreateReceipt_DuplicateEntry() {
        // First, create the initial receipt successfully
        ReceiptRequestDto requestDto = ReceiptRequestDto.builder()
            .identifier("12345")
            .marketPlaceCode("MP001")
            .storeCode("STORE001")
            .timestamp(Instant.now())
            .total(new BigDecimal("100.50"))
            .currency("USD")
            .metadata(Map.of("key1", "value1"))
            .items(Collections.emptyList())
            .payment(null)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(requestDto)
            .when()
            .post("/receipts")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        // Then, try to create it again and expect a 409 Conflict
        given()
            .contentType(ContentType.JSON)
            .body(requestDto)
            .when()
            .post("/receipts")
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("error", equalTo("Conflict"))
            .body("message", equalTo("A receipt with this identifier already exists."));
    }

    @Test
    void testUpdateReceipt_NotFound() {
        // Attempt to update a receipt that does not exist
        ReceiptUpdateRequestDto updateRequestDto = ReceiptUpdateRequestDto.builder()
            .metadata(Map.of("key2", "value2"))
            .total(new BigDecimal("150.75"))
            .currency("EUR")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(updateRequestDto)
            .when()
            .put("/receipts/{marketPlaceCode}/{storeCode}/{identifier}", "NON_EXISTENT_MP", "NON_EXISTENT_STORE",
                "NON_EXISTENT_ID")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("error", equalTo("Not Found"))
            .body("message", equalTo("No receipt found with this identifier to update."));
    }

    @Test
    void testCreateReceipt_UnsupportedMediaType() {
        // Send a request with an incorrect Content-Type (XML)
        String xmlBody = "<ReceiptRequestDto><identifier>bad-request-id</identifier></ReceiptRequestDto>";

        given()
            .contentType(ContentType.XML)
            .body(xmlBody)
            .when()
            .post("/receipts")
            .then()
            .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    void testCreateReceipt_InternalServerError() {
        // This test case is conceptual and requires a mocked service to simulate a 5xx error.
        // For a real component test, this scenario might be less common, as you are testing
        // the full stack and need to cause a real failure.
        // A simple way to simulate this is by passing invalid data that would cause a runtime exception
        // in your service layer (e.g., a database connection issue).
        // Since the current service logic doesn't have an explicit way to trigger a 500,
        // this is more of a blueprint for how you would test such a scenario.

        given()
            .contentType(ContentType.JSON)
            // A request body that would cause an unexpected error in the service layer
            .body("{\"identifier\":\"cause-500-error\"}")
            .when()
            .post("/receipts")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void testCreateReceipt() {
        ReceiptRequestDto requestDto = ReceiptRequestDto.builder()
            .identifier("12345")
            .marketPlaceCode("MP001")
            .storeCode("STORE001")
            .timestamp(Instant.now())
            .total(new BigDecimal("100.50"))
            .currency("USD")
            .metadata(Map.of("key1", "value1"))
            .items(Collections.emptyList())
            .payment(null)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(requestDto)
            .when()
            .post("/receipts")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("receiptId", notNullValue())
            .body("identifier", equalTo("12345"));
    }

    @Test
    void testUpdateReceipt() {
        // Create a receipt first to ensure a record exists for the update test
        ReceiptRequestDto createDto = ReceiptRequestDto.builder()
            .identifier("existing-id")
            .marketPlaceCode("MP001")
            .storeCode("STORE001")
            .timestamp(Instant.now())
            .total(new BigDecimal("100.00"))
            .currency("USD")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(createDto)
            .when()
            .post("/receipts")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        ReceiptUpdateRequestDto updateRequestDto = ReceiptUpdateRequestDto.builder()
            .metadata(Map.of("key2", "value2"))
            .total(new BigDecimal("150.75"))
            .currency("EUR")
            .build();

        String marketPlaceCode = "MP001";
        String storeCode = "STORE001";
        String identifier = "existing-id";

        given()
            .contentType(ContentType.JSON)
            .body(updateRequestDto)
            .when()
            .put("/receipts/{marketPlaceCode}/{storeCode}/{identifier}", marketPlaceCode, storeCode, identifier)
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .body("receiptId", notNullValue());
    }

}
