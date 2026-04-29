package com.bpm.bpm_backend.model;

import com.bpm.bpm_backend.model.enums.EndStatus;
import com.bpm.bpm_backend.model.enums.NodeType;
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
@Document(collection = "workflow_nodes")
public class WorkflowNode {
    @Id
    private String id;
    private String workflowDefinitionId;
    private String name;
    private NodeType type;
    private EndStatus endStatus;
    private String departmentId;
}
