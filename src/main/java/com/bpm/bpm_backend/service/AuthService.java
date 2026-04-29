package com.bpm.bpm_backend.service;

import com.bpm.bpm_backend.dto.AuthRequestDTO;
import com.bpm.bpm_backend.dto.AuthResponseDTO;
import com.bpm.bpm_backend.dto.RegisterRequestDTO;
import com.bpm.bpm_backend.model.User;
import com.bpm.bpm_backend.repository.UserRepository;
import com.bpm.bpm_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .departmentId(request.departmentId())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), user.getDepartmentId());
        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), user.getDepartmentId());
        return new AuthResponseDTO(token);
    }

    public com.bpm.bpm_backend.dto.UserDTO getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new com.bpm.bpm_backend.dto.UserDTO(
                user.getId(),
                user.getEmail(),
                user.getDepartmentId(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getRole()
        );
    }
}
