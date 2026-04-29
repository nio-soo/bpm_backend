package com.bpm.bpm_backend.controller;

import com.bpm.bpm_backend.model.Department;
import com.bpm.bpm_backend.model.User;
import com.bpm.bpm_backend.model.enums.Role;
import com.bpm.bpm_backend.repository.AiAnalysisRepository;
import com.bpm.bpm_backend.repository.DepartmentRepository;
import com.bpm.bpm_backend.repository.DynamicFormRepository;
import com.bpm.bpm_backend.repository.NodeExecutionRepository;
import com.bpm.bpm_backend.repository.NotificationRepository;
import com.bpm.bpm_backend.repository.ProcessInstanceRepository;
import com.bpm.bpm_backend.repository.TaskInstanceRepository;
import com.bpm.bpm_backend.repository.UserRepository;
import com.bpm.bpm_backend.repository.WorkflowDefinitionRepository;
import com.bpm.bpm_backend.repository.WorkflowNodeRepository;
import com.bpm.bpm_backend.repository.WorkflowTransitionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dev")
@RequiredArgsConstructor
@SecurityRequirements   // anula el bearerAuth global: estos endpoints no requieren JWT en Swagger
@Tag(name = "Dev Utils", description = "Reset y carga de datos para defensa del proyecto — accesible sin autenticación")
public class DatabaseDevController {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final DynamicFormRepository        dynamicFormRepository;
    private final ProcessInstanceRepository    processInstanceRepository;
    private final TaskInstanceRepository       taskInstanceRepository;
    private final DepartmentRepository         departmentRepository;
    private final UserRepository               userRepository;
    private final WorkflowNodeRepository       workflowNodeRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final AiAnalysisRepository         aiAnalysisRepository;
    private final NodeExecutionRepository      nodeExecutionRepository;
    private final NotificationRepository       notificationRepository;
    private final PasswordEncoder              passwordEncoder;

    private static final String PASSWORD = "12345678";

    // ─────────────────────────────────────────────────────────────
    //  DELETE /api/admin/dev/wipe
    // ─────────────────────────────────────────────────────────────

    @DeleteMapping("/wipe")
    @Operation(
        summary     = "Limpiar base de datos",
        description = "Elimina **todos** los documentos de todas las colecciones. " +
                      "Llámalo antes de /seed para partir de cero."
    )
    public ResponseEntity<Map<String, Object>> wipe() {
        // Orden: hijos primero para respetar dependencias lógicas
        notificationRepository.deleteAll();
        nodeExecutionRepository.deleteAll();
        aiAnalysisRepository.deleteAll();
        taskInstanceRepository.deleteAll();
        processInstanceRepository.deleteAll();
        dynamicFormRepository.deleteAll();
        workflowTransitionRepository.deleteAll();
        workflowNodeRepository.deleteAll();
        workflowDefinitionRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();

        return ResponseEntity.ok(Map.of(
                "status",    "wiped",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ─────────────────────────────────────────────────────────────
    //  POST /api/admin/dev/seed
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/seed")
    @Operation(
        summary     = "Poblar base de datos",
        description = "Crea 5 departamentos y 21 usuarios con contraseña `12345678`:<br>" +
                      "• 1 PROCESS_ARCHITECT — user@example.com (sin departamento)<br>" +
                      "• 5 DEPT_MANAGER — user1…user5 (uno por departamento)<br>" +
                      "• 10 TASK_WORKER — user10…user19 (2 por departamento)<br>" +
                      "• 5 REQUESTER — user100…user104 (sin departamento)<br>" +
                      "Se recomienda ejecutar /wipe antes."
    )
    public ResponseEntity<Map<String, Object>> seed() {

        // ── Departamentos ──────────────────────────────────────────
        List<Department> savedDepts = departmentRepository.saveAll(List.of(
                Department.builder().name("Sistemas").build(),
                Department.builder().name("Recursos Humanos").build(),
                Department.builder().name("Contabilidad").build(),
                Department.builder().name("Marketing").build(),
                Department.builder().name("Ventas").build()
        ));

        String        encoded = passwordEncoder.encode(PASSWORD);
        LocalDateTime now     = LocalDateTime.now();
        List<User>    users   = new ArrayList<>();

        // ── 1 PROCESS_ARCHITECT ────────────────────────────────────
        users.add(User.builder()
                .email("user@example.com")
                .password(encoded)
                .role(Role.PROCESS_ARCHITECT)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build());

        // ── 5 DEPT_MANAGER — user1…user5 (uno por departamento) ───
        for (int i = 0; i < 5; i++) {
            users.add(User.builder()
                    .email("user" + (i + 1) + "@example.com")
                    .password(encoded)
                    .role(Role.DEPT_MANAGER)
                    .departmentId(savedDepts.get(i).getId())
                    .isActive(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }

        // ── 10 TASK_WORKER — user10…user19 (2 por departamento) ───
        for (int i = 0; i < 10; i++) {
            users.add(User.builder()
                    .email("user" + (i + 10) + "@example.com")
                    .password(encoded)
                    .role(Role.TASK_WORKER)
                    .departmentId(savedDepts.get(i / 2).getId())
                    .isActive(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }

        // ── 5 REQUESTER — user100…user104 (sin departamento) ──────
        for (int i = 0; i < 5; i++) {
            users.add(User.builder()
                    .email("user" + (i + 100) + "@example.com")
                    .password(encoded)
                    .role(Role.REQUESTER)
                    .isActive(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }

        List<User> savedUsers = userRepository.saveAll(users);

        return ResponseEntity.ok(Map.of(
                "status",      "seeded",
                "departments", savedDepts.size(),
                "users",       savedUsers.size(),
                "summary", Map.of(
                        "PROCESS_ARCHITECT", 1,
                        "DEPT_MANAGER",      5,
                        "TASK_WORKER",       10,
                        "REQUESTER",         5
                )
        ));
    }
}
