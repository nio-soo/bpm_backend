package com.bpm.bpm_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_analysis")
public class AiAnalysis {
    @Id
    private String id;
    private String processInstanceId;
    private String analysisResult;
    private LocalDateTime createdAt;
}
