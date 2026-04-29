package com.bpm.bpm_backend.service;

import com.bpm.bpm_backend.dto.DynamicFormDTO;
import com.bpm.bpm_backend.dto.request.DynamicFormRequest;
import com.bpm.bpm_backend.exception.ResourceNotFoundException;
import com.bpm.bpm_backend.model.DynamicForm;
import com.bpm.bpm_backend.repository.DynamicFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DynamicFormService {

    private final DynamicFormRepository dynamicFormRepository;

    // ── Mapping ──────────────────────────────────────────────────────────────

    private DynamicFormDTO toDTO(DynamicForm f) {
        return new DynamicFormDTO(f.getId(), f.getWorkflowNodeId(), f.getFormSchema());
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public List<DynamicFormDTO> findAll() {
        return dynamicFormRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public DynamicFormDTO findById(String id) {
        return toDTO(getOrThrow(id));
    }

    public DynamicFormDTO create(DynamicFormRequest request) {
        DynamicForm form = DynamicForm.builder()
                .workflowNodeId(request.workflowNodeId())
                .formSchema(request.formSchema())
                .build();
        return toDTO(dynamicFormRepository.save(form));
    }

    public DynamicFormDTO update(String id, DynamicFormRequest request) {
        DynamicForm form = getOrThrow(id);
        form.setWorkflowNodeId(request.workflowNodeId());
        form.setFormSchema(request.formSchema());
        return toDTO(dynamicFormRepository.save(form));
    }

    public void delete(String id) {
        getOrThrow(id);
        dynamicFormRepository.deleteById(id);
    }

    public DynamicFormDTO getFormByNodeId(String nodeId) {
        return dynamicFormRepository.findByWorkflowNodeId(nodeId)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DynamicForm no encontrado para el nodo: " + nodeId));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private DynamicForm getOrThrow(String id) {
        return dynamicFormRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DynamicForm no encontrado con id: " + id));
    }
}
