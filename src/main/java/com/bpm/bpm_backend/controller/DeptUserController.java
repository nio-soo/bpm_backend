package com.bpm.bpm_backend.controller;

import com.bpm.bpm_backend.dto.UserDTO;
import com.bpm.bpm_backend.exception.ResourceNotFoundException;
import com.bpm.bpm_backend.model.User;
import com.bpm.bpm_backend.model.enums.Role;
import com.bpm.bpm_backend.repository.UserRepository;
import com.bpm.bpm_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dept/users")
@RequiredArgsConstructor
public class DeptUserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getDeptUsers() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        if (currentUser.getRole() == Role.PROCESS_ARCHITECT) {
            return ResponseEntity.ok(userService.findAll());
        }

        return ResponseEntity.ok(userService.findByDepartmentId(currentUser.getDepartmentId()));
    }
}
