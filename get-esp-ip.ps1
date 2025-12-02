# PowerShell script to get ESP32 Device IP Address
# Usage: .\get-esp-ip.ps1

$baseUrl = "http://localhost:8080"

Write-Host "=== Getting ESP32 Device IP Address ===" -ForegroundColor Cyan
Write-Host ""

# Step 1: Login as admin
Write-Host "1. Logging in..." -ForegroundColor Yellow
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

# Step 2: Get all devices with IPs
Write-Host "2. Getting all devices with IP addresses..." -ForegroundColor Yellow
Write-Host ""

$devices = Invoke-RestMethod -Uri "$baseUrl/api/admin/devices" `
    -Method GET `
    -Headers @{
        "Authorization" = "Bearer $token"
    }

if ($devices.Count -eq 0) {
    Write-Host "No devices found." -ForegroundColor Yellow
    Write-Host "Devices will appear here after:" -ForegroundColor Gray
    Write-Host "  1. Assigning device to farm via admin panel" -ForegroundColor Gray
    Write-Host "  2. ESP32 sends sensor data (auto-detects IP)" -ForegroundColor Gray
} else {
    Write-Host "Found $($devices.Count) device(s):" -ForegroundColor Green
    Write-Host ""
    foreach ($device in $devices) {
        Write-Host "  Device ID: $($device.deviceId)" -ForegroundColor Cyan
        Write-Host "    IP Address: $($device.ip)" -ForegroundColor Yellow
        Write-Host "    Farm ID: $($device.farmId)" -ForegroundColor Gray
        Write-Host ""
    }
}

# Step 3: Get specific device (if needed)
Write-Host "3. To get a specific device IP, filter by deviceId:" -ForegroundColor Yellow
Write-Host '   $devices | Where-Object { $_.deviceId -eq "ESP32-001" }' -ForegroundColor Gray
Write-Host ""

