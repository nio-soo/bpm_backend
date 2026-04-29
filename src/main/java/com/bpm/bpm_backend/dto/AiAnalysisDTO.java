package com.bpm.bpm_backend.dto;

import java.time.LocalDateTime;

public record AiAnalysisDTO(
    String id,
    String processInstanceId,
    String analysisResult,
    LocalDateTime createdAt
) {}
