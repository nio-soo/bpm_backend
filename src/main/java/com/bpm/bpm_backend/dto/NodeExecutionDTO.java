package com.bpm.bpm_backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record NodeExecutionDTO(
    String id,
    String processInstanceId,
    String workflowNodeId,
    String executedById,
    Map<String, Object> formResponse,
    LocalDateTime executedAt
) {}
