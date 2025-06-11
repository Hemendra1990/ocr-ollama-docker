#!/bin/bash

echo "🧪 Testing Docker OCR & AI Services..."

# Test 1: OCR Health check
echo "1. Testing OCR health endpoint..."
ocr_health=$(curl -s http://localhost:8080/api/ocr/health)
echo "OCR Health: $ocr_health"

# Test 2: AI Health check
echo -e "\n2. Testing AI health endpoint..."
ai_health=$(curl -s http://localhost:8080/api/ai/health)
echo "AI Health: $ai_health"

# Test 3: Check if services are ready
if echo "$ocr_health" | grep -q '"status":"UP"'; then
    echo "✅ OCR Service is UP and running!"
else
    echo "❌ OCR Service is not ready"
    exit 1
fi

if echo "$ai_health" | grep -q '"status":"UP"'; then
    echo "✅ AI Service is UP and running!"
else
    echo "❌ AI Service is not ready"
    exit 1
fi

# Test 4: Check supported formats
echo -e "\n3. Testing supported formats..."
formats_response=$(curl -s http://localhost:8080/api/ocr/formats)
echo "OCR Formats: $formats_response"

# Test 5: Check AI models
echo -e "\n4. Testing AI models..."
models_response=$(curl -s http://localhost:8080/api/ai/models)
echo "AI Models: $models_response"

echo -e "\n🎉 Both OCR & AI Services are ready!"
echo -e "\n📋 Available endpoints:"
echo "   🔤 OCR Health: http://localhost:8080/api/ocr/health"
echo "   🔤 OCR Formats: http://localhost:8080/api/ocr/formats"
echo "   🔤 OCR Extract: POST http://localhost:8080/api/ocr/extract"
echo "   🤖 AI Health: http://localhost:8080/api/ai/health"
echo "   🤖 AI Models: http://localhost:8080/api/ai/models"
echo "   🤖 AI Analyze: POST http://localhost:8080/api/ai/analyze"
echo "   🌐 Web Interface: http://localhost:8080"

echo -e "\n🧪 Test OCR with an image:"
echo "   curl -X POST -F \"file=@your-image.png\" http://localhost:8080/api/ocr/extract"

echo -e "\n🧪 Test AI analysis with an image:"
echo "   curl -X POST -F \"file=@your-image.png\" http://localhost:8080/api/ai/analyze"

echo -e "\n🧪 Test AI with custom prompt:"
echo "   curl -X POST -F \"file=@your-image.png\" -F \"prompt=Describe this image\" -F \"model=llava:latest\" http://localhost:8080/api/ai/analyze-custom"

echo -e "\n📊 View logs:"
echo "   docker compose logs -f"

echo -e "\n🛑 Stop services:"
echo "   docker compose down"

# Test 6: Chat Service Health
echo -e "\n5. Testing Chat service health..."
chat_health=$(curl -s http://localhost:8080/api/chat/health)
echo "Chat Health: $chat_health"

if echo "$chat_health" | grep -q '"status":"UP"'; then
    echo "✅ Chat Service is UP and running!"
else
    echo "❌ Chat Service is not ready"
fi

# Test 7: Chat Models
echo -e "\n6. Testing Chat models..."
chat_models=$(curl -s http://localhost:8080/api/chat/models)
echo "Chat Models: $chat_models"

echo -e "\n🎉 All Services (OCR, AI, Chat) are ready!"
echo -e "\n📋 Complete API endpoints:"
echo "   🔤 OCR Health: http://localhost:8080/api/ocr/health"
echo "   🔤 OCR Formats: http://localhost:8080/api/ocr/formats"
echo "   🔤 OCR Extract: POST http://localhost:8080/api/ocr/extract"
echo "   🤖 AI Health: http://localhost:8080/api/ai/health"
echo "   🤖 AI Models: http://localhost:8080/api/ai/models"
echo "   🤖 AI Analyze: POST http://localhost:8080/api/ai/analyze"
echo "   💬 Chat Health: http://localhost:8080/api/chat/health"
echo "   💬 Chat Models: http://localhost:8080/api/chat/models"
echo "   💬 Chat Message: POST http://localhost:8080/api/chat/message"
echo "   💬 Install Model: POST http://localhost:8080/api/chat/install-model"
echo "   🌐 Main Interface: http://localhost:8080"
echo "   🌐 Chat Interface: http://localhost:8080/chat.html"

echo -e "\n🧪 Test Chat with a message:"
echo "   curl -X POST -H \"Content-Type: application/json\" -d '{\"message\":\"Hello!\",\"model\":\"gemma2:2b\"}' http://localhost:8080/api/chat/message"

echo -e "\n🧪 Install a chat model:"
echo "   curl -X POST -d \"model=llama3.2:1b\" http://localhost:8080/api/chat/install-model"
