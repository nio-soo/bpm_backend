package com.bpm.bpm_backend.controller;

import com.bpm.bpm_backend.dto.WorkflowDefinitionDTO;
import com.bpm.bpm_backend.dto.WorkflowNodeDTO;
import com.bpm.bpm_backend.dto.WorkflowTransitionDTO;
import com.bpm.bpm_backend.dto.request.WorkflowDefinitionRequest;
import com.bpm.bpm_backend.dto.request.WorkflowNodeRequest;
import com.bpm.bpm_backend.dto.request.WorkflowTransitionRequest;
import com.bpm.bpm_backend.service.WorkflowDefinitionService;
import com.bpm.bpm_backend.service.WorkflowNodeService;
import com.bpm.bpm_backend.service.WorkflowTransitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de WorkflowDefinitions, Nodes y Transitions.
 * Acceso restringido a PROCESS_ARCHITECT (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/api/admin/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowDefinitionService workflowDefinitionService;
    private final WorkflowNodeService workflowNodeService;
    private final WorkflowTransitionService workflowTransitionService;

    // ══════════════════════════════════════════════════════════════════════════
    // WorkflowDefinition CRUD
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping
    public ResponseEntity<List<WorkflowDefinitionDTO>> getAllWorkflows() {
        return ResponseEntity.ok(workflowDefinitionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDefinitionDTO> getWorkflowById(@PathVariable String id) {
        return ResponseEntity.ok(workflowDefinitionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<WorkflowDefinitionDTO> createWorkflow(
            @Valid @RequestBody WorkflowDefinitionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workflowDefinitionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDefinitionDTO> updateWorkflow(
            @PathVariable String id,
            @Valid @RequestBody WorkflowDefinitionRequest request) {
        return ResponseEntity.ok(workflowDefinitionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable String id) {
        workflowDefinitionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Publish
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Publica un workflow: DRAFT → ACTIVE.
     * Valida nodos START y END. Requiere PROCESS_ARCHITECT.
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<WorkflowDefinitionDTO> publishWorkflow(@PathVariable String id) {
        return ResponseEntity.ok(workflowDefinitionService.publish(id));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // WorkflowNode CRUD
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/{workflowId}/nodes")
    public ResponseEntity<List<WorkflowNodeDTO>> getNodes(@PathVariable String workflowId) {
        return ResponseEntity.ok(workflowNodeService.findByWorkflow(workflowId));
    }

    @GetMapping("/{workflowId}/nodes/{nodeId}")
    public ResponseEntity<WorkflowNodeDTO> getNodeById(
            @PathVariable String workflowId, @PathVariable String nodeId) {
        return ResponseEntity.ok(workflowNodeService.findById(workflowId, nodeId));
    }

    @PostMapping("/{workflowId}/nodes")
    public ResponseEntity<WorkflowNodeDTO> createNode(
            @PathVariable String workflowId,
            @Valid @RequestBody WorkflowNodeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workflowNodeService.create(workflowId, request));
    }

    /**
     * Actualizar nodo — lanza 409 si el workflow está ACTIVE.
     */
    @PutMapping("/{workflowId}/nodes/{nodeId}")
    public ResponseEntity<WorkflowNodeDTO> updateNode(
            @PathVariable String workflowId,
            @PathVariable String nodeId,
            @Valid @RequestBody WorkflowNodeRequest request) {
        return ResponseEntity.ok(workflowNodeService.update(workflowId, nodeId, request));
    }

    @DeleteMapping("/{workflowId}/nodes/{nodeId}")
    public ResponseEntity<Void> deleteNode(
            @PathVariable String workflowId, @PathVariable String nodeId) {
        workflowNodeService.delete(workflowId, nodeId);
        return ResponseEntity.noContent().build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // WorkflowTransition CRUD
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/{workflowId}/transitions")
    public ResponseEntity<List<WorkflowTransitionDTO>> getTransitions(
            @PathVariable String workflowId) {
        return ResponseEntity.ok(workflowTransitionService.findByWorkflow(workflowId));
    }

    @GetMapping("/{workflowId}/transitions/{transitionId}")
    public ResponseEntity<WorkflowTransitionDTO> getTransitionById(
            @PathVariable String workflowId, @PathVariable String transitionId) {
        return ResponseEntity.ok(workflowTransitionService.findById(workflowId, transitionId));
    }

    @PostMapping("/{workflowId}/transitions")
    public ResponseEntity<WorkflowTransitionDTO> createTransition(
            @PathVariable String workflowId,
            @Valid @RequestBody WorkflowTransitionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workflowTransitionService.create(workflowId, request));
    }

    /**
     * Actualizar transición — lanza 409 si el workflow está ACTIVE.
     */
    @PutMapping("/{workflowId}/transitions/{transitionId}")
    public ResponseEntity<WorkflowTransitionDTO> updateTransition(
            @PathVariable String workflowId,
            @PathVariable String transitionId,
            @Valid @RequestBody WorkflowTransitionRequest request) {
        return ResponseEntity.ok(
                workflowTransitionService.update(workflowId, transitionId, request));
    }

    @DeleteMapping("/{workflowId}/transitions/{transitionId}")
    public ResponseEntity<Void> deleteTransition(
            @PathVariable String workflowId, @PathVariable String transitionId) {
        workflowTransitionService.delete(workflowId, transitionId);
        return ResponseEntity.noContent().build();
    }
}
