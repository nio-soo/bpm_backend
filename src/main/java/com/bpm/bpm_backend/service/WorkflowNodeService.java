package com.bpm.bpm_backend.service;

import com.bpm.bpm_backend.dto.WorkflowNodeDTO;
import com.bpm.bpm_backend.dto.request.WorkflowNodeRequest;
import com.bpm.bpm_backend.exception.ResourceNotFoundException;
import com.bpm.bpm_backend.model.WorkflowDefinition;
import com.bpm.bpm_backend.model.WorkflowNode;
import com.bpm.bpm_backend.model.enums.WorkflowStatus;
import com.bpm.bpm_backend.repository.WorkflowNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowNodeService {

    private final WorkflowNodeRepository workflowNodeRepository;
    private final WorkflowDefinitionService workflowDefinitionService;

    // ── Mapping ──────────────────────────────────────────────────────────────

    private WorkflowNodeDTO toDTO(WorkflowNode n) {
        return new WorkflowNodeDTO(
                n.getId(), n.getWorkflowDefinitionId(), n.getName(), n.getType(), n.getEndStatus(),
                n.getDepartmentId());
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public List<WorkflowNodeDTO> findByWorkflow(String workflowId) {
        workflowDefinitionService.getOrThrow(workflowId); // verifica que el workflow exista
        return workflowNodeRepository.findByWorkflowDefinitionId(workflowId)
                .stream().map(this::toDTO).toList();
    }

    public WorkflowNodeDTO findById(String workflowId, String nodeId) {
        return toDTO(getNodeOrThrow(workflowId, nodeId));
    }

    public WorkflowNodeDTO create(String workflowId, WorkflowNodeRequest request) {
        workflowDefinitionService.getOrThrow(workflowId); // verifica que el workflow exista
        WorkflowNode node = WorkflowNode.builder()
                .workflowDefinitionId(workflowId)
                .name(request.name())
                .type(request.type())
                .endStatus(request.endStatus())
                .departmentId(request.departmentId())
                .build();
        return toDTO(workflowNodeRepository.save(node));
    }

    /**
     * Actualización protegida: lanza {@link IllegalStateException} si el workflow
     * está ACTIVE.
     */
    public WorkflowNodeDTO update(String workflowId, String nodeId, WorkflowNodeRequest request) {
        WorkflowDefinition wf = workflowDefinitionService.getOrThrow(workflowId);
        guardActiveWorkflow(wf);

        WorkflowNode node = getNodeOrThrow(workflowId, nodeId);
        node.setName(request.name());
        node.setType(request.type());
        node.setEndStatus(request.endStatus());
        node.setDepartmentId(request.departmentId());
        return toDTO(workflowNodeRepository.save(node));
    }

    public void delete(String workflowId, String nodeId) {
        getNodeOrThrow(workflowId, nodeId);
        workflowNodeRepository.deleteById(nodeId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private WorkflowNode getNodeOrThrow(String workflowId, String nodeId) {
        WorkflowNode node = workflowNodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WorkflowNode no encontrado con id: " + nodeId));
        if (!node.getWorkflowDefinitionId().equals(workflowId)) {
            throw new ResourceNotFoundException(
                    "El nodo " + nodeId + " no pertenece al workflow " + workflowId);
        }
        return node;
    }

    private void guardActiveWorkflow(WorkflowDefinition wf) {
        if (wf.getStatus() == WorkflowStatus.ACTIVE) {
            throw new IllegalStateException(
                    "No se puede modificar el nodo de un workflow que está en estado ACTIVE.");
        }
    }
}
