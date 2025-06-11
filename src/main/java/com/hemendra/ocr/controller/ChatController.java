package com.hemendra.ocr.controller;

import com.hemendra.ocr.dto.ChatRequest;
import com.hemendra.ocr.dto.ChatResponse;
import com.hemendra.ocr.exception.OcrException;
import com.hemendra.ocr.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Chat functionality
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private ChatService chatService;
    
    /**
     * Send a chat message
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        try {
            logger.info("Received chat request for model: {}", request.getModel());
            
            ChatResponse response = chatService.chat(request);
            return ResponseEntity.ok(response);
            
        } catch (OcrException e) {
            logger.error("Chat request failed", e);
            ChatResponse errorResponse = new ChatResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error in chat", e);
            ChatResponse errorResponse = new ChatResponse("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Send a chat message with streaming response
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> sendMessageStream(@RequestBody ChatRequest request) {
        try {
            logger.info("Received streaming chat request for model: {}", request.getModel());

            return chatService.chatStream(request)
                .onErrorResume(throwable -> {
                    logger.error("Streaming chat request failed", throwable);
                    return Flux.just("Error: " + throwable.getMessage());
                });

        } catch (Exception e) {
            logger.error("Unexpected error in streaming chat", e);
            return Flux.just("Error: " + e.getMessage());
        }
    }

    /**
     * Install a new model
     */
    @PostMapping("/install-model")
    public ResponseEntity<ChatResponse> installModel(@RequestParam("model") String modelName) {
        try {
            logger.info("Installing model: {}", modelName);
            
            ChatResponse response = chatService.installModel(modelName);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Model installation failed", e);
            ChatResponse errorResponse = new ChatResponse("Model installation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Check if a model is available
     */
    @GetMapping("/model-status/{modelName}")
    public ResponseEntity<Map<String, Object>> getModelStatus(@PathVariable String modelName) {
        try {
            boolean available = chatService.isModelAvailable(modelName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("model", modelName);
            response.put("available", available);
            response.put("status", available ? "ready" : "not_installed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to check model status", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("model", modelName);
            errorResponse.put("available", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Get available text models
     */
    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getAvailableModels() {
        try {
            List<String> models = chatService.getAvailableTextModels();
            
            Map<String, Object> response = new HashMap<>();
            response.put("available_models", models);
            response.put("default_model", "gemma2:2b");
            response.put("recommended_models", List.of("gemma2:2b", "llama3.2:1b", "phi3:mini"));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to get available models", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get models: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Clear conversation history
     */
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Map<String, Object>> clearConversation(@PathVariable String conversationId) {
        try {
            chatService.clearConversation(conversationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("conversation_id", conversationId);
            response.put("status", "cleared");
            response.put("message", "Conversation history cleared successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to clear conversation", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to clear conversation: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Get conversation history
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Map<String, Object>> getConversationHistory(@PathVariable String conversationId) {
        try {
            List<Map<String, String>> history = chatService.getConversationHistory(conversationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("conversation_id", conversationId);
            response.put("history", history);
            response.put("message_count", history.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to get conversation history", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get conversation history: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Health check for chat service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("service", "Chat Service");
            response.put("timestamp", System.currentTimeMillis());
            
            // Check if at least one model is available
            List<String> models = chatService.getAvailableTextModels();
            boolean hasModels = !models.isEmpty();
            
            response.put("models_available", hasModels);
            response.put("model_count", models.size());
            
            if (hasModels) {
                response.put("sample_models", models.subList(0, Math.min(3, models.size())));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Chat health check failed", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "DOWN");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
