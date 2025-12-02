# Reproduction Script for 403 Forbidden on Sensor Data

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

# 1. Login as Admin
$adminToken = Get-AuthToken $adminUser $adminPass
if (-not $adminToken) { exit }
$adminHeaders = @{ Authorization = "Bearer $adminToken" }

# 2. Create Farm A
Write-Host "Creating Farm A..." -ForegroundColor Yellow
$farmABody = @{ name = "Farm A" } | ConvertTo-Json
$farmAResp = Invoke-WebRequest -Uri "$baseUrl/api/admin/farms" -Method POST -Headers $adminHeaders -ContentType "application/json" -Body $farmABody
$farmAId = ($farmAResp.Content | ConvertFrom-Json).farmId
Write-Host "Farm A Created: $farmAId" -ForegroundColor Green

# 3. Create User A (assigned to Farm A)
Write-Host "Creating User A (assigned to Farm A)..." -ForegroundColor Yellow
$userAName = "UserA_$(Get-Random)"
$userABody = @{ 
    username = $userAName
    password = "password123"
    email = "$userAName@example.com"
    farmId = $farmAId
    role = "USER"
} | ConvertTo-Json
$userAResp = Invoke-WebRequest -Uri "$baseUrl/api/admin/users" -Method POST -Headers $adminHeaders -ContentType "application/json" -Body $userABody
$userAId = ($userAResp.Content | ConvertFrom-Json).userId
Write-Host "User A Created: $userAId" -ForegroundColor Green

# 4. Create Device A (assigned to Farm A)
Write-Host "Creating Device A (assigned to Farm A)..." -ForegroundColor Yellow
$deviceAId = "DeviceA_$(Get-Random)"
$deviceABody = @{ 
    deviceId = $deviceAId
    farmId = $farmAId
} | ConvertTo-Json
Invoke-WebRequest -Uri "$baseUrl/api/admin/devices/assign" -Method POST -Headers $adminHeaders -ContentType "application/json" -Body $deviceABody | Out-Null
Write-Host "Device A Assigned to Farm A" -ForegroundColor Green

# 5. Create Device B (assigned to NO Farm or Farm B)
Write-Host "Creating Device B (Unassigned)..." -ForegroundColor Yellow
$deviceBId = "DeviceB_$(Get-Random)"
# Just simulate it sending data to create it
$readingBody = @{
    deviceId = $deviceBId
    temperature = 25.0
    humidity = 60.0
    soil = 50.0
} | ConvertTo-Json
Invoke-WebRequest -Uri "$baseUrl/api/sensor/add" -Method POST -ContentType "application/json" -Body $readingBody | Out-Null
Write-Host "Device B Created (Unassigned)" -ForegroundColor Green

# 6. Login as User A
Write-Host "Logging in as User A..." -ForegroundColor Yellow
$userAToken = Get-AuthToken $userAName "password123"
$userAHeaders = @{ Authorization = "Bearer $userAToken" }

# 7. User A tries to access Device A (Should Succeed)
Write-Host "User A accessing Device A (Same Farm)..." -ForegroundColor Yellow
try {
    $resp = Invoke-WebRequest -Uri "$baseUrl/api/sensor/$deviceAId/latest" -Method GET -Headers $userAHeaders
    Write-Host "SUCCESS: Accessed Device A" -ForegroundColor Green
} catch {
    Write-Host "FAILED to access Device A: $($_.Exception.Message)" -ForegroundColor Red
}

# 8. User A tries to access Device B (Different/No Farm)
Write-Host "User A accessing Device B (Unassigned)..." -ForegroundColor Yellow
try {
    $resp = Invoke-WebRequest -Uri "$baseUrl/api/sensor/$deviceBId/latest" -Method GET -Headers $userAHeaders
    Write-Host "SUCCESS: Accessed Device B (Unexpected)" -ForegroundColor Red
} catch {
    Write-Host "EXPECTED FAILURE accessing Device B: $($_.Exception.Message)" -ForegroundColor Green
    if ($_.Exception.Response.StatusCode -eq [System.Net.HttpStatusCode]::Forbidden) {
        Write-Host "Confirmed 403 Forbidden" -ForegroundColor Green
    }
}
