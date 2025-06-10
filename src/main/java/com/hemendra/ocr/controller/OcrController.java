package com.hemendra.ocr.controller;

import com.hemendra.ocr.dto.OcrResponse;
import com.hemendra.ocr.exception.OcrException;
import com.hemendra.ocr.service.OcrService;
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
 * REST Controller for OCR operations
 */
@RestController
@RequestMapping("/api/ocr")
@CrossOrigin(origins = "*") // Allow CORS for frontend integration
public class OcrController {
    
    private static final Logger logger = LoggerFactory.getLogger(OcrController.class);
    
    @Autowired
    private OcrService ocrService;
    
    /**
     * Extract text from uploaded image
     */
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcrResponse> extractText(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", defaultValue = "eng") String language) {
        
        logger.info("Received OCR request for file: {} with language: {}", 
                   file.getOriginalFilename(), language);
        
        try {
            OcrResponse response = ocrService.extractTextFromImage(file, language);
            return ResponseEntity.ok(response);
            
        } catch (OcrException e) {
            logger.error("OCR processing failed: {}", e.getMessage());
            OcrResponse errorResponse = new OcrResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Unexpected error during OCR processing", e);
            OcrResponse errorResponse = new OcrResponse("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "OCR Service");
        health.put("supported_formats", ocrService.getSupportedFormats());
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Get supported image formats
     */
    @GetMapping("/formats")
    public ResponseEntity<Map<String, Object>> getSupportedFormats() {
        Map<String, Object> response = new HashMap<>();
        response.put("supported_formats", ocrService.getSupportedFormats());
        response.put("max_file_size_mb", 10);
        
        return ResponseEntity.ok(response);
    }
}
