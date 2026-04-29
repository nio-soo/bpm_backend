package com.bpm.bpm_backend.dto;

import com.bpm.bpm_backend.model.enums.ProcessStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ProcessInstanceDTO(
    String id,
    String workflowDefinitionId,
    String requesterId,
    ProcessStatus status,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Map<String, Object> variables
) {}
