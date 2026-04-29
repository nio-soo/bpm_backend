package com.bpm.bpm_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import com.bpm.bpm_backend.model.enums.Role;
import java.util.List;

public record WorkflowDefinitionRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        String description,
        List<Role> allowedRoles,
        String bpmnXml) {
}
