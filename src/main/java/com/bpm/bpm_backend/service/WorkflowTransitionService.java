package com.bpm.bpm_backend.service;

import com.bpm.bpm_backend.dto.WorkflowTransitionDTO;
import com.bpm.bpm_backend.dto.request.WorkflowTransitionRequest;
import com.bpm.bpm_backend.exception.ResourceNotFoundException;
import com.bpm.bpm_backend.model.WorkflowDefinition;
import com.bpm.bpm_backend.model.WorkflowTransition;
import com.bpm.bpm_backend.model.enums.WorkflowStatus;
import com.bpm.bpm_backend.repository.WorkflowTransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowTransitionService {

    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final WorkflowDefinitionService workflowDefinitionService;

    // ── Mapping ──────────────────────────────────────────────────────────────

    private WorkflowTransitionDTO toDTO(WorkflowTransition t) {
        return new WorkflowTransitionDTO(
                t.getId(), t.getWorkflowDefinitionId(),
                t.getFromNodeId(), t.getToNodeId(), t.getCondition());
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public List<WorkflowTransitionDTO> findByWorkflow(String workflowId) {
        workflowDefinitionService.getOrThrow(workflowId);
        return workflowTransitionRepository.findByWorkflowDefinitionId(workflowId)
                .stream().map(this::toDTO).toList();
    }

    public WorkflowTransitionDTO findById(String workflowId, String transitionId) {
        return toDTO(getTransitionOrThrow(workflowId, transitionId));
    }

    public WorkflowTransitionDTO create(String workflowId, WorkflowTransitionRequest request) {
        workflowDefinitionService.getOrThrow(workflowId);
        WorkflowTransition transition = WorkflowTransition.builder()
                .workflowDefinitionId(workflowId)
                .fromNodeId(request.fromNodeId())
                .toNodeId(request.toNodeId())
                .condition(request.condition())
                .build();
        return toDTO(workflowTransitionRepository.save(transition));
    }

    /**
     * Actualización protegida: lanza {@link IllegalStateException} si el workflow está ACTIVE.
     */
    public WorkflowTransitionDTO update(String workflowId, String transitionId,
                                        WorkflowTransitionRequest request) {
        WorkflowDefinition wf = workflowDefinitionService.getOrThrow(workflowId);
        guardActiveWorkflow(wf);

        WorkflowTransition transition = getTransitionOrThrow(workflowId, transitionId);
        transition.setFromNodeId(request.fromNodeId());
        transition.setToNodeId(request.toNodeId());
        transition.setCondition(request.condition());
        return toDTO(workflowTransitionRepository.save(transition));
    }

    public void delete(String workflowId, String transitionId) {
        getTransitionOrThrow(workflowId, transitionId);
        workflowTransitionRepository.deleteById(transitionId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private WorkflowTransition getTransitionOrThrow(String workflowId, String transitionId) {
        WorkflowTransition t = workflowTransitionRepository.findById(transitionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WorkflowTransition no encontrada con id: " + transitionId));
        if (!t.getWorkflowDefinitionId().equals(workflowId)) {
            throw new ResourceNotFoundException(
                    "La transición " + transitionId + " no pertenece al workflow " + workflowId);
        }
        return t;
    }

    private void guardActiveWorkflow(WorkflowDefinition wf) {
        if (wf.getStatus() == WorkflowStatus.ACTIVE) {
            throw new IllegalStateException(
                    "No se puede modificar una transición de un workflow que está en estado ACTIVE.");
        }
    }
}
