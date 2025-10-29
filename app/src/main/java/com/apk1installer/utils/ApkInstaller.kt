package com.apk1installer.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

object ApkInstaller {
    
    /**
     * 安装APK文件
     */
    fun installApk(context: Context, apkUri: Uri) {
        // 检查安装权限
        if (!canInstallApk(context)) {
            throw Exception("没有安装未知应用的权限，请先授予权限")
        }
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        
        try {
            // 检查是否有应用可以处理安装Intent
            val packageManager = context.packageManager
            val resolveInfos = packageManager.queryIntentActivities(intent, 0)
            
            if (resolveInfos.isEmpty()) {
                throw Exception("系统中没有可用的APK安装程序")
            }
            
            context.startActivity(intent)
        } catch (e: Exception) {
            when {
                e.message?.contains("No Activity found") == true -> {
                    throw Exception("系统不支持APK安装或安装程序被禁用")
                }
                e.message?.contains("Permission") == true -> {
                    throw Exception("权限不足，无法启动安装程序")
                }
                else -> {
                    throw Exception("启动安装程序失败: ${e.message}")
                }
            }
        }
    }
    
    /**
     * 检查是否可以安装APK
     */
    fun canInstallApk(context: Context): Boolean {
        return PermissionHelper.hasInstallPermission(context)
    }
    
    /**
     * 获取APK包信息
     */
    fun getApkInfo(context: Context, apkPath: String): ApkInfo? {
        return try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageArchiveInfo(apkPath, 0)
            
            packageInfo?.let { info ->
                // 设置应用信息的源路径，以便获取图标和标签
                info.applicationInfo.sourceDir = apkPath
                info.applicationInfo.publicSourceDir = apkPath
                
                val appName = packageManager.getApplicationLabel(info.applicationInfo).toString()
                val packageName = info.packageName
                val versionName = info.versionName ?: "未知"
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    info.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    info.versionCode.toLong()
                }
                
                ApkInfo(
                    appName = appName,
                    packageName = packageName,
                    versionName = versionName,
                    versionCode = versionCode
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 检查应用是否已安装
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取已安装应用的版本信息
     */
    fun getInstalledAppVersion(context: Context, packageName: String): Long? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            null
        }
    }
    
    data class ApkInfo(
        val appName: String,
        val packageName: String,
        val versionName: String,
        val versionCode: Long
    )
}