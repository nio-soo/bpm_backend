package com.bpm.bpm_backend.dto;

public record WorkflowTransitionDTO(
    String id,
    String workflowDefinitionId,
    String fromNodeId,
    String toNodeId,
    String condition
) {}
