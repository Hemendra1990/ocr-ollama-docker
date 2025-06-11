package com.hemendra.ocr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for Chat API
 */
public class ChatResponse {
    
    @JsonProperty("response")
    private String response;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("conversation_id")
    private String conversationId;
    
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("processing_time_ms")
    private Long processingTimeMs;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("error_message")
    private String errorMessage;
    
    @JsonProperty("token_count")
    private Integer tokenCount;
    
    @JsonProperty("model_loaded")
    private boolean modelLoaded;
    
    @JsonProperty("model_download_status")
    private String modelDownloadStatus;

    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
        this.messageId = java.util.UUID.randomUUID().toString();
    }

    public ChatResponse(String response, String model, String conversationId) {
        this();
        this.response = response;
        this.model = model;
        this.conversationId = conversationId;
        this.success = true;
        this.modelLoaded = true;
    }

    public ChatResponse(String errorMessage) {
        this();
        this.errorMessage = errorMessage;
        this.success = false;
    }

    // Getters and Setters
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }

    public boolean isModelLoaded() {
        return modelLoaded;
    }

    public void setModelLoaded(boolean modelLoaded) {
        this.modelLoaded = modelLoaded;
    }

    public String getModelDownloadStatus() {
        return modelDownloadStatus;
    }

    public void setModelDownloadStatus(String modelDownloadStatus) {
        this.modelDownloadStatus = modelDownloadStatus;
    }
}
