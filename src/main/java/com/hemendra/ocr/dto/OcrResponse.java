package com.hemendra.ocr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for OCR operations
 */
public class OcrResponse {
    
    @JsonProperty("extracted_text")
    private String extractedText;
    
    @JsonProperty("confidence")
    private Double confidence;
    
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

    public OcrResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public OcrResponse(String extractedText, String fileName, Long fileSize) {
        this();
        this.extractedText = extractedText;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.success = true;
    }

    public OcrResponse(String errorMessage) {
        this();
        this.errorMessage = errorMessage;
        this.success = false;
    }

    // Getters and Setters
    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
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
}
