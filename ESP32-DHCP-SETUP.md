# ESP32 DHCP Setup - Automatic IP Detection

## Overview

The backend now **automatically detects and saves ESP32 IP addresses** when devices send sensor data. ESP32 devices can use **DHCP** (no static IP configuration needed).

## How It Works

### 1. ESP32 Configuration (Arduino Code)
- **Use DHCP** - Let the router assign IP addresses automatically
- **No static IP configuration needed**
- ESP32 just needs to send HTTP POST requests to: `http://YOUR_BACKEND_IP:8080/api/sensor/add`

### 2. Backend Automatic IP Detection

When ESP32 sends sensor data to `/api/sensor/add`:
1. Backend extracts the **real client IP** from the HTTP request
2. Automatically creates/updates the Device record with the IP address
3. Updates `lastSeen` timestamp automatically
4. Handles IP changes (if ESP32 gets a new IP from DHCP)

### 3. IP Extraction Features

The `IpAddressUtil` class handles:
- ✅ Direct connections (`getRemoteAddr()`)
- ✅ Proxies and load balancers (`X-Forwarded-For` header)
- ✅ Alternative proxy headers (`X-Real-IP`, `X-Forwarded`)
- ✅ IPv6 to IPv4 conversion for localhost
- ✅ Multiple IPs in headers (takes the first one - original client)

## Device Registration Flow

### Option 1: Automatic (Recommended)
1. Admin assigns device to farm via `/api/admin/devices/assign` (IP not required)
2. ESP32 sends sensor data to `/api/sensor/add`
3. Backend automatically detects and saves the IP address
4. Device is now fully registered and trackable

### Option 2: Manual IP Override
- Admin can still manually set IP via `/api/admin/devices/assign` if needed
- This is optional - IP will be auto-updated when device sends data anyway

## Example ESP32 Code (Arduino)

```cpp
#include <WiFi.h>
#include <HTTPClient.h>

const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";
const char* serverUrl = "http://YOUR_BACKEND_IP:8080/api/sensor/add";

void setup() {
  Serial.begin(115200);
  
  // Connect to WiFi (DHCP - automatic IP)
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected!");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP()); // This is auto-assigned by DHCP
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverUrl);
    http.addHeader("Content-Type", "application/json");
    
    // Send sensor data
    String jsonData = "{\"deviceId\":\"ESP32-001\",\"soil\":45,\"humidity\":60.5,\"temperature\":25.3}";
    int httpResponseCode = http.POST(jsonData);
    
    if (httpResponseCode > 0) {
      Serial.print("Response code: ");
      Serial.println(httpResponseCode);
      // Backend automatically saves your IP address!
    }
    
    http.end();
  }
  
  delay(30000); // Send data every 30 seconds
}
```

## API Endpoints

### POST `/api/sensor/add`
- **Purpose**: ESP32 sends sensor readings
- **Automatic**: Extracts and saves device IP from request
- **No authentication required** (public endpoint for devices)

**Request Body:**
```json
{
  "deviceId": "ESP32-001",
  "soil": 45,
  "humidity": 60.5,
  "temperature": 25.3
}
```

### POST `/api/admin/devices/assign`
- **Purpose**: Assign device to farm
- **IP is optional** - will be auto-detected when device sends data

**Request Body:**
```json
{
  "deviceId": "ESP32-001",
  "farmId": 1,
  "ip": null  // Optional - will be auto-detected
}
```

## Benefits

✅ **No static IP configuration** - ESP32 uses DHCP  
✅ **Automatic IP tracking** - Backend reads IP from HTTP requests  
✅ **Handles IP changes** - If ESP32 gets new IP, it's automatically updated  
✅ **Works behind proxies** - Handles X-Forwarded-For headers  
✅ **Scalable** - No manual IP management needed  
✅ **Professional** - Industry-standard approach  

## Testing

Test the automatic IP detection:

```bash
# Simulate ESP32 sending data
curl -X POST http://localhost:8080/api/sensor/add \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "TEST-DEVICE-001",
    "soil": 50,
    "humidity": 65.0,
    "temperature": 22.5
  }'

# Check if device was created with your IP
curl -X GET http://localhost:8080/api/admin/devices \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

The device should appear with your machine's IP address automatically!

