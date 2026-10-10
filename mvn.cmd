@echo off
setlocal

set "REAL_MVN="
for /f "delims=" %%I in ('where.exe mvn.cmd 2^>nul') do (
  if /I not "%%~fI"=="%~f0" if not defined REAL_MVN set "REAL_MVN=%%~fI"
)

if not defined REAL_MVN (
  echo ERROR: Maven was not found on PATH. Install Maven and add its bin directory to PATH.
  exit /b 1
)

if /I "%~1"=="spring-boot" if "%~2"=="" (
  call "%REAL_MVN%" spring-boot:run
  exit /b %ERRORLEVEL%
)

call "%REAL_MVN%" %*
exit /b %ERRORLEVEL%
