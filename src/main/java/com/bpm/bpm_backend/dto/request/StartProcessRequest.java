package com.bpm.bpm_backend.dto.request;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Request body para iniciar un proceso.
 * <p>
 * El campo {@code startTime} es <b>opcional</b>: si se envía, se usará como
 * fecha de inicio artificial (útil para seeders / carga de datos históricos).
 * Si no se envía, se usará {@link LocalDateTime#now()}.
 * <p>
 * El {@code requesterId} NO viene en este DTO; se obtiene del
 * {@code SecurityContextHolder}.
 */
public record StartProcessRequest(
    Map<String, Object> variables,
    LocalDateTime startTime     // nullable — solo para seeder
) {}
