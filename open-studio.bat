@echo off
echo Opening Android Studio with LNRead project...

REM Try different Android Studio paths
if exist "C:\Program Files\Android\Android Studio\bin\studio64.exe" (
    start "" "C:\Program Files\Android\Android Studio\bin\studio64.exe" "%CD%"
    echo Android Studio is opening...
) else if exist "C:\Program Files\JetBrains\Android Studio\bin\studio64.exe" (
    start "" "C:\Program Files\JetBrains\Android Studio\bin\studio64.exe" "%CD%"
    echo Android Studio is opening...
) else (
    echo.
    echo Could not find Android Studio installation.
    echo.
    echo Please open Android Studio manually and open this folder:
    echo %CD%
    echo.
    echo Then press the Run button (or Shift+F10) to build and run the app.
    echo.
    pause
)

