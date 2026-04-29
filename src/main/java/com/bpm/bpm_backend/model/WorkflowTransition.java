package com.bpm.bpm_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workflow_transitions")
public class WorkflowTransition {
    @Id
    private String id;
    private String workflowDefinitionId;
    private String fromNodeId;
    private String toNodeId;
    private String condition;
}
