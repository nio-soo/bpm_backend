package com.bpm.bpm_backend.dto.request;

import com.bpm.bpm_backend.model.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
    @NotNull(message = "El rol es obligatorio")
    Role role,
    String departmentId
) {}
