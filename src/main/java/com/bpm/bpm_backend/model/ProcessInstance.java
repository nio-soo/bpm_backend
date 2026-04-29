package com.bpm.bpm_backend.model;

import com.bpm.bpm_backend.model.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "process_instances")
public class ProcessInstance {

    @Id
    private String id;

    @Indexed
    private String workflowDefinitionId;

    @Indexed
    private String requesterId;

    @Builder.Default
    private ProcessStatus status = ProcessStatus.IN_PROGRESS;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();
}
