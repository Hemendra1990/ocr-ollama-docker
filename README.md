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

### **Chat Capabilities**
- **Text Conversation**: Chat with various AI text models (Gemma, Llama, Phi-3, etc.)
- **Streaming Responses**: Real-time streaming chat responses
- **Conversation Memory**: Maintains context across conversation turns
- **Auto Model Installation**: Automatically download any Ollama-compatible model
- **System Prompts**: Set custom AI personality and behavior
- **Multiple Models**: Support for 20+ different text models

### **Technical Features**
- **REST API**: Clean RESTful endpoints for OCR, AI, and Chat
- **Dual Web Interface**: Main page for OCR/AI + dedicated chat interface
- **Streaming Support**: Real-time streaming responses for chat
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

### **Chat Endpoints**

### 8. Send Chat Message (Regular)
**POST** `/api/chat/message`

Send a message to an AI text model and get a complete response.

**Parameters:**
- `message` (string): The message to send
- `model` (string): AI model to use (e.g., "gemma2:2b")
- `conversation_id` (optional): Conversation ID for context
- `system_prompt` (optional): System instructions for the AI

**Example using curl:**
```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"message":"Hello! How are you?","model":"gemma2:2b"}' \
  http://localhost:8080/api/chat/message
```

**Response:**
```json
{
  "response": "I am an AI assistant, so I don't have feelings...",
  "model": "gemma2:2b",
  "conversation_id": "76f014fa-91ff-4ad7-a44f-fd0346825217",
  "processing_time_ms": 10107,
  "success": true,
  "token_count": 39
}
```

### 9. Send Chat Message (Streaming)
**POST** `/api/chat/stream`

Send a message to an AI text model and get a streaming response.

**Parameters:** Same as regular chat message

**Example using curl:**
```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"message":"Tell me a story","model":"gemma2:2b"}' \
  http://localhost:8080/api/chat/stream
```

### 10. Install AI Model
**POST** `/api/chat/install-model`

Install a new AI model from Ollama's library.

**Parameters:**
- `model` (string): Model name to install (e.g., "llama3.2:1b")

**Example using curl:**
```bash
curl -X POST -d "model=llama3.2:1b" \
  http://localhost:8080/api/chat/install-model
```

### 11. Check Model Status
**GET** `/api/chat/model-status/{model}`

Check if a specific model is available locally.

**Example using curl:**
```bash
curl -X GET http://localhost:8080/api/chat/model-status/gemma2:2b
```

**Response:**
```json
{
  "model": "gemma2:2b",
  "available": true,
  "status": "ready"
}
```

### 12. Available Chat Models
**GET** `/api/chat/models`

Get information about available text models.

**Response:**
```json
{
  "available_models": ["gemma2:2b", "llama3.2:1b", "phi3:mini"],
  "default_model": "gemma2:2b",
  "recommended_models": ["gemma2:2b", "llama3.2:1b", "phi3:mini"]
}
```

### 13. Chat Service Health Check
**GET** `/api/chat/health`

Check if the chat service is running.

**Response:**
```json
{
  "status": "UP",
  "service": "Chat Service",
  "models_available": true,
  "model_count": 9,
  "timestamp": 1749577676083
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

## Web Interfaces

### **Main Interface** - http://localhost:8080
- OCR text extraction from images
- AI image analysis with vision models
- Custom prompts for AI analysis
- File upload with drag & drop support

### **Chat Interface** - http://localhost:8080/chat.html
- Real-time chat with AI text models
- Streaming and regular response modes
- Conversation memory and history
- Auto model installation
- System prompt configuration
- Beautiful modern UI with animations

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
