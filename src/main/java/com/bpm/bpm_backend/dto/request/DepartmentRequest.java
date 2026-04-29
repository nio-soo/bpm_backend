package com.bpm.bpm_backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
    @NotBlank(message = "El nombre del departamento es obligatorio")
    String name
) {}
