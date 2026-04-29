package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.AiAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiAnalysisRepository extends MongoRepository<AiAnalysis, String> {
}
