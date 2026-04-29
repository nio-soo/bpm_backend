package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.DynamicForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DynamicFormRepository extends MongoRepository<DynamicForm, String> {
    Optional<DynamicForm> findByWorkflowNodeId(String workflowNodeId);
}
