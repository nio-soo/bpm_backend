package com.bpm.bpm_backend.service;

import com.bpm.bpm_backend.dto.WorkflowDefinitionDTO;
import com.bpm.bpm_backend.dto.request.WorkflowDefinitionRequest;
import com.bpm.bpm_backend.exception.ResourceNotFoundException;
import com.bpm.bpm_backend.model.WorkflowDefinition;
import com.bpm.bpm_backend.model.enums.NodeType;
import com.bpm.bpm_backend.model.enums.Role;
import com.bpm.bpm_backend.model.enums.WorkflowStatus;
import com.bpm.bpm_backend.repository.WorkflowDefinitionRepository;
import com.bpm.bpm_backend.repository.WorkflowNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowDefinitionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowNodeRepository workflowNodeRepository;

    // ── Mapping ──────────────────────────────────────────────────────────────

    private WorkflowDefinitionDTO toDTO(WorkflowDefinition w) {
        // <-- NUEVO: Se agregó w.getBpmnXml() al constructor del DTO
        return new WorkflowDefinitionDTO(w.getId(), w.getName(), w.getDescription(), w.getBpmnXml(),
                w.getAllowedRoles(), w.getStatus());
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public List<WorkflowDefinitionDTO> findAll() {
        return workflowDefinitionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public WorkflowDefinitionDTO findById(String id) {
        return toDTO(getOrThrow(id));
    }

    public WorkflowDefinitionDTO create(WorkflowDefinitionRequest request) {
        WorkflowDefinition wf = WorkflowDefinition.builder()
                .name(request.name())
                .description(request.description())
                .bpmnXml(request.bpmnXml()) // <-- NUEVO: Se agregó para que lo guarde al crear
                .allowedRoles(request.allowedRoles())
                .status(WorkflowStatus.DRAFT)
                .build();
        return toDTO(workflowDefinitionRepository.save(wf));
    }

    public WorkflowDefinitionDTO update(String id, WorkflowDefinitionRequest request) {
        WorkflowDefinition wf = getOrThrow(id);
        wf.setName(request.name());
        wf.setDescription(request.description());
        wf.setBpmnXml(request.bpmnXml()); // <-- NUEVO: Se agregó para que lo actualice
        wf.setAllowedRoles(request.allowedRoles());
        return toDTO(workflowDefinitionRepository.save(wf));
    }

    public void delete(String id) {
        getOrThrow(id);
        workflowDefinitionRepository.deleteById(id);
    }

    // ── Available for role ────────────────────────────────────────────────────

    public List<WorkflowDefinitionDTO> findAvailableForRole(Role role) {
        return workflowDefinitionRepository
                .findByStatusAndAllowedRolesContaining(WorkflowStatus.ACTIVE, role)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ── Publish ───────────────────────────────────────────────────────────────

    /**
     * Publica un workflow cambiando su estado de DRAFT → ACTIVE.
     * Valida que exista al menos 1 nodo START y 1 nodo END.
     */
    public WorkflowDefinitionDTO publish(String id) {
        WorkflowDefinition wf = getOrThrow(id);

        if (wf.getStatus() == WorkflowStatus.ACTIVE) {
            throw new IllegalStateException(
                    "El workflow '" + wf.getName() + "' ya se encuentra en estado ACTIVE.");
        }

        boolean hasStart = !workflowNodeRepository
                .findByWorkflowDefinitionIdAndType(id, NodeType.START).isEmpty();
        boolean hasEnd = !workflowNodeRepository
                .findByWorkflowDefinitionIdAndType(id, NodeType.END).isEmpty();

        if (!hasStart || !hasEnd) {
            throw new IllegalArgumentException(
                    "El workflow debe tener al menos un nodo de tipo START y un nodo de tipo END para ser publicado.");
        }

        wf.setStatus(WorkflowStatus.ACTIVE);
        return toDTO(workflowDefinitionRepository.save(wf));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    public WorkflowDefinition getOrThrow(String id) {
        return workflowDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WorkflowDefinition no encontrado con id: " + id));
    }
}