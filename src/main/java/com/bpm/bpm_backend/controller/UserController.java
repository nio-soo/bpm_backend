package com.bpm.bpm_backend.controller;

import com.bpm.bpm_backend.dto.UserDTO;
import com.bpm.bpm_backend.dto.request.UserUpdateRequest;
import com.bpm.bpm_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UserDTO> toggleStatus(@PathVariable String id) {
        return ResponseEntity.ok(userService.toggleStatus(id));
    }
}
