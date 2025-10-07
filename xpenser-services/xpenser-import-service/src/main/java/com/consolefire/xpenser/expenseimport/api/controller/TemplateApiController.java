package com.consolefire.xpenser.expenseimport.api.controller;

import com.consolefire.xpenser.expenseimport.api.model.CreateTemplateRequest;
import com.consolefire.xpenser.expenseimport.api.model.TemplateResponse;
import com.consolefire.xpenser.expenseimport.service.TemplateService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/templates")
public class TemplateApiController {

    private final TemplateService templateService;

    public TemplateApiController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<TemplateResponse> getTemplateByName(@PathVariable String name) {
        TemplateResponse response = templateService.getTemplateByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        List<TemplateResponse> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(@Valid @RequestBody CreateTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request));
    }

    @PutMapping("/{id}")
    public TemplateResponse updateTemplate(@PathVariable Long id, @RequestBody String newTemplateData) {
        return templateService.updateTemplateData(id, newTemplateData);
    }

    @PutMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TemplateResponse uploadTemplateFile(
        @PathVariable Long id,
        @RequestPart("file") MultipartFile file) {
        return templateService.updateTemplateFromFile(id, file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
    }
}