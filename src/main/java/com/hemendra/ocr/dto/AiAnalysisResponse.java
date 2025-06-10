package com.hemendra.ocr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for AI image analysis
 */
public class AiAnalysisResponse {
    
    @JsonProperty("analysis")
    private String analysis;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("prompt")
    private String prompt;
    
    @JsonProperty("processing_time_ms")
    private Long processingTimeMs;
    
    @JsonProperty("file_name")
    private String fileName;
    
    @JsonProperty("file_size")
    private Long fileSize;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("error_message")
    private String errorMessage;
    
    @JsonProperty("ollama_stats")
    private OllamaStats ollamaStats;

    public AiAnalysisResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public AiAnalysisResponse(String analysis, String model, String fileName, Long fileSize) {
        this();
        this.analysis = analysis;
        this.model = model;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.success = true;
    }

    public AiAnalysisResponse(String errorMessage) {
        this();
        this.errorMessage = errorMessage;
        this.success = false;
    }

    // Getters and Setters
    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

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

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

    public OllamaStats getOllamaStats() {
        return ollamaStats;
    }

    public void setOllamaStats(OllamaStats ollamaStats) {
        this.ollamaStats = ollamaStats;
    }

    /**
     * Ollama performance statistics
     */
    public static class OllamaStats {
        @JsonProperty("total_duration_ms")
        private Long totalDurationMs;
        
        @JsonProperty("load_duration_ms")
        private Long loadDurationMs;
        
        @JsonProperty("eval_count")
        private Integer evalCount;
        
        @JsonProperty("eval_duration_ms")
        private Long evalDurationMs;

        public Long getTotalDurationMs() {
            return totalDurationMs;
        }

        public void setTotalDurationMs(Long totalDurationMs) {
            this.totalDurationMs = totalDurationMs;
        }

        public Long getLoadDurationMs() {
            return loadDurationMs;
        }

        public void setLoadDurationMs(Long loadDurationMs) {
            this.loadDurationMs = loadDurationMs;
        }

        public Integer getEvalCount() {
            return evalCount;
        }

        public void setEvalCount(Integer evalCount) {
            this.evalCount = evalCount;
        }

        public Long getEvalDurationMs() {
            return evalDurationMs;
        }

        public void setEvalDurationMs(Long evalDurationMs) {
            this.evalDurationMs = evalDurationMs;
        }
    }
}
