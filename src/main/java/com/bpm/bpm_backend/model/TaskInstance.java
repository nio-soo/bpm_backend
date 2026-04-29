package com.bpm.bpm_backend.model;

import com.bpm.bpm_backend.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "task_instances")
public class TaskInstance {

    @Id
    private String id;

    @Indexed
    private String processInstanceId;

    /** ID del nodo del workflow al que corresponde esta tarea */
    private String nodeId;

    /** Departamento responsable de esta tarea */
    @Indexed
    private String departmentId;

    /** Usuario que reclamó/tomó la tarea */
    private String assignedUserId;

    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    private LocalDateTime createdDate;
    private LocalDateTime claimedDate;
    private LocalDateTime completedDate;
}
