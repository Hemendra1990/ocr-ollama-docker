package com.hemendra.ocr.controller;

import com.hemendra.ocr.dto.AiAnalysisResponse;
import com.hemendra.ocr.exception.OcrException;
import com.hemendra.ocr.service.OllamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for AI image analysis operations
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {
    
    private static final Logger logger = LoggerFactory.getLogger(AiController.class);
    
    @Autowired
    private OllamaService ollamaService;
    
    /**
     * Analyze image using AI with default prompt
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AiAnalysisResponse> analyzeImage(
            @RequestParam("file") MultipartFile file) {
        
        logger.info("Received AI analysis request for file: {}", file.getOriginalFilename());
        
        try {
            AiAnalysisResponse response = ollamaService.analyzeImage(file);
            return ResponseEntity.ok(response);
            
        } catch (OcrException e) {
            logger.error("AI analysis failed: {}", e.getMessage());
            AiAnalysisResponse errorResponse = new AiAnalysisResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Unexpected error during AI analysis", e);
            AiAnalysisResponse errorResponse = new AiAnalysisResponse("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Analyze image using AI with custom prompt
     */
    @PostMapping(value = "/analyze-custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AiAnalysisResponse> analyzeImageWithPrompt(
            @RequestParam("file") MultipartFile file,
            @RequestParam("prompt") String prompt,
            @RequestParam(value = "model", defaultValue = "llava:latest") String model) {
        
        logger.info("Received AI analysis request for file: {} with custom prompt and model: {}", 
                   file.getOriginalFilename(), model);
        
        try {
            AiAnalysisResponse response = ollamaService.analyzeImage(file, prompt, model);
            return ResponseEntity.ok(response);
            
        } catch (OcrException e) {
            logger.error("AI analysis failed: {}", e.getMessage());
            AiAnalysisResponse errorResponse = new AiAnalysisResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Unexpected error during AI analysis", e);
            AiAnalysisResponse errorResponse = new AiAnalysisResponse("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Health check for AI service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        boolean ollamaAvailable = ollamaService.isOllamaAvailable();
        
        health.put("status", ollamaAvailable ? "UP" : "DOWN");
        health.put("service", "AI Analysis Service");
        health.put("ollama_available", ollamaAvailable);
        health.put("available_models", ollamaService.getAvailableModels());
        health.put("timestamp", System.currentTimeMillis());
        
        HttpStatus status = ollamaAvailable ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(health);
    }
    
    /**
     * Get available AI models
     */
    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getAvailableModels() {
        Map<String, Object> response = new HashMap<>();
        response.put("available_models", ollamaService.getAvailableModels());
        response.put("default_model", "llava:latest");
        response.put("max_file_size_mb", 50);
        
        return ResponseEntity.ok(response);
    }
}
