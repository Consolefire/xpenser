package com.consolefire.xpenser.expenseimport.service.impl;

import com.consolefire.xpenser.expenseimport.api.model.CreateTemplateRequest;
import com.consolefire.xpenser.expenseimport.api.model.TemplateResponse;
import com.consolefire.xpenser.expenseimport.data.entity.Template;
import com.consolefire.xpenser.expenseimport.data.exception.DuplicateRecordFoundException;
import com.consolefire.xpenser.expenseimport.data.exception.RecordNotFoundException;
import com.consolefire.xpenser.expenseimport.data.mapper.TemplateMapper;
import com.consolefire.xpenser.expenseimport.data.repository.TemplateRepository;
import com.consolefire.xpenser.expenseimport.exception.UnsupportedTemplateFileException;
import com.consolefire.xpenser.expenseimport.service.TemplateService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Override
    @Transactional(readOnly = true)
    public TemplateResponse getTemplateByName(String name) {
        Template template = templateRepository.findByName(name)
            .orElseThrow(() -> new RecordNotFoundException("Template not found with name: " + name));

        return templateMapper.toResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateResponse> getAllTemplates() {
        List<Template> templates = templateRepository.findAll();
        return templateMapper.toResponseList(templates);
    }

    @Override
    @Transactional
    public TemplateResponse createTemplate(CreateTemplateRequest request) {
        // 1. Check for existing record with the same name
        if (templateRepository.existsByName(request.getName())) {
            throw new DuplicateRecordFoundException("Template already exists with name: " + request.getName());
        }

        // 2. Proceed with creation
        Template entity = templateMapper.toEntity(request);
        Template savedTemplate = templateRepository.save(entity);
        return templateMapper.toResponse(savedTemplate);
    }

    @Override
    @Transactional
    public TemplateResponse updateTemplateData(Long id, String newData) {
        Template template = templateRepository.findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Template not found with id: " + id));

        template.setData(newData);
        Template updatedTemplate = templateRepository.save(template);
        return templateMapper.toResponse(updatedTemplate);
    }

    @Override
    @Transactional
    public TemplateResponse updateTemplateFromFile(Long id, MultipartFile file) {
        Template template = templateRepository.findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Template not found with id: " + id));

        if (file.isEmpty()) {
            throw new UnsupportedTemplateFileException("Cannot update template: uploaded file is empty.");
        }

        try {
            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            template.setData(fileContent);

            Template updatedTemplate = templateRepository.save(template);
            return templateMapper.toResponse(updatedTemplate);
        } catch (IOException e) {
            throw new UnsupportedTemplateFileException("Failed to read uploaded file content for template update.", e);
        }
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RecordNotFoundException("Template not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }
}