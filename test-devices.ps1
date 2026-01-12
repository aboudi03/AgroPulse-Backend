# AgroPulse API Test Script - DEVICE TEST
# Run this after starting the server with: mvn spring-boot:run

Write-Host "=== 🌱 AgroPulse API - Device Test Sequence ===" -ForegroundColor Cyan
Write-Host ""

# ============================================
# STEP 1: LOGIN AND GET TOKEN
# ============================================
Write-Host "[STEP 1] 🔐 Logging in as admin..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody
    
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    
    Write-Host "✓ LOGIN SUCCESS - Token: $($token.Substring(0, 20))..." -ForegroundColor Green
} catch {
    Write-Host "✗ LOGIN FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit
}
Write-Host ""

# ============================================
# STEP 2: SEND SIMULATED SENSOR DATA (like Arduino would)
# ============================================
Write-Host "[STEP 2] 📡 Sending simulated sensor data (like ESP32)..." -ForegroundColor Yellow
$sensorData = @{
    deviceId = "GHOUSE_01"
    soil = 65
    humidity = 75.5
    temperature = 28.3
} | ConvertTo-Json

try {
    $sensorResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/sensor/add" `
        -Method POST `
        -ContentType "application/json" `
        -Body $sensorData
    
    Write-Host "✓ SENSOR DATA SENT - Status: $($sensorResponse.StatusCode)" -ForegroundColor Green
    Write-Host "  Device registered: GHOUSE_01" -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# ============================================
# STEP 3: CHECK IF DEVICE APPEARS IN ADMIN PANEL
# ============================================
Write-Host "[STEP 3] 📋 Checking admin devices list..." -ForegroundColor Yellow
try {
    $headers = @{ Authorization = "Bearer $token" }
    $devicesResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/admin/devices" `
        -Method GET `
        -Headers $headers
    
    $devices = $devicesResponse.Content | ConvertFrom-Json
    Write-Host "✓ DEVICES RETRIEVED - Total: $($devices.Count)" -ForegroundColor Green
    
    if ($devices.Count -gt 0) {
        Write-Host "  Devices found:" -ForegroundColor Green
        foreach ($device in $devices) {
            Write-Host "    - Device ID: $($device.deviceId)" -ForegroundColor Green
            Write-Host "      IP: $($device.ip)" -ForegroundColor Gray
            Write-Host "      Farm ID: $($device.farmId)" -ForegroundColor Gray
        }
    } else {
        Write-Host "  ⚠ No devices found - devices list is empty!" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# ============================================
# STEP 4: GET ALL SENSOR READINGS
# ============================================
Write-Host "[STEP 4] 📊 Checking sensor readings..." -ForegroundColor Yellow
try {
    $headers = @{ Authorization = "Bearer $token" }
    $readingsResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/sensor/all" `
        -Method GET `
        -Headers $headers
    
    $readings = $readingsResponse.Content | ConvertFrom-Json
    Write-Host "✓ READINGS RETRIEVED - Total: $($readings.Count)" -ForegroundColor Green
    
    if ($readings.Count -gt 0) {
        Write-Host "  Latest reading:" -ForegroundColor Green
        $latest = $readings[0]
        Write-Host "    Device: $($latest.deviceId)" -ForegroundColor Gray
        Write-Host "    Soil: $($latest.soil)%" -ForegroundColor Gray
        Write-Host "    Humidity: $($latest.humidity)%" -ForegroundColor Gray
        Write-Host "    Temperature: $($latest.temperature)°C" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== ✓ Test Complete ===" -ForegroundColor Cyan
