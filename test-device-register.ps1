# Test ESP32 Device Registration
# This script tests the device registration endpoint

Write-Host "Testing ESP32 Device Registration..." -ForegroundColor Cyan

$body = @{
    deviceId = "GHOUSE_01"
} | ConvertTo-Json

Write-Host "`nSending POST request to http://localhost:8080/api/device/register" -ForegroundColor Yellow
Write-Host "Body: $body`n" -ForegroundColor Gray

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/device/register" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body `
        -ErrorAction Stop
    
    Write-Host "✅ SUCCESS!" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Cyan
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "Status Code: $statusCode" -ForegroundColor Yellow
        
        if ($statusCode -eq 403) {
            Write-Host "`n⚠️  403 Forbidden - Make sure:" -ForegroundColor Yellow
            Write-Host "   1. Spring Boot server has been RESTARTED after security changes" -ForegroundColor White
            Write-Host "   2. Server is running on port 8080" -ForegroundColor White
            Write-Host "   3. SecurityConfig.java has /api/device/** in permitAll()" -ForegroundColor White
        }
    }
}

