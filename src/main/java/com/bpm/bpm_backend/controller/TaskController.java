package com.bpm.bpm_backend.controller;

import com.bpm.bpm_backend.dto.TaskInstanceDTO;
import com.bpm.bpm_backend.dto.request.CompleteTaskRequest;
import com.bpm.bpm_backend.service.ProcessEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para gestión de tareas por parte de los trabajadores.
 * <p>
 * Ruta base: {@code /api/tasks}
 * <p>
 * Accesible para roles: TASK_WORKER, DEPT_MANAGER, PROCESS_ARCHITECT
 * (configurado en {@code SecurityConfig}).
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Engine", description = "Gestión de tareas del motor BPM")
public class TaskController {

    private final ProcessEngineService engineService;

    /**
     * GET /api/tasks/my-department
     * <p>
     * Devuelve las tareas PENDING cuyo departmentId coincida con el
     * departamento del usuario autenticado.
     */
    @GetMapping("/my-department")
    @Operation(summary = "Tareas de mi departamento",
               description = "Lista las tareas PENDING asignadas al departamento del usuario logueado.")
    public ResponseEntity<List<TaskInstanceDTO>> getTasksForMyDepartment() {
        return ResponseEntity.ok(engineService.getTasksForMyDepartment());
    }

    /**
     * GET /api/tasks/{id}
     * <p>
     * Devuelve los detalles de una TaskInstance por su ID (incluye el nodeId
     * necesario para que el frontend resuelva el formulario dinámico).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una tarea",
               description = "Devuelve los detalles completos de una TaskInstance, incluyendo el nodeId para resolver el formulario.")
    public ResponseEntity<TaskInstanceDTO> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(engineService.getTaskById(id));
    }

    /**
     * PATCH /api/tasks/{id}/claim
     * <p>
     * El trabajador se asigna la tarea a sí mismo, cambiando su estado
     * de PENDING a CLAIMED.
     */
    @PatchMapping("/{id}/claim")
    @Operation(summary = "Reclamar una tarea",
               description = "El usuario autenticado se asigna la tarea. Estado: PENDING → CLAIMED.")
    public ResponseEntity<TaskInstanceDTO> claimTask(@PathVariable String id) {
        return ResponseEntity.ok(engineService.claimTask(id));
    }

    /**
     * PATCH /api/tasks/{id}/complete
     * <p>
     * Marca la tarea como COMPLETED y dispara el motor para evaluar
     * el siguiente paso del proceso.
     * <p>
     * El body es opcional; útil para pasar {@code completedDate} artificial
     * cuando se alimenta el sistema desde un seeder, y {@code decision}
     * para nodos de tipo DECISION.
     */
    @PatchMapping("/{id}/complete")
    @Operation(summary = "Completar una tarea",
               description = "Marca la tarea como completada y avanza el proceso al siguiente nodo. " +
                             "El campo 'completedDate' permite cargar fechas artificiales para reportes.")
    public ResponseEntity<TaskInstanceDTO> completeTask(
            @PathVariable String id,
            @RequestBody(required = false) CompleteTaskRequest request) {

        String decision      = (request != null) ? request.decision()      : null;
        java.time.LocalDateTime completedDate = (request != null) ? request.completedDate() : null;

        return ResponseEntity.ok(engineService.completeTask(id, decision, completedDate));
    }
}
