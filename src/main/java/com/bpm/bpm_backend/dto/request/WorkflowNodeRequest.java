package com.bpm.bpm_backend.dto.request;

import com.bpm.bpm_backend.model.enums.EndStatus;
import com.bpm.bpm_backend.model.enums.NodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkflowNodeRequest(
        @NotBlank(message = "El nombre del nodo es obligatorio") String name,

        @NotNull(message = "El tipo de nodo es obligatorio") NodeType type,

        EndStatus endStatus,

        String departmentId) {
}