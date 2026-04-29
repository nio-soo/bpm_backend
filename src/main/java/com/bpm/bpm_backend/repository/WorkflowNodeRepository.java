package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.WorkflowNode;
import com.bpm.bpm_backend.model.enums.NodeType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowNodeRepository extends MongoRepository<WorkflowNode, String> {
    List<WorkflowNode> findByWorkflowDefinitionId(String workflowDefinitionId);
    List<WorkflowNode> findByWorkflowDefinitionIdAndType(String workflowDefinitionId, NodeType type);
    void deleteByWorkflowDefinitionId(String workflowDefinitionId);
}
