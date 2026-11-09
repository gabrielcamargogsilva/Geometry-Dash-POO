$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$buildDirectory = Join-Path $projectRoot "build"
$jarPath = Join-Path $buildDirectory "Geometry-Dash-POO.jar"
$stagingDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("geometry-dash-poo-" + [Guid]::NewGuid().ToString("N"))
$classesDirectory = Join-Path $stagingDirectory "classes"

try {
    New-Item -ItemType Directory -Force -Path $classesDirectory, $buildDirectory | Out-Null
    $sources = Get-ChildItem -Path (Join-Path $projectRoot "src") -Recurse -Filter *.java |
        ForEach-Object { $_.FullName }

    if (-not $sources) {
        throw "Nenhum arquivo Java foi encontrado em src."
    }

    & javac --release 17 -encoding UTF-8 -d $classesDirectory $sources
    if ($LASTEXITCODE -ne 0) {
        throw "A compilação do projeto falhou."
    }

    Copy-Item -Path (Join-Path $projectRoot "resources\*") -Destination $classesDirectory -Recurse -Force
    & jar --create --file $jarPath --main-class geometrydash.Main -C $classesDirectory .
    if ($LASTEXITCODE -ne 0) {
        throw "A criação do JAR falhou."
    }

    Write-Output "JAR criado: $jarPath"
}
finally {
    if (Test-Path -LiteralPath $stagingDirectory) {
        Remove-Item -LiteralPath $stagingDirectory -Recurse -Force
    }
}
