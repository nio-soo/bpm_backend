package com.bpm.bpm_backend.service;

import com.bpm.bpm_backend.dto.DepartmentDTO;
import com.bpm.bpm_backend.dto.request.DepartmentRequest;
import com.bpm.bpm_backend.exception.ResourceNotFoundException;
import com.bpm.bpm_backend.model.Department;
import com.bpm.bpm_backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    private DepartmentDTO toDTO(Department d) {
        return new DepartmentDTO(d.getId(), d.getName(), d.isActive());
    }

    public List<DepartmentDTO> findAll() {
        return departmentRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public DepartmentDTO findById(String id) {
        return toDTO(getOrThrow(id));
    }

    public DepartmentDTO create(DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.name())
                .build();
        return toDTO(departmentRepository.save(department));
    }

    public DepartmentDTO update(String id, DepartmentRequest request) {
        Department department = getOrThrow(id);
        department.setName(request.name());
        return toDTO(departmentRepository.save(department));
    }

    public DepartmentDTO toggleStatus(String id) {
        Department department = getOrThrow(id);
        department.setActive(!department.isActive());
        return toDTO(departmentRepository.save(department));
    }

    private Department getOrThrow(String id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con id: " + id));
    }
}
