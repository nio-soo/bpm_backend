package com.bpm.bpm_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bpm.bpm_backend.dto.ProcessInstanceDTO;
import com.bpm.bpm_backend.dto.TaskInstanceDTO;
import com.bpm.bpm_backend.dto.request.StartProcessRequest;
import com.bpm.bpm_backend.model.ProcessInstance;
import com.bpm.bpm_backend.model.TaskInstance;
import com.bpm.bpm_backend.model.WorkflowDefinition;
import com.bpm.bpm_backend.model.WorkflowNode;
import com.bpm.bpm_backend.model.WorkflowTransition;
import com.bpm.bpm_backend.model.enums.NodeType;
import com.bpm.bpm_backend.model.enums.ProcessStatus;
import com.bpm.bpm_backend.model.enums.Role;
import com.bpm.bpm_backend.model.enums.TaskStatus;
import com.bpm.bpm_backend.repository.ProcessInstanceRepository;
import com.bpm.bpm_backend.repository.TaskInstanceRepository;
import com.bpm.bpm_backend.repository.WorkflowDefinitionRepository;
import com.bpm.bpm_backend.repository.WorkflowNodeRepository;
import com.bpm.bpm_backend.repository.WorkflowTransitionRepository;
import com.bpm.bpm_backend.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

/**
 * Motor de ejecución BPM.
 * <p>
 * Responsabilidades:
 * <ul>
 * <li>Iniciar un {@link ProcessInstance} validando el rol del solicitante.</li>
 * <li>Crear la primera {@link TaskInstance} apuntando al primer nodo TASK
 * que sigue al nodo START.</li>
 * <li>Avanzar el proceso al siguiente nodo cuando se completa una tarea.</li>
 * <li>Cerrar el proceso al llegar a un nodo END.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ProcessEngineService {

    private final ProcessInstanceRepository processInstanceRepo;
    private final TaskInstanceRepository taskInstanceRepo;
    private final WorkflowDefinitionRepository workflowDefinitionRepo;
    private final WorkflowNodeRepository workflowNodeRepo;
    private final WorkflowTransitionRepository workflowTransitionRepo;
    private final SecurityUtils securityUtils;

    // -------------------------------------------------------------------------
    // START PROCESS
    // -------------------------------------------------------------------------

    /**
     * Inicia un nuevo proceso a partir de un workflow publicado.
     *
     * @param workflowId ID del WorkflowDefinition
     * @param request    Variables iniciales y, opcionalmente, fecha artificial de
     *                   inicio
     * @return DTO del ProcessInstance creado
     */
    public ProcessInstanceDTO startProcess(String workflowId, StartProcessRequest request) {

        // 1. Cargar workflow
        WorkflowDefinition workflow = workflowDefinitionRepo.findById(workflowId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Workflow no encontrado: " + workflowId));

        // 2. Validar que el rol del usuario esté en los allowedRoles
        com.bpm.bpm_backend.model.User currentUser = securityUtils.getCurrentUser();
        Role userRole = currentUser.getRole();

        List<Role> allowed = workflow.getAllowedRoles();
        if (allowed == null || !allowed.contains(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tu rol '" + userRole + "' no tiene permiso para iniciar este proceso");
        }

        // 3. Resolver tiempo de inicio (real o artificial para seeder)
        LocalDateTime startTime = (request != null && request.startTime() != null)
                ? request.startTime()
                : LocalDateTime.now();

        // 4. Crear ProcessInstance
        ProcessInstance process = ProcessInstance.builder()
                .workflowDefinitionId(workflowId)
                .requesterId(currentUser.getId())
                .status(ProcessStatus.IN_PROGRESS)
                .startTime(startTime)
                .variables(request != null && request.variables() != null
                        ? request.variables()
                        : Map.of())
                .build();

        process = processInstanceRepo.save(process);

        // 5. Localizar nodo START del workflow
        List<WorkflowNode> startNodes = workflowNodeRepo
                .findByWorkflowDefinitionIdAndType(workflowId, NodeType.START);

        if (startNodes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El workflow no tiene un nodo START definido");
        }
        WorkflowNode startNode = startNodes.get(0);

        // 6. Encontrar el primer nodo TASK después del START
        WorkflowNode firstTaskNode = findNextTaskNode(workflowId, startNode.getId(), null);

        if (firstTaskNode == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El workflow no tiene nodos TASK accesibles desde el START");
        }

        // 7. Crear la primera TaskInstance
        TaskInstance task = TaskInstance.builder()
                .processInstanceId(process.getId())
                .nodeId(firstTaskNode.getId())
                .departmentId(firstTaskNode.getDepartmentId())
                .status(TaskStatus.PENDING)
                .createdDate(startTime)
                .build();

        taskInstanceRepo.save(task);

        return toProcessDTO(process);
    }

    // -------------------------------------------------------------------------
    // COMPLETE TASK — motor de avance
    // -------------------------------------------------------------------------

    /**
     * Completa una tarea y crea la siguiente TaskInstance según las transiciones
     * del workflow. Si el siguiente nodo es un END, cierra el proceso.
     *
     * @param taskId        ID de la TaskInstance a completar
     * @param decision      Condición a evaluar en nodos DECISION (puede ser null)
     * @param completedDate Fecha artificial opcional (para seeder)
     * @return DTO de la tarea completada
     */
    public TaskInstanceDTO completeTask(String taskId, String decision, LocalDateTime completedDate) {

        // 1. Cargar tarea
        TaskInstance task = taskInstanceRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tarea no encontrada: " + taskId));

        // 2. Solo el usuario asignado puede completarla
        String currentUserId = securityUtils.getCurrentUserId();
        //if (!currentUserId.equals(task.getAssignedUserId())) {
        //    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
        //            "Solo el usuario asignado puede completar esta tarea");
        //}

        if (task.getStatus() != TaskStatus.CLAIMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La tarea debe estar en estado CLAIMED para poder completarse");
        }

        // 3. Marcar tarea como completada
        LocalDateTime now = (completedDate != null) ? completedDate : LocalDateTime.now();
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedDate(now);
        taskInstanceRepo.save(task);

        // 4. Cargar proceso
        ProcessInstance process = processInstanceRepo.findById(task.getProcessInstanceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Proceso no encontrado: " + task.getProcessInstanceId()));

        // 5. Avanzar al siguiente nodo
        advanceProcess(process, task.getNodeId(), decision, now);

        return toTaskDTO(task);
    }

    // -------------------------------------------------------------------------
    // Lógica interna de avance
    // -------------------------------------------------------------------------

    private void advanceProcess(ProcessInstance process, String currentNodeId,
            String decision, LocalDateTime timestamp) {

        String workflowId = process.getWorkflowDefinitionId();

        // Obtener transiciones desde el nodo actual
        List<WorkflowTransition> transitions = workflowTransitionRepo
                .findByWorkflowDefinitionId(workflowId)
                .stream()
                .filter(t -> currentNodeId.equals(t.getFromNodeId()))
                .toList();

        if (transitions.isEmpty()) {
            // Sin transiciones → cerrar proceso por defecto
            closeProcess(process, ProcessStatus.COMPLETED, timestamp);
            return;
        }

        // Seleccionar transición: si hay condición, filtra por ella; si no, toma la
        // primera
        WorkflowTransition selected = transitions.stream()
                .filter(t -> t.getCondition() == null || t.getCondition().isBlank()
                        || t.getCondition().equalsIgnoreCase(decision))
                .findFirst()
                .orElse(transitions.get(0));

        // Cargar nodo destino
        WorkflowNode nextNode = workflowNodeRepo.findById(selected.getToNodeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Nodo destino no encontrado: " + selected.getToNodeId()));

        switch (nextNode.getType()) {

            case END -> {
                // Determinar estado final según endStatus del nodo
                ProcessStatus finalStatus = resolveEndStatus(nextNode);
                closeProcess(process, finalStatus, timestamp);
            }

            case TASK -> {
                // Crear nueva TaskInstance para este nodo
                TaskInstance nextTask = TaskInstance.builder()
                        .processInstanceId(process.getId())
                        .nodeId(nextNode.getId())
                        .departmentId(nextNode.getDepartmentId())
                        .status(TaskStatus.PENDING)
                        .createdDate(timestamp)
                        .build();
                taskInstanceRepo.save(nextTask);
            }

            case DECISION -> {
                // Avanzar recursivamente evaluando la misma decisión
                advanceProcess(process, nextNode.getId(), decision, timestamp);
            }

            default ->
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Tipo de nodo no soportado para avance: " + nextNode.getType());
        }
    }

    private void closeProcess(ProcessInstance process, ProcessStatus status, LocalDateTime endTime) {
        process.setStatus(status);
        process.setEndTime(endTime);
        processInstanceRepo.save(process);
    }

    private ProcessStatus resolveEndStatus(WorkflowNode endNode) {
        if (endNode.getEndStatus() == null) {
            return ProcessStatus.COMPLETED;
        }
        return switch (endNode.getEndStatus()) {
            case APPROVED -> ProcessStatus.COMPLETED;
            case REJECTED -> ProcessStatus.REJECTED;
            case FILED -> ProcessStatus.FILED;
            default -> ProcessStatus.COMPLETED;
        };
    }

    /**
     * Navega el grafo de transiciones desde {@code fromNodeId} buscando el
     * primer nodo de tipo TASK (saltando nodos intermedios como DECISION).
     */
    private WorkflowNode findNextTaskNode(String workflowId, String fromNodeId, String decision) {

        List<WorkflowTransition> transitions = workflowTransitionRepo
                .findByWorkflowDefinitionId(workflowId)
                .stream()
                .filter(t -> fromNodeId.equals(t.getFromNodeId()))
                .toList();

        for (WorkflowTransition t : transitions) {
            WorkflowNode node = workflowNodeRepo.findById(t.getToNodeId()).orElse(null);
            if (node == null)
                continue;

            if (node.getType() == NodeType.TASK)
                return node;
            if (node.getType() == NodeType.DECISION || node.getType() == NodeType.START) {
                WorkflowNode deeper = findNextTaskNode(workflowId, node.getId(), decision);
                if (deeper != null)
                    return deeper;
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Consultas de seguimiento
    // -------------------------------------------------------------------------

    /** Todos los procesos del usuario autenticado (para /me). */
    public List<ProcessInstanceDTO> getMyProcesses() {
        String userId = securityUtils.getCurrentUserId();
        return processInstanceRepo.findByRequesterId(userId)
                .stream().map(this::toProcessDTO).toList();
    }

    /** Tareas PENDING del departamento del usuario autenticado. */
    public List<TaskInstanceDTO> getTasksForMyDepartment() {
        com.bpm.bpm_backend.model.User user = securityUtils.getCurrentUser();
        return taskInstanceRepo
                .findByDepartmentIdAndStatus(user.getDepartmentId(), TaskStatus.PENDING)
                .stream().map(this::toTaskDTO).toList();
    }

    /** Devuelve los detalles de una tarea por su ID. */
    public TaskInstanceDTO getTaskById(String taskId) {
        TaskInstance task = taskInstanceRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tarea no encontrada: " + taskId));
        return toTaskDTO(task);
    }

    /** Asigna la tarea al usuario autenticado (claim). */
    public TaskInstanceDTO claimTask(String taskId) {
        TaskInstance task = taskInstanceRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tarea no encontrada: " + taskId));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden reclamar tareas en estado PENDING");
        }

        String userId = securityUtils.getCurrentUserId();
        task.setAssignedUserId(userId);
        task.setStatus(TaskStatus.CLAIMED);
        task.setClaimedDate(LocalDateTime.now());

        return toTaskDTO(taskInstanceRepo.save(task));
    }

    // -------------------------------------------------------------------------
    // Mappers
    // -------------------------------------------------------------------------

    public ProcessInstanceDTO toProcessDTO(ProcessInstance p) {
        return new ProcessInstanceDTO(
                p.getId(),
                p.getWorkflowDefinitionId(),
                p.getRequesterId(),
                p.getStatus(),
                p.getStartTime(),
                p.getEndTime(),
                p.getVariables());
    }

    public TaskInstanceDTO toTaskDTO(TaskInstance t) {
        return new TaskInstanceDTO(
                t.getId(),
                t.getProcessInstanceId(),
                t.getNodeId(),
                t.getDepartmentId(),
                t.getAssignedUserId(),
                t.getStatus(),
                t.getCreatedDate(),
                t.getClaimedDate(),
                t.getCompletedDate());
    }
}
