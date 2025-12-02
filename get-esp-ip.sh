#!/bin/bash
# Get ESP32 Device IP Address using curl

BASE_URL="http://localhost:8080"

echo "=== Getting ESP32 Device IP Address ==="
echo ""

# Step 1: Login as admin
echo "1. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "ERROR: Login failed!"
  exit 1
fi

echo "✓ Login successful"
echo ""

# Step 2: Get all devices with IPs
echo "2. Getting all devices with IP addresses..."
echo ""
curl -s -X GET "$BASE_URL/api/admin/devices" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'

# Alternative: Get specific device IP
echo ""
echo "3. To get a specific device IP, use:"
echo "   curl -X GET \"$BASE_URL/api/admin/devices\" \\"
echo "     -H \"Authorization: Bearer $TOKEN\" | jq '.[] | select(.deviceId==\"YOUR_DEVICE_ID\")'"
echo ""

