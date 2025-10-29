@echo off
chcp 65001 >nul
echo ========================================
echo    APK1安装器 - 自动配置脚本
echo ========================================
echo.

echo [1/4] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 未检测到Java，请先安装Java 17
    echo 下载地址: https://adoptium.net/temurin/releases/?version=17
    pause
    exit /b 1
) else (
    echo ✅ Java环境正常
)

echo.
echo [2/4] 检查项目文件...
if not exist "app\build.gradle" (
    echo ❌ 项目文件不完整，请确保在正确的项目目录中运行
    pause
    exit /b 1
) else (
    echo ✅ 项目文件完整
)

echo.
echo [3/4] 创建本地配置文件...
if not exist "local.properties" (
    echo # 自动生成的本地配置文件 > local.properties
    echo # 如果Android SDK路径不正确，请手动修改 >> local.properties
    echo sdk.dir=C\:\\Users\\%USERNAME%\\AppData\\Local\\Android\\Sdk >> local.properties
    echo ✅ 已创建 local.properties 文件
) else (
    echo ✅ local.properties 文件已存在
)

echo.
echo [4/4] 检查Gradle Wrapper...
if not exist "gradlew.bat" (
    echo ❌ Gradle Wrapper 文件缺失
    echo 请在IDEA中打开项目，让IDEA自动生成这些文件
) else (
    echo ✅ Gradle Wrapper 文件存在
)

echo.
echo ========================================
echo           配置完成！
echo ========================================
echo.
echo 下一步操作：
echo 1. 打开 IntelliJ IDEA 旗舰版
echo 2. 选择 "Open" 并选择当前文件夹
echo 3. 等待IDEA自动配置Android开发环境
echo 4. 在右侧Gradle面板中双击 assembleDebug 构建APK
echo.
echo 如果遇到问题，请查看：
echo - 快速开始指南.md
echo - 开发环境配置指南.md
echo.
pause