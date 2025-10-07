package com.consolefire.xpenser.expenseimport.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.consolefire.xpenser.expenseimport.api.model.CreateTemplateRequest;
import com.consolefire.xpenser.expenseimport.data.entity.Template;
import com.consolefire.xpenser.expenseimport.data.repository.TemplateRepository;
import com.consolefire.xpenser.expenseimport.tc.TestContainerDatabaseInitializer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles({"ComponentTests", "TestContainerTests"})
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestContainerDatabaseInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TemplateApiIntegrationTest {

    private static final String BASE_PATH = "/templates";
    private static final String TEMPLATE_NAME_1 = "invoice-template-A";
    private static final String TEMPLATE_NAME_2 = "receipt-template-B";
    private static final String INITIAL_DATA = "Initial template data";
    private static final String NEW_DATA = "Updated template data";

    @LocalServerPort
    private int port;

    // Inject the real repository to manage test data directly in the database
    @Autowired
    private TemplateRepository templateRepository;

    private Long existingTemplateId;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        // Setup: Create a template directly in the database for tests that require an existing record
        Template existingTemplate = Template.builder()
            .name(TEMPLATE_NAME_1)
            .data(INITIAL_DATA)
            .build();
        existingTemplate = templateRepository.saveAndFlush(existingTemplate);
        existingTemplateId = existingTemplate.getId();
    }

    // Clean up any templates that might have been created but not cleaned up (though @Transactional should handle most)
    @AfterEach
    public void tearDown() {
        // Since @Transactional is used, an explicit clean up isn't strictly necessary
        // but can be added for robustness in certain configurations.
        // For this setup, we rely on the transaction rollback.
        templateRepository.deleteAll();
    }

    // --- GET Endpoint Tests (/templates/{name}) ---
    @Test
    void getTemplateByName_shouldReturn200AndTemplate() {
        given()
            .when()
            .get(BASE_PATH + "/{name}", TEMPLATE_NAME_1)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(existingTemplateId.intValue()))
            .body("name", equalTo(TEMPLATE_NAME_1))
            .body("data", equalTo(INITIAL_DATA));
    }

    @Test
    void getTemplateByName_shouldReturn404OnNotFound() {
        given()
            .when()
            .get(BASE_PATH + "/{name}", "non-existent-name")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value()) // Mapped by DefaultRestErrorHandler
            .body(startsWith("Template not found with name:"));
    }

    // --- GET All Endpoint Tests (/templates/) ---
    @Test
    void getAllTemplates_shouldReturn200AndList() {
        // Create a second template to ensure two are returned
        Template secondTemplate = Template.builder().name(TEMPLATE_NAME_2).data(INITIAL_DATA).build();
        templateRepository.saveAndFlush(secondTemplate);

        given()
            .when()
            .get(BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(2))
            // Check if both names are present
            .body("name", hasItems(TEMPLATE_NAME_1, TEMPLATE_NAME_2));
    }

    // --- POST Endpoint Tests (/templates/) ---
    @Test
    void createTemplate_shouldReturn201OnSuccess() {
        CreateTemplateRequest request = new CreateTemplateRequest();
        request.setName("new-valid-template");
        request.setTemplate(NEW_DATA);

        // Assert the template doesn't exist before creation
        assertFalse(templateRepository.existsByName("new-valid-template"));

        Integer createdId = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo("new-valid-template"))
            .extract().path("id");

        // Assert the template now exists in the database
        Optional<Template> createdEntity = templateRepository.findById(createdId.longValue());
        assertTrue(createdEntity.isPresent());
        assertTrue(createdEntity.get().getData().equals(NEW_DATA));
    }

    @Test
    void createTemplate_shouldReturn409OnDuplicateName() {
        CreateTemplateRequest request = new CreateTemplateRequest();
        request.setName(TEMPLATE_NAME_1); // Use the name of the template created in @BeforeEach
        request.setTemplate(NEW_DATA);

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(HttpStatus.CONFLICT.value()) // Mapped by DefaultRestErrorHandler
            .body(startsWith("Template already exists with name:"));
    }

    // --- POST Validation Tests (@Valid mapped to 406 Not Acceptable) ---
    @Test
    void createTemplate_shouldReturn406OnValidationFailure() {
        CreateTemplateRequest invalidRequest = new CreateTemplateRequest();
        invalidRequest.setName("a"); // Too short
        invalidRequest.setTemplate(" "); // Blank

        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(HttpStatus.NOT_ACCEPTABLE.value()) // Mapped by DefaultRestErrorHandler
            .body("name", containsString("between 3 and 120 characters"))
            .body("template", containsString("Template content must not be blank."));
    }

    // --- PUT Data Update Tests (/templates/{id}) ---
    @Test
    void updateTemplateData_shouldReturn200() {
        given()
            .contentType(ContentType.TEXT)
            .body(NEW_DATA)
            .when()
            .put(BASE_PATH + "/{id}", existingTemplateId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data", equalTo(NEW_DATA));

        // Assert the change was persisted in the database
        Template updatedEntity = templateRepository.findById(existingTemplateId).orElseThrow();
        assertTrue(updatedEntity.getData().equals(NEW_DATA));
    }

    @Test
    void updateTemplateData_shouldReturn404OnNotFound() {
        given()
            .contentType(ContentType.TEXT)
            .body(NEW_DATA)
            .when()
            .put(BASE_PATH + "/{id}", 9999L)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    // --- PUT File Update Tests (/templates/{id}/file) ---
    @Test
    void uploadTemplateFile_shouldReturn200OnSuccess() throws IOException {
        Path tempFile = Files.createTempFile("test-file", ".txt");
        Files.writeString(tempFile, NEW_DATA);
        File fileToUpload = tempFile.toFile();

        try {
            given()
                .multiPart("file", fileToUpload, MediaType.TEXT_PLAIN_VALUE)
                .when()
                .put(BASE_PATH + "/{id}/file", existingTemplateId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data", equalTo(NEW_DATA));

            // Assert the change was persisted in the database
            Template updatedEntity = templateRepository.findById(existingTemplateId).orElseThrow();
            assertTrue(updatedEntity.getData().equals(NEW_DATA));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void uploadTemplateFile_shouldReturn400OnEmptyFile() throws IOException {
        Path emptyFile = Files.createTempFile("empty-file", ".txt");
        File fileToUpload = emptyFile.toFile();

        try {
            given()
                .multiPart("file", fileToUpload, MediaType.TEXT_PLAIN_VALUE)
                .when()
                .put(BASE_PATH + "/{id}/file", existingTemplateId)
                .then()
                .statusCode(
                    HttpStatus.BAD_REQUEST.value()) // Mapped by DefaultRestErrorHandler as UnsupportedTemplateFileException
                .body(equalTo("Cannot update template: uploaded file is empty."));
        } finally {
            Files.deleteIfExists(emptyFile);
        }
    }

    @Test
    void uploadTemplateFile_shouldReturn404OnNotFound() throws IOException {
        Path tempFile = Files.createTempFile("test-file", ".txt");
        Files.writeString(tempFile, "Content");
        File fileToUpload = tempFile.toFile();

        try {
            given()
                .multiPart("file", fileToUpload, MediaType.TEXT_PLAIN_VALUE)
                .when()
                .put(BASE_PATH + "/{id}/file", 9999L)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    // --- DELETE Endpoint Tests (/templates/{id}) ---
    @Test
    void deleteTemplate_shouldReturn204OnSuccess() {
        // Assert it exists before deletion
        assertTrue(templateRepository.existsById(existingTemplateId));

        given()
            .when()
            .delete(BASE_PATH + "/{id}", existingTemplateId)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        // Assert it no longer exists in the database
        assertFalse(templateRepository.existsById(existingTemplateId));
    }

    @Test
    void deleteTemplate_shouldReturn404OnNotFound() {
        given()
            .when()
            .delete(BASE_PATH + "/{id}", 9999L)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body(startsWith("Template not found with id:"));
    }
}