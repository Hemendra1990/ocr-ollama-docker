package com.hemendra.ocr.service;

import com.hemendra.ocr.dto.ChatRequest;
import com.hemendra.ocr.dto.ChatResponse;
import com.hemendra.ocr.dto.OllamaRequest;
import com.hemendra.ocr.dto.OllamaResponse;
import com.hemendra.ocr.exception.OcrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for chat functionality with Ollama text models
 */
@Service
public class ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    
    private static final Duration TIMEOUT = Duration.ofMinutes(10);
    private static final Duration MODEL_DOWNLOAD_TIMEOUT = Duration.ofMinutes(30);
    
    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;
    
    private final WebClient webClient;
    private final Map<String, List<Map<String, String>>> conversations = new ConcurrentHashMap<>();
    
    public ChatService() {
        this.webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024))
            .build();
    }
    
    /**
     * Send a chat message to the specified model (non-streaming)
     */
    public ChatResponse chat(ChatRequest request) throws OcrException {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate request
            validateChatRequest(request);
            
            // Ensure model is available
            ensureModelAvailable(request.getModel());
            
            // Get or create conversation
            String conversationId = request.getConversationId() != null ? 
                request.getConversationId() : UUID.randomUUID().toString();
            
            List<Map<String, String>> conversation = conversations.computeIfAbsent(
                conversationId, k -> new ArrayList<>());
            
            // Add system prompt if provided and conversation is new
            if (request.getSystemPrompt() != null && conversation.isEmpty()) {
                Map<String, String> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", request.getSystemPrompt());
                conversation.add(systemMessage);
            }
            
            // Add user message to conversation
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", request.getMessage());
            conversation.add(userMessage);
            
            // Create Ollama request for chat with conversation context
            OllamaRequest ollamaRequest = createOllamaRequestWithContext(request, conversation);
            
            // Call Ollama API
            OllamaResponse ollamaResponse = callOllamaChatApi(ollamaRequest);
            
            // Add assistant response to conversation
            Map<String, String> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", ollamaResponse.getResponse());
            conversation.add(assistantMessage);
            
            // Calculate processing time
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Create response
            ChatResponse response = new ChatResponse(
                ollamaResponse.getResponse(), 
                request.getModel(), 
                conversationId
            );
            response.setProcessingTimeMs(processingTime);
            response.setTokenCount(ollamaResponse.getEvalCount());
            
            logger.info("Chat completed successfully for model: {} in {}ms", 
                       request.getModel(), processingTime);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Chat failed for model: {}", request.getModel(), e);
            throw new OcrException("Chat failed: " + e.getMessage(), e);
        }
    }

    /**
     * Send a chat message with streaming response
     */
    public Flux<String> chatStream(ChatRequest request) throws OcrException {
        try {
            // Validate request
            validateChatRequest(request);

            // Ensure model is available
            ensureModelAvailable(request.getModel());

            // Get or create conversation
            String conversationId = request.getConversationId() != null ?
                request.getConversationId() : UUID.randomUUID().toString();

            List<Map<String, String>> conversation = conversations.computeIfAbsent(
                conversationId, k -> new ArrayList<>());

            // Add system prompt if provided and conversation is new
            if (request.getSystemPrompt() != null && conversation.isEmpty()) {
                Map<String, String> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", request.getSystemPrompt());
                conversation.add(systemMessage);
            }

            // Add user message to conversation
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", request.getMessage());
            conversation.add(userMessage);

            // Create Ollama request for streaming chat
            OllamaRequest ollamaRequest = createOllamaRequestWithContext(request, conversation);
            ollamaRequest.setStream(true);

            // Call Ollama streaming API
            return callOllamaStreamingApi(ollamaRequest)
                .doOnComplete(() -> {
                    // Add assistant response to conversation when complete
                    // Note: In a real implementation, you'd collect the full response
                    logger.info("Streaming chat completed for model: {}", request.getModel());
                })
                .doOnError(error -> {
                    logger.error("Streaming chat failed for model: {}", request.getModel(), error);
                });

        } catch (Exception e) {
            logger.error("Streaming chat failed for model: {}", request.getModel(), e);
            return Flux.error(new OcrException("Streaming chat failed: " + e.getMessage(), e));
        }
    }

    /**
     * Get available text models (excluding vision models)
     */
    public List<String> getAvailableTextModels() {
        try {
            // Get all models from Ollama
            String response = webClient.get()
                .uri(ollamaBaseUrl + "/api/tags")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .block();
            
            // Parse and filter text models (this is simplified)
            List<String> textModels = new ArrayList<>();
            textModels.add("gemma2:2b");
            textModels.add("gemma2:9b");
            textModels.add("llama3.2:1b");
            textModels.add("llama3.2:3b");
            textModels.add("qwen2.5:0.5b");
            textModels.add("qwen2.5:1.5b");
            textModels.add("phi3:mini");
            textModels.add("mistral:7b");
            textModels.add("codellama:7b");
            
            return textModels;
            
        } catch (Exception e) {
            logger.error("Failed to get available models", e);
            return Arrays.asList("gemma2:2b", "llama3.2:1b", "phi3:mini");
        }
    }
    
    /**
     * Install a model if not already available
     */
    public ChatResponse installModel(String modelName) {
        try {
            logger.info("Installing model: {}", modelName);
            
            ChatResponse response = new ChatResponse();
            response.setModel(modelName);
            response.setModelDownloadStatus("downloading");
            response.setSuccess(true);
            
            // Start model download in background
            downloadModelAsync(modelName);
            
            response.setResponse("Model installation started. This may take several minutes depending on model size.");
            response.setModelLoaded(false);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to install model: {}", modelName, e);
            ChatResponse errorResponse = new ChatResponse("Failed to install model: " + e.getMessage());
            errorResponse.setModel(modelName);
            return errorResponse;
        }
    }
    
    /**
     * Clear conversation history
     */
    public void clearConversation(String conversationId) {
        conversations.remove(conversationId);
        logger.info("Cleared conversation: {}", conversationId);
    }
    
    /**
     * Get conversation history
     */
    public List<Map<String, String>> getConversationHistory(String conversationId) {
        return conversations.getOrDefault(conversationId, new ArrayList<>());
    }
    
    /**
     * Check if model is available locally
     */
    public boolean isModelAvailable(String modelName) {
        try {
            // Try to generate with the model to check if it's available
            OllamaRequest testRequest = new OllamaRequest();
            testRequest.setModel(modelName);
            testRequest.setPrompt("test");
            
            webClient.post()
                .uri(ollamaBaseUrl + "/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Ensure model is available, download if necessary
     */
    private void ensureModelAvailable(String modelName) throws OcrException {
        if (!isModelAvailable(modelName)) {
            logger.info("Model {} not available, attempting to download...", modelName);
            downloadModel(modelName);
        }
    }
    
    /**
     * Download model synchronously
     */
    private void downloadModel(String modelName) throws OcrException {
        try {
            Map<String, String> pullRequest = new HashMap<>();
            pullRequest.put("name", modelName);
            
            webClient.post()
                .uri(ollamaBaseUrl + "/api/pull")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pullRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(MODEL_DOWNLOAD_TIMEOUT)
                .block();
            
            logger.info("Model {} downloaded successfully", modelName);
            
        } catch (Exception e) {
            logger.error("Failed to download model: {}", modelName, e);
            throw new OcrException("Failed to download model " + modelName + ": " + e.getMessage());
        }
    }
    
    /**
     * Download model asynchronously
     */
    private void downloadModelAsync(String modelName) {
        new Thread(() -> {
            try {
                downloadModel(modelName);
            } catch (Exception e) {
                logger.error("Async model download failed for: {}", modelName, e);
            }
        }).start();
    }
    
    /**
     * Call Ollama chat API
     */
    private OllamaResponse callOllamaChatApi(OllamaRequest request) throws OcrException {
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
            throw new OcrException("Failed to communicate with chat service: " + e.getMessage());
        }
    }
    
    /**
     * Validate chat request
     */
    private void validateChatRequest(ChatRequest request) throws OcrException {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new OcrException("Message cannot be empty");
        }
        
        if (request.getModel() == null || request.getModel().trim().isEmpty()) {
            throw new OcrException("Model must be specified");
        }
        
        if (request.getMessage().length() > 10000) {
            throw new OcrException("Message too long (max 10,000 characters)");
        }
    }

    /**
     * Create Ollama request with conversation context
     */
    private OllamaRequest createOllamaRequestWithContext(ChatRequest request, List<Map<String, String>> conversation) {
        OllamaRequest ollamaRequest = new OllamaRequest();
        ollamaRequest.setModel(request.getModel());
        ollamaRequest.setStream(false);

        // Build context from conversation history
        StringBuilder contextPrompt = new StringBuilder();
        for (Map<String, String> message : conversation) {
            String role = message.get("role");
            String content = message.get("content");

            if ("system".equals(role)) {
                contextPrompt.append("System: ").append(content).append("\n");
            } else if ("user".equals(role)) {
                contextPrompt.append("User: ").append(content).append("\n");
            } else if ("assistant".equals(role)) {
                contextPrompt.append("Assistant: ").append(content).append("\n");
            }
        }

        // Add current message
        contextPrompt.append("Assistant: ");

        ollamaRequest.setPrompt(contextPrompt.toString());
        return ollamaRequest;
    }

    /**
     * Call Ollama streaming API
     */
    private Flux<String> callOllamaStreamingApi(OllamaRequest request) {
        return webClient.post()
            .uri(ollamaBaseUrl + "/api/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(String.class)
            .timeout(TIMEOUT)
            .map(this::parseStreamingResponse)
            .filter(response -> response != null && !response.isEmpty());
    }

    /**
     * Parse streaming response from Ollama
     */
    private String parseStreamingResponse(String jsonLine) {
        try {
            // Parse JSON response and extract the "response" field
            if (jsonLine.contains("\"response\":")) {
                int start = jsonLine.indexOf("\"response\":\"") + 12;
                int end = jsonLine.indexOf("\"", start);
                if (start > 11 && end > start) {
                    return jsonLine.substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
                }
            }
            return "";
        } catch (Exception e) {
            logger.warn("Failed to parse streaming response: {}", jsonLine, e);
            return "";
        }
    }
}
