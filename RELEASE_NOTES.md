## v1.1.0

### Fixes
- 修复初次安装出现两个启动图标，只保留一个 LAUNCHER 入口
- 修复 debug 包隐藏/恢复图标时的崩溃（组件名解析错误）
- Android 13+ 不再启动时请求存储权限，改用 SAF 选文件
- 更换启动图标并补充自适应图标资源

### CI
- 修复 GitHub Actions 在 Setup Android SDK 步骤的失败，release/debug 包均可正常构建
