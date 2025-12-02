# ESP Scanner Backend Endpoint

## Overview

The ESP scanner endpoint allows admins to discover ESP32 devices on the network. Devices are automatically discovered when they send sensor data to `/api/sensor/add`, and this endpoint returns all known devices with their registration status.

## Endpoint

**POST** `/api/admin/devices/scan`

### Authentication
- Requires: Admin role
- Header: `Authorization: Bearer <JWT_TOKEN>`

### Request
No request body required.

### Response

Returns an array of `ScannedEspDto` objects:

```json
[
  {
    "deviceId": "ESP32-001",
    "ipAddress": "192.168.1.100",
    "macAddress": null,
    "registered": true,
    "farmId": 1
  },
  {
    "deviceId": "ESP32-002",
    "ipAddress": "192.168.1.101",
    "macAddress": null,
    "registered": false,
    "farmId": null
  }
]
```

### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `deviceId` | String | Unique device identifier |
| `ipAddress` | String | Device IP address (auto-detected when device sends data) |
| `macAddress` | String \| null | MAC address (optional, currently null) |
| `registered` | Boolean | Whether device is assigned to a farm |
| `farmId` | Long \| null | Farm ID if registered, null otherwise |

## How It Works

1. **Device Discovery**: ESP32 devices are automatically discovered when they send sensor data to `/api/sensor/add`
2. **IP Detection**: The backend automatically extracts and saves the device's IP address from the HTTP request
3. **Registration Status**: Devices are considered "registered" if they have a `farmId` assigned
4. **Scan Results**: The scan endpoint returns all devices that have been seen (sent data) with their current status

## Usage Example

### PowerShell
```powershell
# Login
$login = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method POST -ContentType "application/json" `
    -Body '{"username":"admin","password":"admin123"}'
$token = $login.token

# Scan for devices
$devices = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/devices/scan" `
    -Method POST -Headers @{"Authorization"="Bearer $token"}

# Display results
$devices | Format-Table deviceId, ipAddress, registered, farmId
```

### curl
```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | \
  grep -o '"token":"[^"]*' | cut -d'"' -f4)

# Scan for devices
curl -X POST http://localhost:8080/api/admin/devices/scan \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

## Implementation Details

### Current Implementation
- Returns all devices from the database that have been seen (sent sensor data)
- Registration status is determined by whether `farmId` is set
- IP addresses are automatically detected when devices send data

### Future Enhancements
- Network scanning: Actively scan the network for new ESP32 devices
- MAC address detection: Extract and store MAC addresses
- Device health check: Ping devices to verify they're online
- Last seen timestamp: Show when device was last active

## Security

- **Authentication**: Requires admin JWT token
- **Authorization**: Only users with ADMIN role can access
- **Rate Limiting**: Consider adding rate limiting for production use

## Testing

Run the test script:
```powershell
.\test-esp-scan.ps1
```

Or test manually:
1. Create a device via `/api/admin/devices/assign`
2. Have ESP32 send sensor data to `/api/sensor/add` (or simulate it)
3. Call `/api/admin/devices/scan` to see the device

## Notes

- Devices must send sensor data at least once to appear in scan results
- IP addresses are automatically detected using DHCP (no static IP needed)
- Unregistered devices can be assigned to farms via `/api/admin/devices/assign`

