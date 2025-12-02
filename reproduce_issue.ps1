# Reproduction Script for Device/Farm Assignment Issues

$baseUrl = "http://localhost:8080"
$adminUser = "admin"
$adminPass = "admin123"

function Get-AuthToken {
    param ($username, $password)
    $body = @{ username = $username; password = $password } | ConvertTo-Json
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body $body
        $json = $response.Content | ConvertFrom-Json
        return $json.token
    } catch {
        Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

$token = Get-AuthToken $adminUser $adminPass
if (-not $token) { exit }
$headers = @{ Authorization = "Bearer $token" }

Write-Host "1. Creating a Farm..." -ForegroundColor Yellow
$farmName = "TestFarm_$(Get-Random)"
$farmBody = @{ name = $farmName } | ConvertTo-Json
try {
    $farmResp = Invoke-WebRequest -Uri "$baseUrl/api/admin/farms" -Method POST -Headers $headers -ContentType "application/json" -Body $farmBody
    $farm = $farmResp.Content | ConvertFrom-Json
    Write-Host "Farm created: ID $($farm.farmId)" -ForegroundColor Green
    $farmId = $farm.farmId
} catch {
    Write-Host "Failed to create farm: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

Write-Host "`n2. Creating a User..." -ForegroundColor Yellow
$username = "User_$(Get-Random)"
$userBody = @{ 
    username = $username
    password = "password123"
    email = "$username@example.com"
    farmId = $farmId
    role = "USER"
} | ConvertTo-Json
try {
    $userResp = Invoke-WebRequest -Uri "$baseUrl/api/admin/users" -Method POST -Headers $headers -ContentType "application/json" -Body $userBody
    $user = $userResp.Content | ConvertFrom-Json
    Write-Host "User created: ID $($user.userId)" -ForegroundColor Green
    $userId = $user.userId
} catch {
    Write-Host "Failed to create user: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

Write-Host "`n3. Assigning Farm to User using NEW Endpoint..." -ForegroundColor Yellow
$updateBody = @{
    farmId = $farmId
} | ConvertTo-Json

try {
    $updateResp = Invoke-WebRequest -Uri "$baseUrl/api/admin/users/$userId" -Method PUT -Headers $headers -ContentType "application/json" -Body $updateBody
    $updateResult = $updateResp.Content | ConvertFrom-Json
    Write-Host "Update result: $($updateResult.message)" -ForegroundColor Green
    Write-Host "User Farm ID: $($updateResult.farmId)" -ForegroundColor Cyan
    
    if ($updateResult.farmId -eq $farmId) {
        Write-Host "SUCCESS: User assigned to farm correctly!" -ForegroundColor Green
    } else {
        Write-Host "FAILURE: User farm ID does not match!" -ForegroundColor Red
    }
} catch {
    Write-Host "Update failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
        $errContent = $reader.ReadToEnd()
        Write-Host "Error Details: $errContent" -ForegroundColor Red
    }
}

Write-Host "`n4. Attempting to Assign a Real Device to Farm..." -ForegroundColor Yellow
$deviceId = "ESP32_$(Get-Random)"
$assignDeviceBody = @{
    deviceId = $deviceId
    farmId = $farmId
} | ConvertTo-Json

try {
    $devResp = Invoke-WebRequest -Uri "$baseUrl/api/admin/devices/assign" -Method POST -Headers $headers -ContentType "application/json" -Body $assignDeviceBody
    $devResult = $devResp.Content | ConvertFrom-Json
    Write-Host "Device Assignment result: $($devResult.message)" -ForegroundColor Green
} catch {
    Write-Host "Device Assignment failed: $($_.Exception.Message)" -ForegroundColor Red
    # Print detailed error if possible
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
        $errContent = $reader.ReadToEnd()
        Write-Host "Error Details: $errContent" -ForegroundColor Red
    }
}
