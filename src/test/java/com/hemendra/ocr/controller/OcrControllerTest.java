package com.hemendra.ocr.controller;

import com.hemendra.ocr.dto.OcrResponse;
import com.hemendra.ocr.exception.OcrException;
import com.hemendra.ocr.service.OcrService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OcrController.class)
class OcrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OcrService ocrService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testExtractText_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.png", 
            "image/png", 
            "test image content".getBytes()
        );
        
        OcrResponse mockResponse = new OcrResponse("Extracted text", "test.png", 1024L);
        mockResponse.setProcessingTimeMs(500L);
        
        when(ocrService.extractTextFromImage(any(), eq("eng"))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(multipart("/api/ocr/extract")
                .file(file)
                .param("language", "eng"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extracted_text").value("Extracted text"))
                .andExpect(jsonPath("$.file_name").value("test.png"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testExtractText_OcrException() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.png", 
            "image/png", 
            "test image content".getBytes()
        );
        
        when(ocrService.extractTextFromImage(any(), eq("eng")))
            .thenThrow(new OcrException("OCR processing failed"));

        // Act & Assert
        mockMvc.perform(multipart("/api/ocr/extract")
                .file(file)
                .param("language", "eng"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error_message").value("OCR processing failed"));
    }

    @Test
    void testHealthCheck() throws Exception {
        // Arrange
        when(ocrService.getSupportedFormats())
            .thenReturn(Arrays.asList("png", "jpg", "jpeg"));

        // Act & Assert
        mockMvc.perform(get("/api/ocr/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("OCR Service"))
                .andExpect(jsonPath("$.supported_formats").isArray());
    }

    @Test
    void testGetSupportedFormats() throws Exception {
        // Arrange
        when(ocrService.getSupportedFormats())
            .thenReturn(Arrays.asList("png", "jpg", "jpeg"));

        // Act & Assert
        mockMvc.perform(get("/api/ocr/formats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supported_formats").isArray())
                .andExpect(jsonPath("$.max_file_size_mb").value(10));
    }
}
