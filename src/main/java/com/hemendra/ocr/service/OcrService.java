package com.hemendra.ocr.service;

import com.hemendra.ocr.dto.OcrResponse;
import com.hemendra.ocr.exception.OcrException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Service class for OCR operations using Tesseract
 */
@Service
public class OcrService {
    
    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);
    
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
        "png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif"
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    private final ITesseract tesseract;
    
    public OcrService() {
        this.tesseract = new Tesseract();

        // Auto-detect tessdata path based on environment
        String tessDataPath = detectTessDataPath();
        if (tessDataPath != null) {
            tesseract.setDatapath(tessDataPath);
            logger.info("Using tessdata path: {}", tessDataPath);
        } else {
            logger.warn("Could not detect tessdata path, using default");
        }

        tesseract.setLanguage("eng"); // Default to English
    }

    /**
     * Auto-detect tessdata path based on the environment
     */
    private String detectTessDataPath() {
        // Common tessdata paths to check
        String[] possiblePaths = {
            "/usr/share/tesseract-ocr/4.00/tessdata",  // Ubuntu/Debian Docker
            "/usr/share/tesseract-ocr/tessdata",       // Alternative Ubuntu/Debian
            "/usr/share/tessdata",                     // Some Linux distributions
            "/opt/homebrew/share/tessdata",            // macOS Homebrew
            "/usr/local/share/tessdata",               // macOS alternative
            "C:\\Program Files\\Tesseract-OCR\\tessdata" // Windows
        };

        for (String path : possiblePaths) {
            java.io.File tessDataDir = new java.io.File(path);
            if (tessDataDir.exists() && tessDataDir.isDirectory()) {
                return path;
            }
        }

        // Check environment variable
        String envPath = System.getenv("TESSDATA_PREFIX");
        if (envPath != null) {
            java.io.File tessDataDir = new java.io.File(envPath);
            if (tessDataDir.exists() && tessDataDir.isDirectory()) {
                return envPath;
            }
        }

        return null;
    }
    
    /**
     * Extract text from uploaded image file
     */
    public OcrResponse extractTextFromImage(MultipartFile file) throws OcrException {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate file
            validateFile(file);
            
            // Convert MultipartFile to BufferedImage
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new OcrException("Unable to read image file. Please ensure it's a valid image format.");
            }
            
            // Perform OCR
            String extractedText = tesseract.doOCR(image);
            
            // Calculate processing time
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Create response
            OcrResponse response = new OcrResponse(extractedText, file.getOriginalFilename(), file.getSize());
            response.setProcessingTimeMs(processingTime);
            
            logger.info("OCR completed successfully for file: {} in {}ms", 
                       file.getOriginalFilename(), processingTime);
            
            return response;
            
        } catch (TesseractException e) {
            logger.error("Tesseract OCR failed for file: {}", file.getOriginalFilename(), e);
            throw new OcrException("OCR processing failed: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("IO error while processing file: {}", file.getOriginalFilename(), e);
            throw new OcrException("Error reading image file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract text with specific language
     */
    public OcrResponse extractTextFromImage(MultipartFile file, String language) throws OcrException {
        // Store the current language (default is "eng")
        String originalLanguage = "eng";
        try {
            tesseract.setLanguage(language);
            return extractTextFromImage(file);
        } finally {
            // Reset to original language
            tesseract.setLanguage(originalLanguage);
        }
    }
    
    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) throws OcrException {
        if (file == null || file.isEmpty()) {
            throw new OcrException("No file uploaded or file is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new OcrException("File size exceeds maximum limit of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new OcrException("Invalid filename");
        }
        
        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(extension)) {
            throw new OcrException("Unsupported file format. Supported formats: " + SUPPORTED_FORMATS);
        }
    }
    
    /**
     * Get list of supported image formats
     */
    public List<String> getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }
}
