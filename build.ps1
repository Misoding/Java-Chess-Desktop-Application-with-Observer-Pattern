<#
.SYNOPSIS
    Build script for Java Chess Application (Windows PowerShell)

.DESCRIPTION
    Provides compilation, execution, and cleanup functionality for the Chess project
    on Windows systems without requiring GNU Make.

.PARAMETER Action
    The build action to perform: compile, run, test, clean, help

.EXAMPLE
    .\build.ps1 compile
    .\build.ps1 run
    .\build.ps1 test
    .\build.ps1 clean
#>

param(
    [Parameter(Position=0)]
    [ValidateSet("compile", "run", "test", "clean", "help", "all")]
    [string]$Action = "help"
)

# Configuration
$SrcDir = "src"
$OutDir = "out"
$LibDir = "lib"
$JsonLib = Join-Path $LibDir "json-simple-1.1.1.jar"
$MainClass = "main_package.Main"
$TestClass = "Testare.ChessTestRunner"
$JavacFlags = @("-encoding", "UTF-8", "-Xlint:unchecked", "-Xlint:deprecation")

# Verify Java installation
function Test-JavaInstalled {
    try {
        $null = & javac -version 2>&1
        return $true
    } catch {
        Write-Error "ERROR: 'javac' not found. Please install JDK 8 or higher."
        return $false
    }
}

# Compile all Java sources
function Invoke-Compile {
    Write-Host "[COMPILE] Building Java sources..." -ForegroundColor Cyan
    
    if (-not (Test-Path $JsonLib)) {
        Write-Error "ERROR: Missing dependency: $JsonLib"
        exit 1
    }
    
    # Create output directory
    if (-not (Test-Path $OutDir)) {
        New-Item -ItemType Directory -Path $OutDir | Out-Null
    }
    
    # Find all Java files
    $sources = Get-ChildItem -Path $SrcDir -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
    
    if ($sources.Count -eq 0) {
        Write-Error "ERROR: No .java files found in $SrcDir"
        exit 1
    }
    
    Write-Host "  Found $($sources.Count) source files"
    
    # Compile
    $compileArgs = $JavacFlags + @(
        "-d", $OutDir,
        "-cp", $JsonLib,
        "-sourcepath", $SrcDir
    ) + $sources
    
    & javac @compileArgs
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[COMPILE] Done. Output: $OutDir/" -ForegroundColor Green
    } else {
        Write-Error "[COMPILE] Compilation failed!"
        exit 1
    }
}

# Run the GUI application
function Invoke-Run {
    Invoke-Compile
    Write-Host "[RUN] Starting Chess GUI..." -ForegroundColor Cyan
    $classpath = "$OutDir;$JsonLib"
    & java -cp $classpath $MainClass
}

# Run the test harness
function Invoke-Test {
    Invoke-Compile
    Write-Host "[TEST] Starting Console Test Runner..." -ForegroundColor Cyan
    $classpath = "$OutDir;$JsonLib"
    & java -cp $classpath $TestClass
}

# Clean build artifacts
function Invoke-Clean {
    Write-Host "[CLEAN] Removing compiled artifacts..." -ForegroundColor Cyan
    if (Test-Path $OutDir) {
        Remove-Item -Recurse -Force $OutDir
        Write-Host "[CLEAN] Done." -ForegroundColor Green
    } else {
        Write-Host "[CLEAN] Nothing to clean." -ForegroundColor Yellow
    }
}

# Display help
function Show-Help {
    Write-Host @"
==========================================
 Chess Application - Build Script
==========================================

Usage: .\build.ps1 <action>

Actions:
  compile   Compile all Java sources to out/
  run       Compile and launch the GUI application
  test      Compile and run the console test harness
  clean     Remove compiled artifacts
  help      Show this message

Examples:
  .\build.ps1 compile
  .\build.ps1 run
  .\build.ps1 test
  .\build.ps1 clean

Requirements:
  - JDK 8 or higher (javac, java in PATH)

"@ -ForegroundColor White
}

# Main execution
if (-not (Test-JavaInstalled)) {
    exit 1
}

switch ($Action) {
    "compile" { Invoke-Compile }
    "all"     { Invoke-Compile }
    "run"     { Invoke-Run }
    "test"    { Invoke-Test }
    "clean"   { Invoke-Clean }
    "help"    { Show-Help }
    default   { Show-Help }
}
