param(
    [switch]$SkipData,
    [switch]$NoWait
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$tools = "C:\Users\Admin\Tools"
$logs = Join-Path $root "logs"
New-Item -ItemType Directory -Force -Path $logs | Out-Null

function Test-Port([int]$port) {
    return [bool](Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue)
}

function Start-Detached([string]$title, [string]$command) {
    $line = "cmd /c `"$command`""
    Start-Process cmd -ArgumentList "/c $command" -WindowStyle Hidden
    Write-Host "  -> $title"
}

if (-not $SkipData) {
    if (-not (Test-Port 5440)) {
        Write-Host "PostgreSQL 16.2 : старт на 127.0.0.1:5440"
        Start-Process -FilePath "$tools\pgsql\bin\postgres.exe" `
            -ArgumentList @('-D', "$tools\pgdata2", '-p', '5440', '-c', 'listen_addresses=127.0.0.1') `
            -RedirectStandardOutput "$tools\pg2.out" -RedirectStandardError "$tools\pg2.err" -WindowStyle Hidden
        Start-Sleep -Seconds 5
    } else {
        Write-Host "PostgreSQL 16.2 : уже работает на 5440"
    }

    if (-not (Test-Port 6379)) {
        Write-Host "Redis           : старт на 127.0.0.1:6379"
        Start-Process -FilePath "$tools\redis\redis-server.exe" -ArgumentList "--port 6379" -WindowStyle Hidden
        Start-Sleep -Seconds 2
    } else {
        Write-Host "Redis           : уже работает на 6379"
    }
}

$dbEnv = "set DB_USER=postgres&& set DB_PASSWORD=postgres&& set REDIS_HOST=127.0.0.1"

$services = @(
    @{ Name = "auth-service";         Port = 8081; Env = "$dbEnv&& set DB_URL=jdbc:postgresql://127.0.0.1:5440/auth" },
    @{ Name = "vacancy-service";      Port = 8082; Env = "$dbEnv&& set DB_URL=jdbc:postgresql://127.0.0.1:5440/vacancy&& set CACHE_TYPE=simple" },
    @{ Name = "matching-service";     Port = 8083; Env = "set X=1" },
    @{ Name = "notification-service"; Port = 8084; Env = "$dbEnv&& set DB_URL=jdbc:postgresql://127.0.0.1:5440/notification" },
    @{ Name = "billing-service";      Port = 8085; Env = "$dbEnv&& set DB_URL=jdbc:postgresql://127.0.0.1:5440/billing" },
    @{ Name = "analytics-service";    Port = 8086; Env = "set REDIS_HOST=127.0.0.1&& set ANALYTICS_STORE=redis" },
    @{ Name = "ai-assistant-service"; Port = 8087; Env = "set X=1" },
    @{ Name = "workspace-service";    Port = 8090; Env = "set X=1" }
)

Write-Host "`nСервисы (последовательный старт, лимит памяти 320 МБ):"
foreach ($service in $services) {
    if (Test-Port $service.Port) {
        Write-Host "  == $($service.Name) уже слушает $($service.Port)"
        continue
    }
    $jar = Join-Path $root "services\$($service.Name)\target\$($service.Name)-1.0.0-SNAPSHOT.jar"
    if (-not (Test-Path $jar)) {
        Write-Host "  !! $($service.Name): нет jar, соберите 'mvn package -DskipTests'"
        continue
    }
    $log = Join-Path $logs "$($service.Name).log"
    $java = "java -Xmx256m -Xss512k -XX:ActiveProcessorCount=2 -XX:MaxMetaspaceSize=128m -XX:TieredStopAtLevel=1 -jar `"$jar`""
    $command = "$($service.Env)&& $java > `"$log`" 2>&1"
    Start-Process cmd -ArgumentList "/c $command" -WindowStyle Hidden

    $deadline = (Get-Date).AddSeconds(120)
    $up = $false
    while ((Get-Date) -lt $deadline) {
        Start-Sleep -Seconds 3
        if (Test-Port $service.Port) { $up = $true; break }
    }
    $state = if ($up) { "OK" } else { "не поднялся" }
    Write-Host ("  {0,-22} {1,-6} {2}" -f $service.Name, $service.Port, $state)
}

if ($NoWait) {
    exit 0
}

Write-Host "`nОжидание готовности..."
foreach ($service in $services) {
    $deadline = (Get-Date).AddSeconds(150)
    $up = $false
    while ((Get-Date) -lt $deadline) {
        if (Test-Port $service.Port) { $up = $true; break }
        Start-Sleep -Seconds 3
    }
    $state = if ($up) { "OK" } else { "не поднялся" }
    Write-Host ("  {0,-22} {1,-6} {2}" -f $service.Name, $service.Port, $state)
}

Write-Host @"

Готово. Точки входа:
  Бизнес-приложение   http://127.0.0.1:8090/api/workspace/overview
  Кабинет (UI)        web/business.html
  Портфолио           web/index.html
  Actuator            http://127.0.0.1:8081/actuator/health

Остановка: scripts/stop-stack.ps1
"@
