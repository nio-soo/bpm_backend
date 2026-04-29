package com.bpm.bpm_backend.controller;

import com.bpm.bpm_backend.model.Department;
import com.bpm.bpm_backend.repository.DepartmentRepository;
import com.bpm.bpm_backend.repository.DynamicFormRepository;
import com.bpm.bpm_backend.repository.ProcessInstanceRepository;
import com.bpm.bpm_backend.repository.TaskInstanceRepository;
import com.bpm.bpm_backend.repository.WorkflowDefinitionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Utilería de desarrollo — NUNCA desplegar en producción.
 * Activo solo con los perfiles "dev" o "local".
 */
@Profile({"dev", "local"})
@RestController
@RequestMapping("/api/admin/dev")
@RequiredArgsConstructor
@Tag(name = "Dev Utils", description = "Herramientas de desarrollo: wipe y seed de base de datos")
public class DatabaseDevController {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final DynamicFormRepository        dynamicFormRepository;
    private final ProcessInstanceRepository    processInstanceRepository;
    private final TaskInstanceRepository       taskInstanceRepository;
    private final DepartmentRepository         departmentRepository;

    private static final List<String> SEED_DEPARTMENTS = List.of(
            "Sistemas", "Recursos Humanos", "Contabilidad"
    );

    /**
     * DELETE /api/admin/dev/wipe
     * Borra todos los documentos de las colecciones principales.
     */
    @DeleteMapping("/wipe")
    @Operation(summary = "Limpiar base de datos",
               description = "Elimina todos los documentos de workflows, formularios, procesos y tareas.")
    public ResponseEntity<Map<String, String>> wipe() {
        workflowDefinitionRepository.deleteAll();
        dynamicFormRepository.deleteAll();
        processInstanceRepository.deleteAll();
        taskInstanceRepository.deleteAll();
        return ResponseEntity.ok(Map.of("status", "wiped"));
    }

    /**
     * POST /api/admin/dev/seed-departments
     * Inserta los departamentos maestros si todavía no existen.
     */
    @PostMapping("/seed-departments")
    @Operation(summary = "Poblar departamentos",
               description = "Crea los departamentos base (Sistemas, RRHH, Contabilidad) si no existen.")
    public ResponseEntity<Map<String, Object>> seedDepartments() {
        List<String> created = SEED_DEPARTMENTS.stream()
                .filter(name -> !departmentRepository.existsByName(name))
                .map(name -> departmentRepository.save(
                        Department.builder().name(name).build()).getName())
                .toList();

        return ResponseEntity.ok(Map.of(
                "created", created,
                "skipped", SEED_DEPARTMENTS.size() - created.size()
        ));
    }
}
