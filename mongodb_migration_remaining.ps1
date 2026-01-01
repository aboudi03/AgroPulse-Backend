# MongoDB Migration Completion Script
# This script contains all remaining fixes needed to complete the MongoDB migration

Write-Host "MongoDB Migration - Remaining Fixes" -ForegroundColor Cyan
Write-Host ""
Write-Host "The following files still need manual updates:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Repository Implementations:" -ForegroundColor White
Write-Host "   - FarmRepositoryImpl.java" -ForegroundColor Gray
Write-Host "   - DeviceRepositoryImpl.java" -ForegroundColor Gray
Write-Host "   - SensorRepositoryImpl.java" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Services:" -ForegroundColor White
Write-Host "   - AuthService.java (change farmId parameter to String)" -ForegroundColor Gray
Write-Host "   - JwtUtil.java (change farmId to String)" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Controllers:" -ForegroundColor White
Write-Host "   - AdminController.java (update all DTOs to use String IDs)" -ForegroundColor Gray
Write-Host ""
Write-Host "4. Configuration:" -ForegroundColor White
Write-Host "   - DataInitializer.java (pass String farmId)" -ForegroundColor Gray
Write-Host ""
Write-Host "5. Delete old JPA repository files:" -ForegroundColor White
Write-Host "   - JpaUserRepository.java" -ForegroundColor Gray
Write-Host "   - JpaFarmRepository.java" -ForegroundColor Gray
Write-Host "   - JpaDeviceRepository.java" -ForegroundColor Gray
Write-Host "   - JpaSensorRepository.java" -ForegroundColor Gray
Write-Host ""
Write-Host "Environment Setup:" -ForegroundColor Cyan
Write-Host "Set MONGODB_URI environment variable:" -ForegroundColor White
Write-Host '  $env:MONGODB_URI="mongodb://localhost:27017/agropulse"' -ForegroundColor Green
