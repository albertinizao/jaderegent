$nodePath = Join-Path $PSScriptRoot "target\node"
$env:Path = "$nodePath;$env:Path"

if ($args.Count -eq 0) {
    Write-Host "Usage: .\dev.ps1 [npm command]"
    Write-Host "Example: .\dev.ps1 npm run dev"
    Write-Host "Example: .\dev.ps1 npm install"
    exit
}

$cmd = $args[0]
$cmdArgs = $args | Select-Object -Skip 1

if ($cmd -eq "npm") {
    & "$nodePath\npm.cmd" @cmdArgs
} elseif ($cmd -eq "npx") {
    & "$nodePath\npx.cmd" @cmdArgs
} else {
    Write-Host "Unknown command: $cmd"
    exit 1
}
exit $LASTEXITCODE
