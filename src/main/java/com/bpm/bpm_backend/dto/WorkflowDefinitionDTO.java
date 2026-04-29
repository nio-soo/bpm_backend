package com.bpm.bpm_backend.dto;

import com.bpm.bpm_backend.model.enums.WorkflowStatus;
import com.bpm.bpm_backend.model.enums.Role;
import java.util.List;

public record WorkflowDefinitionDTO(
        String id,
        String name,
        String description,
        String bpmnXml,
        List<Role> allowedRoles,
        WorkflowStatus status) {
}
