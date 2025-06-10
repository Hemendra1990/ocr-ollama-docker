package com.hemendra.ocr.exception;

/**
 * Custom exception for OCR-related errors
 */
public class OcrException extends Exception {
    
    public OcrException(String message) {
        super(message);
    }
    
    public OcrException(String message, Throwable cause) {
        super(message, cause);
    }
}
