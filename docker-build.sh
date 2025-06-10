#!/bin/bash

echo "🐳 Building OCR Service Docker Image..."

# Stop any running containers
echo "Stopping existing containers..."
docker compose down

# Build the image
echo "Building Docker image..."
docker compose build

echo "✅ Docker images built successfully!"
echo ""
echo "To run the services:"
echo "  ./docker-run.sh"
echo ""
echo "Or manually:"
echo "  docker compose up -d"
echo "  ./setup-ollama.sh  # Run this after services are up"
