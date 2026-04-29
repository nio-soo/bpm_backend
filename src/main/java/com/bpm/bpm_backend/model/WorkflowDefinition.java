package com.bpm.bpm_backend.model;

import com.bpm.bpm_backend.model.enums.WorkflowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import com.bpm.bpm_backend.model.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workflow_definitions")
public class WorkflowDefinition {
    @Id
    private String id;
    private String name;
    private String description;
    private String bpmnXml;
    private List<Role> allowedRoles;

    @Builder.Default
    private WorkflowStatus status = WorkflowStatus.DRAFT;
}
