#!/bin/bash
# Bash script to test Admin API endpoints with CORS
# Make sure the backend is running on http://localhost:8080

BASE_URL="http://localhost:8080"
TOKEN=""

echo "=== Testing Admin API with CORS ==="

# Step 1: Login as admin
echo ""
echo "1. Logging in as admin..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
ROLE=$(echo $LOGIN_RESPONSE | grep -o '"role":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "ERROR: Login failed!"
  echo "Response: $LOGIN_RESPONSE"
  exit 1
fi

echo "Login successful! Token: ${TOKEN:0:20}..."
echo "Role: $ROLE"

# Step 2: Create a Farm
echo ""
echo "2. Creating a farm..."
TIMESTAMP=$(date +%Y%m%d%H%M%S)
FARM_RESPONSE=$(curl -s -X POST "$BASE_URL/api/admin/farms" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://localhost:3000" \
  -d "{
    \"name\": \"Test Farm $TIMESTAMP\"
  }")

FARM_ID=$(echo $FARM_RESPONSE | grep -o '"farmId":[0-9]*' | cut -d':' -f2)
FARM_NAME=$(echo $FARM_RESPONSE | grep -o '"name":"[^"]*' | cut -d'"' -f4)

if [ -z "$FARM_ID" ]; then
  echo "ERROR: Farm creation failed!"
  echo "Response: $FARM_RESPONSE"
  exit 1
fi

echo "Farm created successfully!"
echo "Farm ID: $FARM_ID"
echo "Farm Name: $FARM_NAME"

# Step 3: Get all farms
echo ""
echo "3. Getting all farms..."
FARMS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/admin/farms" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://localhost:3000")

echo "Farms response: $FARMS_RESPONSE"

# Step 4: Create a User
echo ""
echo "4. Creating a user..."
USER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/admin/users" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://localhost:3000" \
  -d "{
    \"username\": \"testuser$TIMESTAMP\",
    \"password\": \"password123\",
    \"email\": \"testuser@example.com\",
    \"farmId\": $FARM_ID,
    \"role\": \"USER\"
  }")

USER_ID=$(echo $USER_RESPONSE | grep -o '"userId":[0-9]*' | cut -d':' -f2)
USERNAME=$(echo $USER_RESPONSE | grep -o '"username":"[^"]*' | cut -d'"' -f4)

if [ -z "$USER_ID" ]; then
  echo "ERROR: User creation failed!"
  echo "Response: $USER_RESPONSE"
  exit 1
fi

echo "User created successfully!"
echo "User ID: $USER_ID"
echo "Username: $USERNAME"

# Step 5: Get all users
echo ""
echo "5. Getting all users..."
USERS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/admin/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://localhost:3000")

echo "Users response: $USERS_RESPONSE"

# Step 6: Assign a device to farm
echo ""
echo "6. Assigning device to farm..."
DEVICE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/admin/devices/assign" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://localhost:3000" \
  -d "{
    \"deviceId\": \"DEVICE-$TIMESTAMP\",
    \"farmId\": $FARM_ID,
    \"ip\": \"192.168.1.100\"
  }")

DEVICE_ID=$(echo $DEVICE_RESPONSE | grep -o '"deviceId":"[^"]*' | cut -d'"' -f4)

if [ -z "$DEVICE_ID" ]; then
  echo "ERROR: Device assignment failed!"
  echo "Response: $DEVICE_RESPONSE"
  exit 1
fi

echo "Device assigned successfully!"
echo "Device ID: $DEVICE_ID"

# Step 7: Get all devices
echo ""
echo "7. Getting all devices..."
DEVICES_RESPONSE=$(curl -s -X GET "$BASE_URL/api/admin/devices" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://localhost:3000")

echo "Devices response: $DEVICES_RESPONSE"

echo ""
echo "=== All tests completed! ==="

