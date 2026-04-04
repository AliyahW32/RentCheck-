@echo off
setlocal

set "BASE_DIR=%~dp0"
if "%BASE_DIR:~-1%"=="\" set "BASE_DIR=%BASE_DIR:~0,-1%"

set "WRAPPER_DIR=%BASE_DIR%\.mvn\wrapper"
set "MAVEN_VERSION=3.9.9"
set "MAVEN_FOLDER=apache-maven-%MAVEN_VERSION%"
set "MAVEN_HOME=%WRAPPER_DIR%\%MAVEN_FOLDER%"
set "MAVEN_ZIP=%WRAPPER_DIR%\%MAVEN_FOLDER%-bin.zip"
set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
set "DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/%MAVEN_FOLDER%-bin.zip"

if not exist "%MVN_CMD%" (
  echo Maven %MAVEN_VERSION% not found in project wrapper cache.
  echo Downloading Maven wrapper distribution...

  if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"

  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ProgressPreference='SilentlyContinue';" ^
    "Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%MAVEN_ZIP%';" ^
    "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%WRAPPER_DIR%' -Force"

  if errorlevel 1 (
    echo Failed to download or extract Maven.
    exit /b 1
  )
)

call "%MVN_CMD%" %*
exit /b %errorlevel%
