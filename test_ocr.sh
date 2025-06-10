#!/bin/bash

# Test script for OCR service
echo "Testing OCR Service..."

# Test 1: Health check
echo "1. Testing health endpoint..."
curl -s http://localhost:8080/api/ocr/health | jq '.'

echo -e "\n2. Testing supported formats endpoint..."
curl -s http://localhost:8080/api/ocr/formats | jq '.'

# Test 3: OCR with a sample image (if you have one)
echo -e "\n3. To test OCR with an image, use:"
echo "curl -X POST -F \"file=@your-image.png\" -F \"language=eng\" http://localhost:8080/api/ocr/extract"

echo -e "\n4. Or visit http://localhost:8080 to use the web interface"

echo -e "\nOCR Service is ready! 🎉"
