# Simple CORS test script
# Tests if CORS headers are properly returned

$baseUrl = "http://localhost:8080"

Write-Host "=== Testing CORS Headers ===" -ForegroundColor Cyan

# Test OPTIONS preflight request
Write-Host "`n1. Testing OPTIONS preflight request..." -ForegroundColor Yellow
try {
    $optionsResponse = Invoke-WebRequest -Uri "$baseUrl/api/admin/farms" `
        -Method OPTIONS `
        -Headers @{
            "Origin" = "http://localhost:3000"
            "Access-Control-Request-Method" = "POST"
            "Access-Control-Request-Headers" = "Content-Type,Authorization"
        } -ErrorAction Stop

    Write-Host "Status Code: $($optionsResponse.StatusCode)" -ForegroundColor Green
    Write-Host "CORS Headers:" -ForegroundColor Green
    $optionsResponse.Headers | Where-Object { $_.Key -like "*Access-Control*" } | ForEach-Object {
        Write-Host "  $($_.Key): $($_.Value)" -ForegroundColor Gray
    }
} catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

# Test actual POST request with CORS
Write-Host "`n2. Testing POST request with CORS headers..." -ForegroundColor Yellow

# First login
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody

$token = $loginResponse.token

# Then test POST with CORS
try {
    $postResponse = Invoke-WebRequest -Uri "$baseUrl/api/admin/farms" `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Origin" = "http://localhost:3000"
        } `
        -Body (@{
            name = "CORS Test Farm"
        } | ConvertTo-Json) -ErrorAction Stop

    Write-Host "Status Code: $($postResponse.StatusCode)" -ForegroundColor Green
    Write-Host "CORS Headers:" -ForegroundColor Green
    $postResponse.Headers | Where-Object { $_.Key -like "*Access-Control*" } | ForEach-Object {
        Write-Host "  $($_.Key): $($_.Value)" -ForegroundColor Gray
    }
    Write-Host "Response Body: $($postResponse.Content)" -ForegroundColor Gray
} catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response: $responseBody" -ForegroundColor Red
    }
}

Write-Host "`n=== CORS Test Completed ===" -ForegroundColor Cyan

