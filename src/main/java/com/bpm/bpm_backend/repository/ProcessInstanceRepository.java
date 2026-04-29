package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.ProcessInstance;
import com.bpm.bpm_backend.model.enums.ProcessStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessInstanceRepository extends MongoRepository<ProcessInstance, String> {

    List<ProcessInstance> findByRequesterId(String requesterId);

    List<ProcessInstance> findByRequesterIdAndStatus(String requesterId, ProcessStatus status);

    List<ProcessInstance> findByWorkflowDefinitionId(String workflowDefinitionId);

    List<ProcessInstance> findByStatus(ProcessStatus status);
}
