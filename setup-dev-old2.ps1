# KINEDICAL Development Setup Script
# Tích hợp tất cả services và kiểm tra cấu hình

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  KINEDICAL Development Setup" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

# Step 1: Kiểm tra Docker
Write-Host "`n[1/5] Checking Docker..." -ForegroundColor Green
try {
    $dockerVersion = docker --version
    Write-Host "✓ Docker found: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker not found! Please install Docker Desktop." -ForegroundColor Red
    exit 1
}

# Step 2: Kiểm tra docker-compose.yml
Write-Host "`n[2/5] Checking docker-compose.yml..." -ForegroundColor Green
$dockerComposeExists = Test-Path "docker-compose.yml"
if ($dockerComposeExists) {
    Write-Host "✓ docker-compose.yml found" -ForegroundColor Green
} else {
    Write-Host "✗ docker-compose.yml not found in $projectRoot" -ForegroundColor Red
    exit 1
}

# Step 3: Kiểm tra Node.js (cho frontend)
Write-Host "`n[3/5] Checking Node.js..." -ForegroundColor Green
try {
    $nodeVersion = node --version
    Write-Host "✓ Node.js found: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "⚠ Node.js not found. Frontend dev mode won't work." -ForegroundColor Yellow
}

# Step 4: Kiểm tra Java (cho backend)
Write-Host "`n[4/5] Checking Java..." -ForegroundColor Green
try {
    $javaVersion = java -version 2>&1
    Write-Host "✓ Java found" -ForegroundColor Green
} catch {
    Write-Host "⚠ Java not found. Backend compilation might fail." -ForegroundColor Yellow
}

# Step 5: Kiểm tra Python (cho recommendation API)
Write-Host "`n[5/5] Checking Python..." -ForegroundColor Green
try {
    $pythonVersion = python --version 2>&1
    Write-Host "✓ Python found: $pythonVersion" -ForegroundColor Green
} catch {
    Write-Host "⚠ Python not found. Recommendation API won't work locally." -ForegroundColor Yellow
}

# Summary
Write-Host "`n==================================================" -ForegroundColor Green
Write-Host "  Setup Complete! Ready to start development" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

Write-Host "`nNext steps:" -ForegroundColor Cyan
Write-Host "  1. Start Docker stack:" -ForegroundColor White
Write-Host "     .\start-all.ps1" -ForegroundColor Yellow
Write-Host "`n  2. In a new terminal, start frontend:" -ForegroundColor White
Write-Host "     cd frontend; npm install; npm start" -ForegroundColor Yellow
Write-Host "`n  3. Open browser:" -ForegroundColor White
Write-Host "     http://localhost:3000  (Frontend)" -ForegroundColor Yellow
Write-Host "     http://localhost:8080/swagger-ui.html  (Backend API)" -ForegroundColor Yellow
Write-Host "     http://localhost:8000/docs  (Recommendation API)" -ForegroundColor Yellow

# Step 6: Tự động khởi động Docker stack
Write-Host "`n[6/8] Starting Docker stack..." -ForegroundColor Green
try {
    Write-Host "Building and starting services..." -ForegroundColor Yellow
    docker compose up --build -d
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Docker stack started successfully!" -ForegroundColor Green
    } else {
        Write-Host "✗ Docker compose failed with exit code $LASTEXITCODE" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Failed to start Docker stack: $_" -ForegroundColor Red
    Write-Host "Try running: docker compose up --build" -ForegroundColor Yellow
    exit 1
}

# Step 7: Chờ services sẵn sàng
Write-Host "`n[7/8] Waiting for services to be ready..." -ForegroundColor Green
$maxRetries = 30
$retryCount = 0

do {
    $servicesReady = $true
    $retryCount++

    # Kiểm tra MongoDB
    try {
        $mongoCheck = docker compose exec -T mongo mongosh --eval "db.adminCommand('ping')" 2>$null
        if ($LASTEXITCODE -ne 0) { $servicesReady = $false }
    } catch { $servicesReady = $false }

    # Kiểm tra Backend
    try {
        $backendResponse = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 5 -ErrorAction SilentlyContinue
        if ($backendResponse.StatusCode -ne 200) { $servicesReady = $false }
    } catch { $servicesReady = $false }

    # Kiểm tra Recommendation API
    try {
        $recommendResponse = Invoke-WebRequest -Uri "http://localhost:8000/health" -TimeoutSec 5 -ErrorAction SilentlyContinue
        if ($recommendResponse.StatusCode -ne 200) { $servicesReady = $false }
    } catch { $servicesReady = $false }

    if (-not $servicesReady) {
        Write-Host "   Waiting... ($retryCount/$maxRetries)" -ForegroundColor Yellow
        Start-Sleep -Seconds 10
    }
} while (-not $servicesReady -and $retryCount -lt $maxRetries)

if ($servicesReady) {
    Write-Host "✓ All services are ready!" -ForegroundColor Green
} else {
    Write-Host "⚠ Services may not be fully ready. Check with: docker compose ps" -ForegroundColor Yellow
}

# Step 8: Thiết lập Frontend
Write-Host "`n[8/8] Setting up frontend..." -ForegroundColor Green

if (Test-Path "frontend") {
    Set-Location "frontend"

    # Kiểm tra package.json
    if (Test-Path "package.json") {
        Write-Host "Installing frontend dependencies..." -ForegroundColor Yellow
        try {
            npm install
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✓ Frontend dependencies installed" -ForegroundColor Green
            } else {
                Write-Host "⚠ npm install failed. You may need to run it manually." -ForegroundColor Yellow
            }
        } catch {
            Write-Host "⚠ npm install failed: $_" -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠ package.json not found in frontend directory" -ForegroundColor Yellow
    }

    Set-Location $projectRoot
} else {
    Write-Host "⚠ Frontend directory not found" -ForegroundColor Yellow
}

# Final Summary
Write-Host "`n==================================================" -ForegroundColor Green
Write-Host "  KINEDICAL Development Environment Ready!" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

Write-Host "`nAccess your application:" -ForegroundColor Cyan
Write-Host "  Frontend:     http://localhost:3000" -ForegroundColor White
Write-Host "  Backend API:  http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "  Recommend API: http://localhost:8000/docs" -ForegroundColor White
Write-Host "  MongoDB:      localhost:27017" -ForegroundColor White

Write-Host "`nUseful commands:" -ForegroundColor Cyan
Write-Host "  View logs:    docker compose logs -f" -ForegroundColor White
Write-Host "  Stop stack:   docker compose down" -ForegroundColor White
Write-Host "  Start frontend manually: cd frontend; npm start" -ForegroundColor White

Write-Host "`nHappy coding!" -ForegroundColor Green