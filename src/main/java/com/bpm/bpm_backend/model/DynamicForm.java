package com.bpm.bpm_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dynamic_forms")
public class DynamicForm {
    @Id
    private String id;
    private String workflowNodeId;
    private Map<String, Object> formSchema;
}
