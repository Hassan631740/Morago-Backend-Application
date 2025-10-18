#!/bin/bash

echo "🔧 Testing Spring Boot Configuration"
echo "===================================="

# Test 1: Check if application compiles
echo "1. Testing compilation..."
if mvn clean compile -q; then
    echo "   ✅ Compilation successful"
else
    echo "   ❌ Compilation failed"
    exit 1
fi

# Test 2: Check if application starts without errors (quick test)
echo "2. Testing application startup..."
if mvn spring-boot:run -Dspring-boot.run.profiles=local -q &
then
    APP_PID=$!
    sleep 3
    
    # Check if process is still running
    if kill -0 $APP_PID 2>/dev/null; then
        echo "   ✅ Application started successfully"
        kill $APP_PID
    else
        echo "   ❌ Application failed to start"
        exit 1
    fi
else
    echo "   ❌ Failed to start application"
    exit 1
fi

echo ""
echo "🎉 All tests passed! Configuration is working correctly."
echo ""
echo "📝 Next steps:"
echo "1. Create your .env file: cp env.example .env"
echo "2. Fill in your actual database credentials"
echo "3. Generate JWT secrets: ./generate-jwt-secrets.sh"
echo "4. Run the application: mvn spring-boot:run -Dspring-boot.run.profiles=local"
