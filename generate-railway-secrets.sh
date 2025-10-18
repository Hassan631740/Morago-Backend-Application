#!/bin/bash

# ====================================================================
# Railway Secrets Generator
# ====================================================================
# This script generates secure secrets for Railway deployment
# Run: chmod +x generate-railway-secrets.sh && ./generate-railway-secrets.sh
# ====================================================================

echo "=================================================="
echo "🔐 Railway Secrets Generator"
echo "=================================================="
echo ""

# Check if openssl is available
if ! command -v openssl &> /dev/null; then
    echo "❌ Error: openssl is not installed"
    echo "Please install openssl first"
    exit 1
fi

echo "Generating secure secrets for Railway deployment..."
echo ""

# Generate JWT Secret
echo "=================================================="
echo "1️⃣  JWT SECRET (SECURITY_JWT_SECRET)"
echo "=================================================="
JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
echo "$JWT_SECRET"
echo ""
echo "✅ Copy this value for SECURITY_JWT_SECRET in Railway"
echo ""

# Generate another secret for backup
echo "=================================================="
echo "2️⃣  BACKUP JWT SECRET (optional)"
echo "=================================================="
JWT_SECRET_BACKUP=$(openssl rand -base64 64 | tr -d '\n')
echo "$JWT_SECRET_BACKUP"
echo ""
echo "💡 Keep this as backup in case you need to rotate secrets"
echo ""

# Generate random password for admin (if needed)
echo "=================================================="
echo "3️⃣  RANDOM ADMIN PASSWORD (optional)"
echo "=================================================="
ADMIN_PASSWORD=$(openssl rand -base64 32 | tr -d '\n')
echo "$ADMIN_PASSWORD"
echo ""
echo "💡 Use this for admin accounts if needed"
echo ""

# Display environment variables template
echo "=================================================="
echo "📋 COMPLETE ENVIRONMENT VARIABLES FOR RAILWAY"
echo "=================================================="
echo ""
echo "Copy these to Railway → Your Project → Variables:"
echo ""
echo "# Spring Configuration"
echo "SPRING_PROFILES_ACTIVE=prod"
echo ""
echo "# Database (Railway MySQL - use reference variables)"
echo "DB_URL=jdbc:mysql://\${{MYSQLHOST}}:\${{MYSQLPORT}}/\${{MYSQLDATABASE}}?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC"
echo "DB_USERNAME=\${{MYSQLUSER}}"
echo "DB_PASSWORD=\${{MYSQLPASSWORD}}"
echo ""
echo "# JWT Configuration"
echo "JWT_EXPIRATION=86400000"
echo "JWT_EXPIRATION_MS=3600000"
echo "SECURITY_JWT_SECRET=$JWT_SECRET"
echo ""
echo "# Socket.IO Configuration"
echo "SOCKETIO_HOST=0.0.0.0"
echo "SOCKETIO_PORT=9092"
echo "SOCKETIO_ALLOWED_ORIGINS=https://your-frontend-domain.com,http://localhost:5173"
echo ""
echo "# File Upload Configuration"
echo "MAX_FILE_SIZE=10MB"
echo "MAX_REQUEST_SIZE=10MB"
echo ""

# Save to file
OUTPUT_FILE="railway-secrets-$(date +%Y%m%d-%H%M%S).txt"
cat > "$OUTPUT_FILE" <<EOF
================================================
Railway Secrets - Generated $(date)
================================================

JWT SECRET:
$JWT_SECRET

BACKUP JWT SECRET:
$JWT_SECRET_BACKUP

ADMIN PASSWORD:
$ADMIN_PASSWORD

================================================
COMPLETE ENVIRONMENT VARIABLES FOR RAILWAY
================================================

SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://\${{MYSQLHOST}}:\${{MYSQLPORT}}/\${{MYSQLDATABASE}}?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=\${{MYSQLUSER}}
DB_PASSWORD=\${{MYSQLPASSWORD}}
JWT_EXPIRATION=86400000
JWT_EXPIRATION_MS=3600000
SECURITY_JWT_SECRET=$JWT_SECRET
SOCKETIO_HOST=0.0.0.0
SOCKETIO_PORT=9092
SOCKETIO_ALLOWED_ORIGINS=https://your-frontend-domain.com,http://localhost:5173
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB

================================================
IMPORTANT NOTES
================================================

1. Keep this file secure! It contains sensitive secrets.
2. Add this file to .gitignore immediately.
3. Copy SECURITY_JWT_SECRET to Railway Variables.
4. Update SOCKETIO_ALLOWED_ORIGINS with your actual frontend URL.
5. Railway provides MYSQL* variables automatically when you add MySQL.

================================================
SECURITY REMINDERS
================================================

✅ Never commit secrets to Git
✅ Use Railway Variables for all secrets
✅ Rotate secrets regularly (quarterly)
✅ Keep backup secrets in secure location
✅ Delete this file after copying to Railway

EOF

echo "=================================================="
echo "✅ Secrets saved to: $OUTPUT_FILE"
echo "=================================================="
echo ""
echo "⚠️  IMPORTANT:"
echo "1. Add this file to .gitignore immediately"
echo "2. Copy secrets to Railway Variables"
echo "3. Delete this file after deployment"
echo "4. Never commit this file to Git"
echo ""
echo "🚀 Next steps:"
echo "1. Go to Railway → Your Project → Variables"
echo "2. Copy-paste the environment variables from above"
echo "3. Replace 'your-frontend-domain.com' with your actual domain"
echo "4. Click 'Deploy' in Railway"
echo ""
echo "=================================================="
echo "🎉 Ready to deploy to Railway!"
echo "=================================================="

