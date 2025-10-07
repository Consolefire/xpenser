package com.consolefire.xpenser.expenseimport.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTemplateRequest {

    public static final String EXPRESSION = "^[a-zA-Z](?:[a-zA-Z0-9]+(?:[_.-][a-zA-Z0-9]+)*)?$";

    @NotBlank(message = "Name must not be blank.")
    @Size(min = 3, max = 120, message = "Name must be between 3 and 120 characters.")
//    @Pattern(regexp = EXPRESSION, message = "Name must be alphanumeric and can only contain underscores (_) or hyphens (-) or dot (.) but not in consecutive.")
    private String name;

    @NotBlank(message = "Template content must not be blank.")
    private String template;


}
