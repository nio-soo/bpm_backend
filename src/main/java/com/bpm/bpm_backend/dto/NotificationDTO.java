package com.bpm.bpm_backend.dto;

import java.time.LocalDateTime;

public record NotificationDTO(
    String id,
    String userId,
    String message,
    boolean isRead,
    LocalDateTime createdAt
) {}
