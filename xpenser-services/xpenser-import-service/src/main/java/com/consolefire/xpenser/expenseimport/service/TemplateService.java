package com.consolefire.xpenser.expenseimport.service;

import com.consolefire.xpenser.expenseimport.api.model.CreateTemplateRequest;
import com.consolefire.xpenser.expenseimport.api.model.TemplateResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TemplateService {

    TemplateResponse getTemplateByName(String name);

    List<TemplateResponse> getAllTemplates();

    TemplateResponse createTemplate(CreateTemplateRequest request);

    TemplateResponse updateTemplateData(Long id, String newData);

    TemplateResponse updateTemplateFromFile(Long id, MultipartFile file);

    void deleteTemplate(Long id);

}
