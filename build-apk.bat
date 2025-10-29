@echo off
echo ========================================
echo APK1 Installer - 构建脚本
echo ========================================
echo.

echo 正在检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java环境，请确保已安装JDK 17
    echo 注意: Android Gradle Plugin 8.0.2 需要 JDK 17
    pause
    exit /b 1
)

echo Java环境检查通过
echo 注意: 如果构建失败，请确保使用JDK 17
echo.

echo 正在清理之前的构建...
call gradlew.bat clean
if errorlevel 1 (
    echo 清理失败，但继续构建...
)

echo.
echo 正在构建Debug版本...
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo Debug版本构建失败！
    pause
    exit /b 1
)

echo.
echo 正在构建Release版本...
call gradlew.bat assembleRelease
if errorlevel 1 (
    echo Release版本构建失败，但Debug版本已成功构建
)

echo.
echo ========================================
echo 构建完成！
echo ========================================
echo.

if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✅ Debug APK: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo ❌ Debug APK构建失败
)

if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
    echo ✅ Release APK: app\build\outputs\apk\release\app-release-unsigned.apk
) else (
    echo ❌ Release APK构建失败
)

echo.
echo 提示: 
echo - Debug版本包含调试信息，文件较大
echo - Release版本经过优化，推荐用于分发
echo - Release版本未签名，需要自行签名后才能安装
echo.

pause