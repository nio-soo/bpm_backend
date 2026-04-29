package com.bpm.bpm_backend.dto;

import com.bpm.bpm_backend.model.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskInstanceDTO(
    String id,
    String processInstanceId,
    String nodeId,
    String departmentId,
    String assignedUserId,
    TaskStatus status,
    LocalDateTime createdDate,
    LocalDateTime claimedDate,
    LocalDateTime completedDate
) {}
