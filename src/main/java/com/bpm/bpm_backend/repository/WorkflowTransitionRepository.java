package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.WorkflowTransition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowTransitionRepository extends MongoRepository<WorkflowTransition, String> {
    List<WorkflowTransition> findByWorkflowDefinitionId(String workflowDefinitionId);
    void deleteByWorkflowDefinitionId(String workflowDefinitionId);
}
