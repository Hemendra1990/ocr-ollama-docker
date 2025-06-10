package com.hemendra.ocr.service;

import com.hemendra.ocr.dto.OcrResponse;
import com.hemendra.ocr.exception.OcrException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class OcrServiceTest {

    private OcrService ocrService;

    @BeforeEach
    void setUp() {
        ocrService = new OcrService();
    }

    @Test
    void testValidateFile_EmptyFile() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "image/png", new byte[0]);

        // Act & Assert
        OcrException exception = assertThrows(OcrException.class, () -> {
            ocrService.extractTextFromImage(emptyFile);
        });
        
        assertTrue(exception.getMessage().contains("empty"));
    }

    @Test
    void testValidateFile_UnsupportedFormat() {
        // Arrange
        MockMultipartFile unsupportedFile = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            "some content".getBytes()
        );

        // Act & Assert
        OcrException exception = assertThrows(OcrException.class, () -> {
            ocrService.extractTextFromImage(unsupportedFile);
        });
        
        assertTrue(exception.getMessage().contains("Unsupported file format"));
    }

    @Test
    void testValidateFile_FileTooLarge() {
        // Arrange
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile largeFile = new MockMultipartFile(
            "file", 
            "large.png", 
            "image/png", 
            largeContent
        );

        // Act & Assert
        OcrException exception = assertThrows(OcrException.class, () -> {
            ocrService.extractTextFromImage(largeFile);
        });
        
        assertTrue(exception.getMessage().contains("exceeds maximum limit"));
    }

    @Test
    void testGetSupportedFormats() {
        // Act
        var formats = ocrService.getSupportedFormats();

        // Assert
        assertNotNull(formats);
        assertFalse(formats.isEmpty());
        assertTrue(formats.contains("png"));
        assertTrue(formats.contains("jpg"));
        assertTrue(formats.contains("jpeg"));
    }
}
