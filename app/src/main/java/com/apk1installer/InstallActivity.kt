package com.apk1installer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.apk1installer.databinding.ActivityInstallBinding
import com.apk1installer.utils.ApkInstaller
import com.apk1installer.utils.FileHelper
import com.apk1installer.utils.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class InstallActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityInstallBinding
    private var currentFileUri: Uri? = null
    private var processedApkFile: File? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViews()
        handleIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }
    
    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        binding.processButton.setOnClickListener {
            processAndInstall()
        }
        
        binding.selectAnotherButton.setOnClickListener {
            selectAnotherFile()
        }
    }
    
    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_VIEW, Intent.ACTION_SEND -> {
                val uri = intent.data ?: intent.getParcelableExtra(Intent.EXTRA_STREAM)
                uri?.let { 
                    currentFileUri = it
                    displayFileInfo(it)
                }
            }
        }
    }
    
    private fun displayFileInfo(uri: Uri) {
        lifecycleScope.launch {
            try {
                val fileInfo = withContext(Dispatchers.IO) {
                    FileHelper.getFileInfo(this@InstallActivity, uri)
                }
                
                binding.fileNameTextView.text = "文件名: ${fileInfo.name}"
                binding.fileSizeTextView.text = "文件大小: ${FileHelper.formatFileSize(fileInfo.size)}"
                binding.filePathTextView.text = "文件路径: ${fileInfo.path}"
                
                // 检查是否是APK1文件
                if (fileInfo.name.endsWith(".apk.1", ignoreCase = true) || 
                    fileInfo.name.endsWith(".apk1", ignoreCase = true)) {
                    binding.processButton.text = "处理并安装APK"
                    binding.processButton.isEnabled = true
                } else if (fileInfo.name.endsWith(".apk", ignoreCase = true)) {
                    binding.processButton.text = "直接安装APK"
                    binding.processButton.isEnabled = true
                } else {
                    binding.processButton.text = "不支持的文件格式"
                    binding.processButton.isEnabled = false
                    Toast.makeText(this@InstallActivity, "请选择APK或APK1文件", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                Toast.makeText(this@InstallActivity, "文件信息获取失败: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.processButton.isEnabled = false
            }
        }
    }
    
    private fun processAndInstall() {
        currentFileUri?.let { uri ->
            if (!PermissionHelper.hasInstallPermission(this)) {
                PermissionHelper.requestInstallPermission(this)
                return
            }
            
            showProgress(true)
            
            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        processApkFile(uri)
                    }
                    
                    showProgress(false)
                    
                    if (result.success) {
                        result.file?.let { apkFile ->
                            processedApkFile = apkFile
                            installApk(apkFile)
                        }
                    } else {
                        Toast.makeText(this@InstallActivity, result.error ?: "处理失败", Toast.LENGTH_LONG).show()
                    }
                    
                } catch (e: Exception) {
                    showProgress(false)
                    Toast.makeText(this@InstallActivity, "处理过程中出错: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private suspend fun processApkFile(uri: Uri): ProcessResult {
        return try {
            val fileInfo = FileHelper.getFileInfo(this, uri)
            
            val processedFile = when {
                fileInfo.name.endsWith(".apk.1", ignoreCase = true) || 
                fileInfo.name.endsWith(".apk1", ignoreCase = true) -> {
                    // 处理APK1文件：重命名为APK
                    FileHelper.copyAndRenameApk1File(this, uri, fileInfo.name)
                }
                fileInfo.name.endsWith(".apk", ignoreCase = true) -> {
                    // 直接复制APK文件
                    FileHelper.copyApkFile(this, uri, fileInfo.name)
                }
                else -> {
                    return ProcessResult(false, null, "不支持的文件格式：${fileInfo.name}")
                }
            }
            
            // 验证处理后的APK文件
            val validationResult = FileHelper.validateApkContent(this, processedFile)
            if (!validationResult.isValid) {
                // 删除无效文件
                processedFile.delete()
                return ProcessResult(false, null, validationResult.message)
            }
            
            ProcessResult(true, processedFile, null)
        } catch (e: Exception) {
            ProcessResult(false, null, "文件处理失败: ${e.message}")
        }
    }
    
    private fun installApk(apkFile: File) {
        try {
            val apkUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                apkFile
            )
            
            ApkInstaller.installApk(this, apkUri)
            
        } catch (e: Exception) {
            Toast.makeText(this, "安装失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showProgress(show: Boolean) {
        binding.progressCard.visibility = if (show) View.VISIBLE else View.GONE
        binding.processButton.isEnabled = !show
        binding.selectAnotherButton.isEnabled = !show
        
        if (show) {
            binding.progressTextView.text = "正在处理文件..."
        }
    }
    
    private fun selectAnotherFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivity(Intent.createChooser(intent, "选择APK或APK1文件"))
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理临时文件
        processedApkFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
    }
    
    data class ProcessResult(
        val success: Boolean,
        val file: File?,
        val error: String?
    )
}