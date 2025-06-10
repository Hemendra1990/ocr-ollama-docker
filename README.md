# OCR Service

A Spring Boot application that provides OCR (Optical Character Recognition) functionality using Tesseract. Upload images and extract text from them via REST API endpoints.

## Features

### **OCR Capabilities**
- **Text Extraction**: Extract text from images using Tesseract OCR
- **Multiple Languages**: Support for English, Spanish, French, German, and more
- **Multiple Formats**: PNG, JPG, JPEG, GIF, BMP, TIFF support
- **File Size Limits**: Up to 10MB for OCR processing

### **AI Analysis Capabilities**
- **Image Analysis**: Analyze images using LLaVA vision models
- **Custom Prompts**: Use custom prompts for specific analysis tasks
- **Multiple Models**: LLaVA latest, 7B, and 13B models available
- **Vision Understanding**: Describe objects, text, scenes, and compositions
- **File Size Limits**: Up to 50MB for AI analysis

### **Technical Features**
- **REST API**: Clean RESTful endpoints for both OCR and AI
- **Web Interface**: Unified web UI for OCR and AI analysis
- **Docker Support**: Complete containerization with Ollama integration
- **Error Handling**: Comprehensive error handling and validation
- **Health Monitoring**: Built-in health check endpoints
- **Performance Metrics**: Processing time and token usage tracking

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Tesseract OCR engine

### Installing Tesseract OCR

**macOS (using Homebrew):**
```bash
brew install tesseract
```

**Ubuntu/Debian:**
```bash
sudo apt-get install tesseract-ocr
```

**Windows:**
Download from: https://github.com/UB-Mannheim/tesseract/wiki

**Note:** The application is configured for macOS Homebrew installation. If you're using a different OS, update the tessdata path in `OcrService.java`.

## Quick Start

### **Option 1: Docker (Recommended)**

1. **Build and run with Docker**:
   ```bash
   ./docker-build.sh
   ./docker-run.sh
   ./setup-ollama.sh  # Setup AI models
   ```

2. **Or manually**:
   ```bash
   docker compose build
   docker compose up -d
   ./setup-ollama.sh  # Setup AI models
   ```

3. **Test the services**:
   ```bash
   ./test-docker-ocr.sh
   ```

### **Option 2: Local Installation**

1. **Install Tesseract OCR** (see Prerequisites section)
2. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

### **Access the Application**:
- Web Interface: http://localhost:8080
- OCR API: http://localhost:8080/api/ocr
- AI Analysis API: http://localhost:8080/api/ai

## API Endpoints

### **OCR Endpoints**

### 1. Extract Text from Image
**POST** `/api/ocr/extract`

Upload an image file and extract text from it.

**Parameters:**
- `file` (multipart/form-data): Image file to process
- `language` (optional): Language code (default: "eng")

**Example using curl:**
```bash
curl -X POST \
  -F "file=@your-image.png" \
  -F "language=eng" \
  http://localhost:8080/api/ocr/extract
```

**Response:**
```json
{
  "extracted_text": "Text found in the image",
  "file_name": "your-image.png",
  "file_size": 12345,
  "processing_time_ms": 1500,
  "timestamp": "2025-06-10T20:40:52.347",
  "success": true
}
```

### 2. Health Check
**GET** `/api/ocr/health`

Check if the OCR service is running.

**Response:**
```json
{
  "status": "UP",
  "service": "OCR Service",
  "supported_formats": ["png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif"],
  "timestamp": 1749568282137
}
```

### 3. Supported Formats
**GET** `/api/ocr/formats`

Get information about supported file formats and limits.

**Response:**
```json
{
  "supported_formats": ["png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif"],
  "max_file_size_mb": 10
}
```

### **AI Analysis Endpoints**

### 4. Analyze Image with AI
**POST** `/api/ai/analyze`

Analyze an image using the default LLaVA vision model.

**Parameters:**
- `file` (multipart/form-data): Image file to analyze

**Example using curl:**
```bash
curl -X POST \
  -F "file=@your-image.png" \
  http://localhost:8080/api/ai/analyze
```

### 5. Analyze Image with Custom Prompt
**POST** `/api/ai/analyze-custom`

Analyze an image using a custom prompt and model.

**Parameters:**
- `file` (multipart/form-data): Image file to analyze
- `prompt` (string): Custom prompt for analysis
- `model` (optional): AI model to use (default: "llava:latest")

