package com.bpm.bpm_backend.dto;

import com.bpm.bpm_backend.model.enums.EndStatus;
import com.bpm.bpm_backend.model.enums.NodeType;

public record WorkflowNodeDTO(
        String id,
        String workflowDefinitionId,
        String name,
        NodeType type,
        EndStatus endStatus,
        String departmentId) {
}
