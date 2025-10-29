# APK1 Installer - 多格式APK安装器

## 📱 项目简介

APK1 Installer 是一个功能强大的Android应用，专门用来处理多种APK文件格式。它能自动识别并处理`.apk1`、`.apk.1`和标准`.apk`文件，提供统一的安装体验，让用户无需手动重命名或转换文件。

### 🎯 解决的问题
- 某些应用分发平台使用`.apk.1`或`.apk1`扩展名
- 用户需要手动重命名为`.apk`才能安装
- 不同格式的APK文件处理方式不统一
- 这个应用自动化了整个识别、转换和安装过程

## ✨ 主要功能

- 🔄 **多格式支持**: 智能识别并处理`.apk1`、`.apk.1`和`.apk`三种文件格式
- 📱 **直接安装**: 处理完成后可直接安装APK文件  
- 🔗 **文件关联**: 支持从QQ、微信等应用直接分享文件到本应用
- 🎨 **简洁界面**: Material Design设计，操作简单直观
- 🙈 **隐藏图标**: 可选择隐藏应用图标，通过拨号恢复
- 🛡️ **隐私保护**: 所有处理都在本地进行，不上传任何数据
- ✅ **文件验证**: 自动验证APK文件完整性，确保安装安全

## 📥 下载安装

### 直接下载APK
前往 [Releases页面](https://github.com/your-username/apk1-installer/releases) 下载最新版本：

- **app-debug.apk**: 调试版本，包含调试信息，适合开发测试
- **app-release.apk**: 发布版本，经过优化和混淆，推荐日常使用

### 安装步骤
1. 下载对应的APK文件到Android设备
2. 在设备设置中启用"未知来源"或"安装未知应用"权限
3. 点击下载的APK文件进行安装
4. 首次运行时按照应用提示设置必要权限

## 🔨 本地构建

### 快速构建
```bash
# 使用提供的构建脚本（Windows）
build-apk.bat

# 或者手动构建
gradlew.bat assembleDebug    # 构建调试版本
gradlew.bat assembleRelease  # 构建发布版本
```

### 构建输出位置
- Debug版本: `app/build/outputs/apk/debug/app-debug.apk`
- Release版本: `app/build/outputs/apk/release/app-release-unsigned.apk`

## 🚀 快速开始

### 对于开发新手

1. **📋 查看快速指南**: 先阅读 `快速开始指南.md`
2. **🔧 运行配置脚本**: 双击 `setup.bat` 进行自动配置
3. **💻 打开IDEA**: 使用IntelliJ IDEA旗舰版打开项目
4. **⚡ 一键构建**: 在Gradle面板中双击 `assembleDebug`

### 对于有经验的开发者

```bash
# 克隆项目
git clone <项目地址>

# 进入项目目录
cd apk1-installer

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease
```

## 📁 项目结构

```
apk1-installer/
├── 📄 快速开始指南.md          # 新手必读
├── 📄 开发环境配置指南.md      # 详细配置说明
├── 🔧 setup.bat               # 自动配置脚本
├── 📱 app/
│   ├── src/main/
│   │   ├── java/com/apk1installer/
│   │   │   ├── MainActivity.kt      # 主界面
│   │   │   ├── InstallActivity.kt   # 安装界面
│   │   │   ├── SettingsActivity.kt  # 设置界面
│   │   │   └── utils/               # 工具类
│   │   ├── res/                     # 资源文件
│   │   └── AndroidManifest.xml      # 应用配置
│   └── build.gradle                 # 应用构建配置
├── build.gradle                     # 项目构建配置
└── gradle.properties               # Gradle配置
```

## 🛠️ 开发环境要求

- **Java**: JDK 11 或 JDK 17
- **IDE**: IntelliJ IDEA 旗舰版 (推荐) 或 Android Studio
- **Android SDK**: API 24-34
- **Gradle**: 8.0+ (项目自带Wrapper)

## 📱 使用方法

### 基本使用
1. 安装应用到Android设备
2. 首次打开会有权限设置引导
3. 选择以下任意格式的文件：
   - `.apk1` 文件（重命名后安装）
   - `.apk.1` 文件（重命名后安装）
   - `.apk` 文件（直接安装）
4. 点击"处理并安装"完成安装

### 文件关联使用  
1. 在QQ、微信中收到APK相关文件（`.apk1`、`.apk.1`或`.apk`）
2. 点击"用其他应用打开"
3. 选择"APK1安装器"
4. 应用会自动识别文件格式并处理，然后显示安装界面

### 支持的文件格式详解
- **`.apk1`**: 自动重命名为`.apk`格式并安装
- **`.apk.1`**: 自动重命名为`.apk`格式并安装  
- **`.apk`**: 直接验证并安装，无需转换

## 🔧 常见问题

### Q: 我是Android开发新手，不知道怎么开始？
**A**: 请按顺序阅读：
1. `快速开始指南.md` - 5分钟上手
2. `开发环境配置指南.md` - 详细配置说明
3. 运行 `setup.bat` 自动配置

### Q: IDEA提示找不到Android SDK？
**A**: 
1. 在IDEA中安装Android插件
2. 下载Android SDK (IDEA会自动提示)
3. 或手动设置SDK路径

### Q: Gradle同步失败？
**A**:
1. 检查网络连接
2. 确保Java版本正确 (11或17)
3. 尝试Clean Project后重新构建

### Q: 构建的APK在哪里？
**A**: 
- Debug版本: `app/build/outputs/apk/debug/app-debug.apk`
- Release版本: `app/build/outputs/apk/release/app-release.apk`

## 🎯 技术特点

- **原生开发**: 使用Kotlin + Android原生开发，性能优异
- **Material Design**: 遵循Google设计规范，界面现代化
- **权限最小化**: 只请求必要权限，保护用户隐私
- **本地处理**: 所有文件操作都在本地完成，安全可靠

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👨‍💻 作者

**Edan Liu**
- 📧 邮箱: liurc2004@outlook.com

## 🤝 贡献

欢迎提交Issue和Pull Request！

---

**💡 提示**: 如果您是Android开发新手，强烈建议先阅读 `快速开始指南.md` 和 `开发环境配置指南.md`！

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。

## 免责声明

本应用仅提供文件格式转换功能，用户需自行承担安装第三方应用的风险。请确保遵守当地法律法规，不要使用本应用进行任何违法活动。