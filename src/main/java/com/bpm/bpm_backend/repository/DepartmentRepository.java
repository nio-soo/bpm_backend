package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {

    boolean existsByName(String name);
}
