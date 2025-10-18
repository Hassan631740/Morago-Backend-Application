#!/bin/bash

# JWT Secret Generator Script
# This script generates secure JWT secrets for different environments

echo "🔐 JWT Secret Generator"
echo "======================"
echo ""

# Generate secrets for different environments
echo "Local Development JWT Secret:"
echo "SECURITY_JWT_SECRET=$(openssl rand -base64 32)"
echo "jwt.secret=$(openssl rand -base64 32)"
echo ""

echo "Development Environment JWT Secret:"
echo "SECURITY_JWT_SECRET=$(openssl rand -base64 32)"
echo "jwt.secret=$(openssl rand -base64 32)"
echo ""

echo "Production Environment JWT Secret:"
echo "SECURITY_JWT_SECRET=$(openssl rand -base64 32)"
echo "jwt.secret=$(openssl rand -base64 32)"
echo ""

echo "⚠️  IMPORTANT SECURITY NOTES:"
echo "- Use DIFFERENT secrets for each environment"
echo "- Store these secrets securely (environment variables, secret management)"
echo "- NEVER commit these secrets to version control"
echo "- Rotate secrets regularly"
echo ""

echo "📝 To use these secrets:"
echo "1. Copy the appropriate secret to your .env file"
echo "2. Set the same value for both SECURITY_JWT_SECRET and jwt.secret"
echo "3. Restart your application"
