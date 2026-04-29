package com.bpm.bpm_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record DynamicFormRequest(
    @NotBlank(message = "El workflowNodeId es obligatorio")
    String workflowNodeId,
    @NotNull(message = "El schema del formulario es obligatorio")
    Map<String, Object> formSchema
) {}
