@echo off
REM Script to build Android project with proper Java version

echo Checking for Android Studio JDK...

REM Try to find JDK 11+ from Android Studio
set ANDROID_STUDIO_JDK=C:\Program Files\Android\Android Studio\jbr\bin\java.exe
if exist "%ANDROID_STUDIO_JDK%" (
    echo Found Android Studio JDK at: %ANDROID_STUDIO_JDK%
    "%ANDROID_STUDIO_JDK%" -version
    goto :build
)

REM Try alternative paths
set ANDROID_STUDIO_JDK=C:\Program Files\JetBrains\Android Studio\jbr\bin\java.exe
if exist "%ANDROID_STUDIO_JDK%" (
    echo Found Android Studio JDK at: %ANDROID_STUDIO_JDK%
    "%ANDROID_STUDIO_JDK%" -version
    goto :build
)

echo.
echo ============================================================
echo ERROR: Java 11+ is required to build this project
echo Current Java version is 8 (Java 8)
echo ============================================================
echo.
echo SOLUTIONS:
echo.
echo Option 1 - Use Android Studio (Recommended):
echo   1. Open Android Studio
echo   2. Open this project
echo   3. Click Run button (Shift+F10)
echo.
echo Option 2 - Install Java 11+:
echo   1. Download JDK 11+ from: https://adoptium.net/
echo   2. Install it
echo   3. Set JAVA_HOME environment variable
echo   4. Run this script again
echo.
echo Option 3 - Run from Gradle with specific JVM:
echo   Set ANDROID_STUDIO_JDK variable and run:
echo   gradlew -Dorg.gradle.java.home="path_to_jdk11" assembleDebug
echo.
pause
exit /b 1

:build
echo.
echo Building APK...
call gradlew.bat assembleDebug
pause

