# PowerShell script to test ESP Scanner endpoint
# Tests POST /api/admin/devices/scan

$baseUrl = "http://localhost:8080"

Write-Host "=== Testing ESP Scanner Endpoint ===" -ForegroundColor Cyan
Write-Host ""

# Step 1: Login as admin
Write-Host "1. Logging in as admin..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody

$token = $loginResponse.token
Write-Host "✓ Login successful" -ForegroundColor Green
Write-Host ""

# Step 2: Create some test devices (if they don't exist)
Write-Host "2. Creating test devices..." -ForegroundColor Yellow
$device1 = @{
    deviceId = "ESP32-SCAN-TEST-001"
    farmId = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "$baseUrl/api/admin/devices/assign" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{ "Authorization" = "Bearer $token" } `
    -Body $device1 | Out-Null

Write-Host "✓ Test device created" -ForegroundColor Green
Write-Host ""

# Step 3: Simulate ESP32 sending data (to auto-detect IP)
Write-Host "3. Simulating ESP32 sending sensor data (auto-detects IP)..." -ForegroundColor Yellow
$sensorData = @{
    deviceId = "ESP32-SCAN-TEST-001"
    soil = 45
    humidity = 60.5
    temperature = 25.3
} | ConvertTo-Json

Invoke-RestMethod -Uri "$baseUrl/api/sensor/add" `
    -Method POST `
    -ContentType "application/json" `
    -Body $sensorData | Out-Null

Write-Host "✓ Sensor data sent (IP auto-detected)" -ForegroundColor Green
Write-Host ""

# Step 4: Scan for ESP devices
Write-Host "4. Scanning for ESP devices..." -ForegroundColor Yellow
$scannedDevices = Invoke-RestMethod -Uri "$baseUrl/api/admin/devices/scan" `
    -Method POST `
    -Headers @{ "Authorization" = "Bearer $token" }

Write-Host "✓ Scan completed" -ForegroundColor Green
Write-Host ""
Write-Host "Found $($scannedDevices.Count) device(s):" -ForegroundColor Green
Write-Host ""

if ($scannedDevices.Count -eq 0) {
    Write-Host "No devices found." -ForegroundColor Yellow
    Write-Host "Devices will appear after they send sensor data to /api/sensor/add" -ForegroundColor Gray
} else {
    foreach ($device in $scannedDevices) {
        Write-Host "  Device ID: $($device.deviceId)" -ForegroundColor Cyan
        Write-Host "    IP Address: $($device.ipAddress)" -ForegroundColor Yellow
        Write-Host "    MAC Address: $(if ($device.macAddress) { $device.macAddress } else { 'N/A' })" -ForegroundColor Gray
        Write-Host "    Registered: $(if ($device.registered) { 'Yes' } else { 'No' })" -ForegroundColor $(if ($device.registered) { 'Green' } else { 'Yellow' })
        if ($device.farmId) {
            Write-Host "    Farm ID: $($device.farmId)" -ForegroundColor Gray
        }
        Write-Host ""
    }
}

Write-Host "=== Test Completed ===" -ForegroundColor Cyan

