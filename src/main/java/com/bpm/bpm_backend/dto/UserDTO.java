package com.bpm.bpm_backend.dto;

import com.bpm.bpm_backend.model.enums.Role;
import java.time.LocalDateTime;

public record UserDTO(
    String id,
    String email,
    String departmentId,
    boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Role role
) {}
