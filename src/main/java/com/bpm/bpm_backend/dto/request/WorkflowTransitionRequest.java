package com.bpm.bpm_backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WorkflowTransitionRequest(
    @NotBlank(message = "El nodo origen es obligatorio")
    String fromNodeId,
    @NotBlank(message = "El nodo destino es obligatorio")
    String toNodeId,
    String condition
) {}
