package com.consolefire.xpenser.expenseimport.api.model;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TemplateResponse {

    private Long id;
    private String name;
    private String template;
    private String data;
    private Instant createdAt;
    private Instant updatedAt;

}
