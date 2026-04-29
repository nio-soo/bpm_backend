package com.bpm.bpm_backend.dto;

import java.util.Map;

public record DynamicFormDTO(
    String id,
    String workflowNodeId,
    Map<String, Object> formSchema
) {}
