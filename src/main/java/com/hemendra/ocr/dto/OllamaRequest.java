package com.hemendra.ocr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request DTO for Ollama API
 */
public class OllamaRequest {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("prompt")
    private String prompt;
    
    @JsonProperty("images")
    private List<String> images;
    
    @JsonProperty("stream")
    private boolean stream = false;
    
    @JsonProperty("options")
    private OllamaOptions options;

    public OllamaRequest() {}

    public OllamaRequest(String model, String prompt, List<String> images) {
        this.model = model;
        this.prompt = prompt;
        this.images = images;
        this.options = new OllamaOptions();
    }

    // Getters and Setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public OllamaOptions getOptions() {
        return options;
    }

    public void setOptions(OllamaOptions options) {
        this.options = options;
    }

    /**
     * Ollama request options
     */
    public static class OllamaOptions {
        @JsonProperty("temperature")
        private double temperature = 0.7;
        
        @JsonProperty("top_p")
        private double topP = 0.9;
        
        @JsonProperty("top_k")
        private int topK = 40;

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public double getTopP() {
            return topP;
        }

        public void setTopP(double topP) {
            this.topP = topP;
        }

        public int getTopK() {
            return topK;
        }

        public void setTopK(int topK) {
            this.topK = topK;
        }
    }
}
