#!/bin/bash

echo "🚀 Starting OCR & AI Services with Docker..."

# Start the services
docker compose up -d

echo "⏳ Waiting for services to start..."
sleep 15

# Check if OCR service is running
if curl -s http://localhost:8080/api/ocr/health > /dev/null; then
    echo "✅ OCR Service is running!"
    echo ""
    echo "🌐 Web Interface: http://localhost:8080"
    echo "📡 OCR Health: http://localhost:8080/api/ocr/health"
    echo "🤖 AI Health: http://localhost:8080/api/ai/health"
    echo "📋 API Docs: http://localhost:8080/api/ocr/formats"
    echo ""
    echo "🔧 Setup AI models:"
    echo "  ./setup-ollama.sh"
    echo ""
    echo "📊 View logs: docker compose logs -f"
    echo "🛑 Stop services: docker compose down"
    echo ""
    echo "🧪 Test OCR:"
    echo "  curl -X POST -F \"file=@your-image.png\" http://localhost:8080/api/ocr/extract"
    echo ""
    echo "🧪 Test AI Analysis:"
    echo "  curl -X POST -F \"file=@your-image.png\" http://localhost:8080/api/ai/analyze"
else
    echo "❌ Services failed to start. Check logs:"
    echo "docker compose logs"
fi
