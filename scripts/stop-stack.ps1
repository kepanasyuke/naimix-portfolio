$ports = 8081, 8082, 8083, 8084, 8085, 8086, 8087, 8090

foreach ($port in $ports) {
    Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue |
        ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
    Write-Host "  порт $port освобождён"
}

$dataPorts = 5440, 6379
foreach ($port in $dataPorts) {
    $proc = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($proc) {
        Write-Host "  данные на $port оставлены запущенными (остановите вручную при необходимости)"
    }
}

Write-Host "`nСервисы остановлены."
