package com.bpm.bpm_backend.model.enums;

/**
 * Estado final que un nodo END puede conferir al proceso.
 * Se mapea a {@link ProcessStatus} en el motor de ejecución.
 */
public enum EndStatus {
    APPROVED,
    REJECTED,
    FILED,
    COMPLETED
}
