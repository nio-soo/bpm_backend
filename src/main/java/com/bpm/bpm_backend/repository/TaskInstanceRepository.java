package com.bpm.bpm_backend.repository;

import com.bpm.bpm_backend.model.TaskInstance;
import com.bpm.bpm_backend.model.enums.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskInstanceRepository extends MongoRepository<TaskInstance, String> {

    List<TaskInstance> findByProcessInstanceId(String processInstanceId);

    List<TaskInstance> findByDepartmentIdAndStatus(String departmentId, TaskStatus status);

    List<TaskInstance> findByAssignedUserIdAndStatus(String assignedUserId, TaskStatus status);

    List<TaskInstance> findByAssignedUserId(String assignedUserId);

    List<TaskInstance> findByStatus(TaskStatus status);
}
