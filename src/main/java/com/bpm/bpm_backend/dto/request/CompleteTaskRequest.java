package com.bpm.bpm_backend.dto.request;

import java.time.LocalDateTime;

/**
 * Request body para completar una tarea.
 * <p>
 * El campo {@code completedDate} es <b>opcional</b>: si se envía, se usará
 * como fecha de completado artificial (útil para seeders / datos históricos).
 */
public record CompleteTaskRequest(
    String decision,            // nullable — valor de condición para DECISION nodes
    LocalDateTime completedDate // nullable — solo para seeder
) {}
