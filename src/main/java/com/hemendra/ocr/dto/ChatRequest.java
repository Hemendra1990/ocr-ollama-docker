package com.hemendra.ocr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for Chat API
 */
public class ChatRequest {
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("conversation_id")
    private String conversationId;
    
    @JsonProperty("system_prompt")
    private String systemPrompt;

    public ChatRequest() {}

    public ChatRequest(String message, String model) {
        this.message = message;
        this.model = model;
    }

    public ChatRequest(String message, String model, String conversationId) {
        this.message = message;
        this.model = model;
        this.conversationId = conversationId;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}
