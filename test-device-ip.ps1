# PowerShell script to test Device IP tracking
# Tests automatic IP detection and retrieval

$baseUrl = "http://localhost:8080"
$token = ""

Write-Host "=== Testing Device IP Tracking ===" -ForegroundColor Cyan

# Step 1: Login as admin
Write-Host "`n1. Logging in as admin..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody

$token = $loginResponse.token
Write-Host "Login successful! Token: $($token.Substring(0, 20))..." -ForegroundColor Green

# Step 2: Assign a device to a farm (IP will be auto-detected)
Write-Host "`n2. Assigning device to farm (IP will be auto-detected)..." -ForegroundColor Yellow
$deviceBody = @{
    deviceId = "ESP32-TEST-001"
    farmId = 1
    # ip is optional - will be auto-detected
} | ConvertTo-Json

$assignResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/devices/assign" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{
        "Authorization" = "Bearer $token"
    } `
    -Body $deviceBody

Write-Host "Device assigned!" -ForegroundColor Green
Write-Host "Device ID: $($assignResponse.deviceId)" -ForegroundColor Gray
Write-Host "IP Status: $($assignResponse.ipAddress)" -ForegroundColor Gray

# Step 3: Simulate ESP32 sending sensor data (this auto-detects IP)
Write-Host "`n3. Simulating ESP32 sending sensor data (auto-detects IP)..." -ForegroundColor Yellow
$sensorBody = @{
    deviceId = "ESP32-TEST-001"
    soil = 45
    humidity = 60.5
    temperature = 25.3
} | ConvertTo-Json

$sensorResponse = Invoke-RestMethod -Uri "$baseUrl/api/sensor/add" `
    -Method POST `
    -ContentType "application/json" `
    -Body $sensorBody

Write-Host "Sensor data sent successfully!" -ForegroundColor Green
Write-Host "Reading ID: $($sensorResponse.id)" -ForegroundColor Gray

# Step 4: Get all devices via Admin API (shows IP)
Write-Host "`n4. Getting all devices via Admin API (with IP addresses)..." -ForegroundColor Yellow
$devicesResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/devices" `
    -Method GET `
    -Headers @{
        "Authorization" = "Bearer $token"
    }

Write-Host "Total devices: $($devicesResponse.Count)" -ForegroundColor Green
foreach ($device in $devicesResponse) {
    Write-Host "  Device ID: $($device.deviceId)" -ForegroundColor Cyan
    Write-Host "    IP Address: $($device.ip)" -ForegroundColor Yellow
    Write-Host "    Farm ID: $($device.farmId)" -ForegroundColor Gray
    Write-Host ""
}

# Step 5: Get specific device info
Write-Host "`n5. Checking specific device..." -ForegroundColor Yellow
$specificDevice = $devicesResponse | Where-Object { $_.deviceId -eq "ESP32-TEST-001" }
if ($specificDevice) {
    Write-Host "Found device: $($specificDevice.deviceId)" -ForegroundColor Green
    Write-Host "  IP Address: $($specificDevice.ip)" -ForegroundColor Yellow
    Write-Host "  Farm ID: $($specificDevice.farmId)" -ForegroundColor Gray
} else {
    Write-Host "Device not found" -ForegroundColor Red
}

Write-Host "`n=== Test Completed ===" -ForegroundColor Cyan
Write-Host "`nNote: The IP shown is the IP address of the machine that sent the request." -ForegroundColor Gray
Write-Host "For ESP32, this will be the ESP32's IP address from DHCP." -ForegroundColor Gray

