package com.bpm.bpm_backend.dto;

import com.bpm.bpm_backend.model.enums.Role;

public record RegisterRequestDTO(String email, String password, Role role, String departmentId) {}
