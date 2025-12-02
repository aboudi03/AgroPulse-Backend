# PowerShell script to test Admin API endpoints with CORS
# Make sure the backend is running on http://localhost:8080

$baseUrl = "http://localhost:8080"
$token = ""

Write-Host "=== Testing Admin API with CORS ===" -ForegroundColor Cyan

# Step 1: Login as admin
Write-Host "`n1. Logging in as admin..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody `
    -Headers @{
        "Origin" = "http://localhost:3000"
        "Access-Control-Request-Method" = "POST"
    }

$token = $loginResponse.token
Write-Host "Login successful! Token: $($token.Substring(0, 20))..." -ForegroundColor Green
Write-Host "Role: $($loginResponse.role)" -ForegroundColor Green

# Step 2: Create a Farm
Write-Host "`n2. Creating a farm..." -ForegroundColor Yellow
$farmBody = @{
    name = "Test Farm $(Get-Date -Format 'yyyyMMddHHmmss')"
} | ConvertTo-Json

$farmResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/farms" `
    -Method POST `
    -ContentType "application/json" `
    -Body $farmBody `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Origin" = "http://localhost:3000"
    }

Write-Host "Farm created successfully!" -ForegroundColor Green
Write-Host "Farm ID: $($farmResponse.farmId)" -ForegroundColor Green
Write-Host "Farm Name: $($farmResponse.name)" -ForegroundColor Green
$farmId = $farmResponse.farmId

# Step 3: Get all farms
Write-Host "`n3. Getting all farms..." -ForegroundColor Yellow
$farmsResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/farms" `
    -Method GET `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Origin" = "http://localhost:3000"
    }

Write-Host "Total farms: $($farmsResponse.Count)" -ForegroundColor Green
foreach ($farm in $farmsResponse) {
    Write-Host "  - Farm ID: $($farm.id), Name: $($farm.name)" -ForegroundColor Gray
}

# Step 4: Create a User
Write-Host "`n4. Creating a user..." -ForegroundColor Yellow
$userBody = @{
    username = "testuser$(Get-Date -Format 'yyyyMMddHHmmss')"
    password = "password123"
    email = "testuser@example.com"
    farmId = $farmId
    role = "USER"
} | ConvertTo-Json

$userResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/users" `
    -Method POST `
    -ContentType "application/json" `
    -Body $userBody `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Origin" = "http://localhost:3000"
    }

Write-Host "User created successfully!" -ForegroundColor Green
Write-Host "User ID: $($userResponse.userId)" -ForegroundColor Green
Write-Host "Username: $($userResponse.username)" -ForegroundColor Green

# Step 5: Get all users
Write-Host "`n5. Getting all users..." -ForegroundColor Yellow
$usersResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/users" `
    -Method GET `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Origin" = "http://localhost:3000"
    }

Write-Host "Total users: $($usersResponse.Count)" -ForegroundColor Green
foreach ($user in $usersResponse) {
    Write-Host "  - User ID: $($user.id), Username: $($user.username), Role: $($user.role)" -ForegroundColor Gray
}

# Step 6: Assign a device to farm
Write-Host "`n6. Assigning device to farm..." -ForegroundColor Yellow
$deviceBody = @{
    deviceId = "DEVICE-$(Get-Date -Format 'yyyyMMddHHmmss')"
    farmId = $farmId
    ip = "192.168.1.100"
} | ConvertTo-Json

$deviceResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/devices/assign" `
    -Method POST `
    -ContentType "application/json" `
    -Body $deviceBody `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Origin" = "http://localhost:3000"
    }

Write-Host "Device assigned successfully!" -ForegroundColor Green
Write-Host "Device ID: $($deviceResponse.deviceId)" -ForegroundColor Green
Write-Host "Farm ID: $($deviceResponse.farmId)" -ForegroundColor Green

# Step 7: Get all devices
Write-Host "`n7. Getting all devices..." -ForegroundColor Yellow
$devicesResponse = Invoke-RestMethod -Uri "$baseUrl/api/admin/devices" `
    -Method GET `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Origin" = "http://localhost:3000"
    }

Write-Host "Total devices: $($devicesResponse.Count)" -ForegroundColor Green
foreach ($device in $devicesResponse) {
    Write-Host "  - Device ID: $($device.deviceId), IP: $($device.ip), Farm ID: $($device.farmId)" -ForegroundColor Gray
}

Write-Host "`n=== All tests completed! ===" -ForegroundColor Cyan

