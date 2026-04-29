package com.bpm.bpm_backend.controller;

import com.bpm.bpm_backend.dto.ProcessInstanceDTO;
import com.bpm.bpm_backend.dto.WorkflowDefinitionDTO;
import com.bpm.bpm_backend.dto.request.StartProcessRequest;
import com.bpm.bpm_backend.model.enums.Role;
import com.bpm.bpm_backend.security.SecurityUtils;
import com.bpm.bpm_backend.service.ProcessEngineService;
import com.bpm.bpm_backend.service.WorkflowDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para iniciar y hacer seguimiento de procesos BPM.
 * <p>
 * Ruta base: {@code /api/processes}
 * <p>
 * El {@code requesterId} se extrae automáticamente del usuario autenticado
 * en el {@code SecurityContextHolder}; nunca viene en el cuerpo.
 */
@RestController
@RequestMapping("/api/processes")
@RequiredArgsConstructor
@Tag(name = "Process Engine", description = "Motor de ejecución de procesos BPM")
public class ProcessController {

    private final ProcessEngineService engineService;
    private final WorkflowDefinitionService workflowDefinitionService;
    private final SecurityUtils securityUtils;

    /**
     * POST /api/processes/start/{workflowId}
     * <p>
     * Inicia un nuevo proceso. Abierto a cualquier usuario autenticado cuyo rol
     * esté en la lista {@code allowedRoles} del workflow.
     * <p>
     * El body es opcional; útil para pasar {@code variables} y {@code startTime}
     * artificial cuando se alimenta el sistema desde un seeder.
     */
    @PostMapping("/start/{workflowId}")
    @Operation(summary = "Iniciar un proceso",
               description = "Crea una nueva instancia de proceso y la primera tarea. " +
                             "El campo 'startTime' es opcional y permite cargar fechas artificiales para reportes.")
    public ResponseEntity<ProcessInstanceDTO> startProcess(
            @PathVariable String workflowId,
            @RequestBody(required = false) StartProcessRequest request) {

        ProcessInstanceDTO result = engineService.startProcess(workflowId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * GET /api/processes/available-workflows
     * <p>
     * Devuelve los workflows activos que el usuario autenticado puede iniciar
     * según su rol.
     */
    @GetMapping("/available-workflows")
    @Operation(summary = "Flujos disponibles",
               description = "Lista los workflows en estado ACTIVE cuyo arreglo allowedRoles contiene el rol del usuario autenticado.")
    public ResponseEntity<List<WorkflowDefinitionDTO>> getAvailableWorkflows() {
        Role role = securityUtils.getCurrentUser().getRole();
        return ResponseEntity.ok(workflowDefinitionService.findAvailableForRole(role));
    }

    /**
     * GET /api/processes/me
     * <p>
     * Devuelve todos los procesos iniciados por el usuario autenticado.
     */
    @GetMapping("/me")
    @Operation(summary = "Mis procesos",
               description = "Lista todos los procesos iniciados por el usuario actual para su seguimiento personal.")
    public ResponseEntity<List<ProcessInstanceDTO>> getMyProcesses() {
        return ResponseEntity.ok(engineService.getMyProcesses());
    }
}
