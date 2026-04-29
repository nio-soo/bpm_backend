package com.bpm.bpm_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
@SecurityRequirements
@Tag(name = "AI Mock", description = "Endpoints mock de generación por IA — accesibles sin autenticación")
public class AiMockController {

    @PostMapping("/generate-bpmn")
    @Operation(summary = "Generar BPMN", description = "Recibe un prompt y devuelve un XML BPMN mock.")
    public ResponseEntity<Map<String, String>> generateBpmn(@RequestBody Map<String, String> prompt) {
        // Simula la espera de Camunda/Grok
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        
        String mockXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bpmn2:definitions ... />"; // Aquí pega tu XML funcional
        return ResponseEntity.ok(Map.of("bpmnXml", mockXml, "status", "success"));
    }

    @PostMapping("/generate-form")
    @Operation(summary = "Generar formulario", description = "Recibe un prompt y devuelve un esquema de formulario mock con campos genéricos.")
    public ResponseEntity<Map<String, Object>> generateForm(@RequestBody Map<String, String> request) {
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
        
        // JSON falso con campos genéricos
        List<Map<String, Object>> mockFields = List.of(
            Map.of("type", "label", "label", "Formulario Generado por IA"),
            Map.of("type", "text", "label", "Nombre Completo"),
            Map.of("type", "textarea", "label", "Motivo de la Solicitud")
        );
        return ResponseEntity.ok(Map.of("fields", mockFields));
    }
}