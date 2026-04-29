package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.WorkflowDefinition;
import com.bpm.bpm_backend.model.enums.Role;
import com.bpm.bpm_backend.model.enums.WorkflowStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowDefinitionRepository extends MongoRepository<WorkflowDefinition, String> {

    List<WorkflowDefinition> findByStatusAndAllowedRolesContaining(WorkflowStatus status, Role role);
}
