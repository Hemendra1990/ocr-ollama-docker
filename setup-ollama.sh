#!/bin/bash

echo "🤖 Setting up Ollama with LLaVA vision model..."

# Function to check if Ollama is running
check_ollama() {
    curl -s http://localhost:11434/api/tags > /dev/null 2>&1
    return $?
}

# Wait for Ollama to be ready
echo "⏳ Waiting for Ollama service to start..."
max_attempts=30
attempt=1

while [ $attempt -le $max_attempts ]; do
    if check_ollama; then
        echo "✅ Ollama service is running!"
        break
    fi
    
    echo "Attempt $attempt/$max_attempts - Ollama not ready yet..."
    sleep 10
    attempt=$((attempt + 1))
done

if [ $attempt -gt $max_attempts ]; then
    echo "❌ Ollama service failed to start after 5 minutes"
    echo "Please check the logs: docker compose logs ollama"
    exit 1
fi

# Pull the LLaVA vision model
echo "📥 Pulling LLaVA vision model (this may take several minutes)..."
docker compose exec ollama ollama pull llava:latest

if [ $? -eq 0 ]; then
    echo "✅ LLaVA vision model downloaded successfully!"
else
    echo "❌ Failed to download LLaVA model"
    exit 1
fi

# Optionally pull other models
echo "📥 Pulling additional vision models..."
echo "Pulling LLaVA 7B (larger vision model)..."
docker compose exec ollama ollama pull llava:7b

echo "📋 Available models:"
docker compose exec ollama ollama list

echo "🎉 Ollama setup complete!"
echo ""
echo "🧪 Test the AI service:"
echo "  curl http://localhost:8080/api/ai/health"
echo ""
echo "🌐 Web interface with AI analysis:"
echo "  http://localhost:8080"
