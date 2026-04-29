package com.bpm.bpm_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "node_executions")
public class NodeExecution {
    @Id
    private String id;
    private String processInstanceId;
    private String workflowNodeId;
    private String executedById;
    private Map<String, Object> formResponse;
    private LocalDateTime executedAt;
}