**Example using curl:**
```bash
curl -X POST \
  -F "file=@your-image.png" \
  -F "prompt=Describe the text and objects in this image" \
  -F "model=llava:latest" \
  http://localhost:8080/api/ai/analyze-custom
```

**Response:**
```json
{
  "analysis": "This image shows a smartphone displaying a WhatsApp conversation...",
  "model": "llava:latest",
  "prompt": "Describe the text and objects in this image",
  "processing_time_ms": 3386,
  "file_name": "your-image.png",
  "file_size": 239789,
  "timestamp": "2025-06-10T20:40:52.347",
  "success": true,
  "ollama_stats": {
    "total_duration_ms": 3363,
    "eval_count": 41,
    "eval_duration_ms": 2890
  }
}
```

### 6. AI Service Health Check
**GET** `/api/ai/health`

Check if the AI analysis service is running.

**Response:**
```json
{
  "status": "UP",
  "service": "AI Analysis Service",
  "ollama_available": true,
  "available_models": ["llava:latest", "llava:7b", "llava:13b"],
  "timestamp": 1749574323273
}
```

### 7. Available AI Models
**GET** `/api/ai/models`

Get information about available AI models.

**Response:**
```json
{
  "available_models": ["llava:latest", "llava:7b", "llava:13b"],
  "default_model": "llava:latest",
  "max_file_size_mb": 50
}
```

## Supported Languages

The service supports multiple languages. Common language codes:
- `eng` - English (default)
- `spa` - Spanish
- `fra` - French
- `deu` - German
- `chi_sim` - Chinese Simplified
- `jpn` - Japanese

## Configuration

The application can be configured via `application.properties`:

```properties
# Server configuration
server.port=8080

# File upload limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.hemendra.ocr=INFO
```

## Error Handling

The API returns appropriate HTTP status codes and error messages:

- **400 Bad Request**: Invalid file format, file too large, or OCR processing failed
- **500 Internal Server Error**: Unexpected server errors

**Error Response Format:**
```json
{
  "success": false,
  "error_message": "Description of the error",
  "timestamp": "2025-06-10T20:40:52.347"
}
```

## Testing

Run the test suite:
```bash
./mvnw test
```

## Web Interface

Access the web interface at http://localhost:8080 for a simple upload form to test the OCR functionality.

## Docker Commands

**Build the image**:
```bash
./docker-build.sh
# or manually: docker compose build
```

**Run the service**:
```bash
./docker-run.sh
# or manually: docker compose up -d
```

**View logs**:
```bash
docker compose logs -f
```

**Stop the service**:
```bash
docker compose down
```

**Test OCR with Docker**:
```bash
curl -X POST -F "file=@your-image.png" http://localhost:8080/api/ocr/extract
```

## Building for Production

**Docker (Recommended)**:
```bash
docker compose build
docker compose up -d
```

**Local JAR**:
```bash
./mvnw clean package
java -jar target/ocr-0.0.1-SNAPSHOT.jar
```

## Architecture

- **Controller Layer**: `OcrController` - Handles HTTP requests
- **Service Layer**: `OcrService` - Business logic and Tesseract integration
- **DTO Layer**: `OcrResponse` - Data transfer objects
- **Exception Handling**: Custom `OcrException` for OCR-specific errors

## Troubleshooting

### Common Issues

**1. UnsatisfiedLinkError: Unable to load library 'tesseract'**
- **Solution**: Install Tesseract OCR engine on your system
- **macOS**: `brew install tesseract`
- **Ubuntu**: `sudo apt-get install tesseract-ocr`

**2. Wrong tessdata path**
- **Solution**: Update the tessdata path in `OcrService.java` constructor:
  ```java
  // For macOS Homebrew
  tesseract.setDatapath("/opt/homebrew/share/tessdata");

  // For Ubuntu/Linux
  tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");

  // For Windows
  tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
  ```

**3. Language not found**
- **Solution**: Install additional language packs
- **macOS**: `brew install tesseract-lang`
- **Ubuntu**: `sudo apt-get install tesseract-ocr-[lang]`

## Dependencies

- Spring Boot 3.5.0
- Tesseract4J 5.9.0 (Java wrapper for Tesseract)
- Apache Commons IO 2.11.0
- JUnit 5 (for testing)
