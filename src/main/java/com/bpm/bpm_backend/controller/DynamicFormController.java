package com.bpm.bpm_backend.controller;

import com.bpm.bpm_backend.dto.DynamicFormDTO;
import com.bpm.bpm_backend.dto.request.DynamicFormRequest;
import com.bpm.bpm_backend.service.DynamicFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de DynamicForms.
 * Acceso restringido a PROCESS_ARCHITECT y DEPT_MANAGER (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/api/dept/forms")
@RequiredArgsConstructor
public class DynamicFormController {

    private final DynamicFormService dynamicFormService;

    @GetMapping
    public ResponseEntity<List<DynamicFormDTO>> getAllForms() {
        return ResponseEntity.ok(dynamicFormService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DynamicFormDTO> getFormById(@PathVariable String id) {
        return ResponseEntity.ok(dynamicFormService.findById(id));
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<DynamicFormDTO> getFormByNodeId(@PathVariable String nodeId) {
        return ResponseEntity.ok(dynamicFormService.getFormByNodeId(nodeId));
    }

    @PostMapping
    public ResponseEntity<DynamicFormDTO> createForm(
            @Valid @RequestBody DynamicFormRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dynamicFormService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DynamicFormDTO> updateForm(
            @PathVariable String id,
            @Valid @RequestBody DynamicFormRequest request) {
        return ResponseEntity.ok(dynamicFormService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable String id) {
        dynamicFormService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
