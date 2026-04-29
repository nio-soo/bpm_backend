package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.NodeExecution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeExecutionRepository extends MongoRepository<NodeExecution, String> {
}
