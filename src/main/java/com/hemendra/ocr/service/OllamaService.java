package com.hemendra.ocr.service;

import com.hemendra.ocr.dto.AiAnalysisResponse;
import com.hemendra.ocr.dto.OllamaRequest;
import com.hemendra.ocr.dto.OllamaResponse;
import com.hemendra.ocr.exception.OcrException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Service for interacting with Ollama API
 */
@Service
public class OllamaService {
    
    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);
    
    private static final String DEFAULT_MODEL = "llava:latest";
    private static final Duration TIMEOUT = Duration.ofMinutes(5);
    
    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;
    
    private final WebClient webClient;
    
    public OllamaService() {
        this.webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024)) // 50MB
            .build();
    }
    
    /**
     * Analyze image using Ollama with default prompt
     */
    public AiAnalysisResponse analyzeImage(MultipartFile file) throws OcrException {
        String defaultPrompt = "Describe what you see in this image in detail. Include any text, objects, people, colors, and overall composition.";
        return analyzeImage(file, defaultPrompt, DEFAULT_MODEL);
    }
    
    /**
     * Analyze image using Ollama with custom prompt
     */
    public AiAnalysisResponse analyzeImage(MultipartFile file, String prompt) throws OcrException {
        return analyzeImage(file, prompt, DEFAULT_MODEL);
    }
    
    /**
     * Analyze image using Ollama with custom prompt and model
     */
    public AiAnalysisResponse analyzeImage(MultipartFile file, String prompt, String model) throws OcrException {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate file
            validateImageFile(file);
            
            // Convert image to base64
            String base64Image = encodeImageToBase64(file);
            
            // Create Ollama request
            OllamaRequest request = new OllamaRequest(model, prompt, Collections.singletonList(base64Image));
            
            // Call Ollama API
            OllamaResponse ollamaResponse = callOllamaApi(request);
            
            // Calculate processing time
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Create response
            AiAnalysisResponse response = new AiAnalysisResponse(
                ollamaResponse.getResponse(), 
                model, 
                file.getOriginalFilename(), 
                file.getSize()
            );
            response.setPrompt(prompt);
            response.setProcessingTimeMs(processingTime);
            
            // Add Ollama stats
            if (ollamaResponse.getTotalDuration() != null) {
                AiAnalysisResponse.OllamaStats stats = new AiAnalysisResponse.OllamaStats();
                stats.setTotalDurationMs(ollamaResponse.getTotalDuration() / 1_000_000); // Convert nanoseconds to milliseconds
                stats.setLoadDurationMs(ollamaResponse.getLoadDuration() != null ? 
                    ollamaResponse.getLoadDuration() / 1_000_000 : null);
                stats.setEvalCount(ollamaResponse.getEvalCount());
                stats.setEvalDurationMs(ollamaResponse.getEvalDuration() != null ? 
                    ollamaResponse.getEvalDuration() / 1_000_000 : null);
                response.setOllamaStats(stats);
            }
            
            logger.info("AI analysis completed successfully for file: {} in {}ms using model: {}", 
                       file.getOriginalFilename(), processingTime, model);
            
            return response;
            
        } catch (IOException e) {
            logger.error("IO error while processing file: {}", file.getOriginalFilename(), e);
            throw new OcrException("Error reading image file: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("AI analysis failed for file: {}", file.getOriginalFilename(), e);
            throw new OcrException("AI analysis failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if Ollama service is available
     */
    public boolean isOllamaAvailable() {
        try {
            webClient.get()
                .uri(ollamaBaseUrl + "/api/tags")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            return true;
        } catch (Exception e) {
            logger.warn("Ollama service is not available: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get list of available models
     */
    public List<String> getAvailableModels() {
        try {
            // This is a simplified implementation
            // In a real scenario, you'd parse the /api/tags response
            return List.of(DEFAULT_MODEL, "llava:7b", "llava:13b");
        } catch (Exception e) {
            logger.error("Failed to get available models", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Call Ollama API
     */
    private OllamaResponse callOllamaApi(OllamaRequest request) throws OcrException {
        try {
            return webClient.post()
                .uri(ollamaBaseUrl + "/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaResponse.class)
                .timeout(TIMEOUT)
                .block();
        } catch (WebClientResponseException e) {
            logger.error("Ollama API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new OcrException("Ollama API error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to call Ollama API", e);
            throw new OcrException("Failed to communicate with AI service: " + e.getMessage());
        }
    }
    
    /**
     * Encode image to base64
     */
    private String encodeImageToBase64(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        return Base64.encodeBase64String(imageBytes);
    }
    
    /**
     * Validate image file
     */
    private void validateImageFile(MultipartFile file) throws OcrException {
        if (file == null || file.isEmpty()) {
            throw new OcrException("No file uploaded or file is empty");
        }
        
        if (file.getSize() > 50 * 1024 * 1024) { // 50MB limit for AI analysis
            throw new OcrException("File size exceeds maximum limit of 50MB for AI analysis");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new OcrException("File must be an image");
        }
    }
}
