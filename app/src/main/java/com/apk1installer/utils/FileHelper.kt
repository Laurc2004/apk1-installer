package com.apk1installer.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

object FileHelper {
    
    data class FileInfo(
        val name: String,
        val size: Long,
        val path: String
    )
    
    data class ValidationResult(
        val isValid: Boolean,
        val message: String
    )
    
    /**
     * 获取文件信息
     */
    fun getFileInfo(context: Context, uri: Uri): FileInfo {
        var name = "未知文件"
        var size = 0L
        var path = uri.toString()
        
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex) ?: "未知文件"
                }
                
                if (sizeIndex >= 0) {
                    size = cursor.getLong(sizeIndex)
                }
            }
        }
        
        // 尝试从URI路径获取更多信息
        uri.path?.let { uriPath ->
            if (path == uri.toString()) {
                path = uriPath
            }
        }
        
        return FileInfo(name, size, path)
    }
    
    /**
     * 复制并重命名APK1文件为APK文件
     */
    fun copyAndRenameApk1File(context: Context, uri: Uri, originalName: String): File {
        val apkName = when {
            originalName.endsWith(".apk.1", ignoreCase = true) -> {
                originalName.substring(0, originalName.length - 2) // 移除 ".1"
            }
            originalName.endsWith(".apk1", ignoreCase = true) -> {
                originalName.substring(0, originalName.length - 1) // 移除 "1"
            }
            else -> {
                "${originalName}.apk"
            }
        }
        
        return copyFileToCache(context, uri, apkName)
    }
    
    /**
     * 复制APK文件
     */
    fun copyApkFile(context: Context, uri: Uri, originalName: String): File {
        return copyFileToCache(context, uri, originalName)
    }
    
    /**
     * 复制文件到缓存目录
     */
    private fun copyFileToCache(context: Context, uri: Uri, fileName: String): File {
        val cacheDir = File(context.cacheDir, "apk_files")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        
        // 清理旧文件
        cleanOldFiles(cacheDir)
        
        val targetFile = File(cacheDir, fileName)
        
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(targetFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: throw Exception("无法打开文件")
        
        return targetFile
    }
    
    /**
     * 清理缓存目录中的旧文件
     */
    private fun cleanOldFiles(cacheDir: File) {
        try {
            cacheDir.listFiles()?.forEach { file ->
                if (file.isFile && System.currentTimeMillis() - file.lastModified() > 24 * 60 * 60 * 1000) {
                    // 删除超过24小时的文件
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // 忽略清理错误
        }
    }
    
    /**
     * 格式化文件大小
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        
        return DecimalFormat("#,##0.#").format(
            bytes / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }
    
    /**
     * 验证APK文件
     */
    fun isValidApkFile(file: File): Boolean {
        return try {
            if (!file.exists() || file.length() <= 0) {
                return false
            }
            
            // 检查文件扩展名
            if (!file.name.endsWith(".apk", ignoreCase = true)) {
                return false
            }
            
            // 检查APK文件头（ZIP文件头）
            file.inputStream().use { inputStream ->
                val header = ByteArray(4)
                val bytesRead = inputStream.read(header)
                if (bytesRead < 4) return false
                
                // ZIP文件头标识：PK (0x504B)
                return header[0] == 0x50.toByte() && header[1] == 0x4B.toByte()
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 验证APK文件内容完整性
     */
    fun validateApkContent(context: Context, file: File): ValidationResult {
        return try {
            if (!isValidApkFile(file)) {
                return ValidationResult(false, "文件不是有效的APK格式")
            }
            
            // 尝试解析APK包信息
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageArchiveInfo(file.absolutePath, 0)
            
            if (packageInfo == null) {
                return ValidationResult(false, "APK文件损坏或格式不正确")
            }
            
            // 检查包名是否有效
            if (packageInfo.packageName.isNullOrBlank()) {
                return ValidationResult(false, "APK包名无效")
            }
            
            ValidationResult(true, "APK文件验证通过")
        } catch (e: Exception) {
            ValidationResult(false, "APK验证失败: ${e.message}")
        }
    }
    
    /**
     * 获取文件扩展名
     */
    fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex > 0 && lastDotIndex < fileName.length - 1) {
            fileName.substring(lastDotIndex + 1).lowercase()
        } else {
            ""
        }
    }
    
    /**
     * 检查是否是APK1文件
     */
    fun isApk1File(fileName: String): Boolean {
        return fileName.endsWith(".apk.1", ignoreCase = true) || 
               fileName.endsWith(".apk1", ignoreCase = true)
    }
    
    /**
     * 检查是否是APK文件
     */
    fun isApkFile(fileName: String): Boolean {
        return fileName.endsWith(".apk", ignoreCase = true)
    }
}