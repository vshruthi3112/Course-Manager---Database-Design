# Maven Wrapper for PowerShell
# Runs Maven from the local installation at ~/apache-maven-3.9.6
# Usage: .\mvnw.ps1 spring-boot:run
#        .\mvnw.ps1 compile
#        .\mvnw.ps1 clean package

$MavenHome = Join-Path $env:USERPROFILE "apache-maven-3.9.6"
$MvnExe = Join-Path $MavenHome "bin\mvn.cmd"

if (-not (Test-Path $MvnExe)) {
    Write-Error "Maven not found at $MavenHome. Please run the setup again."
    exit 1
}

# Run Maven with all passed arguments
& cmd /c "$MvnExe $($args -join ' ')"
exit $LASTEXITCODE
