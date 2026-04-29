package com.bpm.bpm_backend.service;

import com.bpm.bpm_backend.dto.UserDTO;
import com.bpm.bpm_backend.dto.request.UserUpdateRequest;
import com.bpm.bpm_backend.exception.ResourceNotFoundException;
import com.bpm.bpm_backend.model.User;
import com.bpm.bpm_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private UserDTO toDTO(User u) {
        return new UserDTO(
                u.getId(), u.getEmail(), u.getDepartmentId(),
                u.isActive(), u.getCreatedAt(), u.getUpdatedAt(), u.getRole()
        );
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        user.setRole(request.role());
        user.setDepartmentId(request.departmentId());
        user.setUpdatedAt(LocalDateTime.now());

        return toDTO(userRepository.save(user));
    }

    public UserDTO toggleStatus(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        user.setActive(!user.isActive());
        user.setUpdatedAt(LocalDateTime.now());
        return toDTO(userRepository.save(user));
    }

    public List<UserDTO> findByDepartmentId(String departmentId) {
        return userRepository.findByDepartmentId(departmentId).stream()
                .map(this::toDTO)
                .toList();
    }
}
