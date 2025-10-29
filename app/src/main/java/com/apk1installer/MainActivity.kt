package com.apk1installer

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apk1installer.databinding.ActivityMainBinding
import com.apk1installer.utils.PermissionHelper

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: com.apk1installer.utils.AppPreferenceManager
    private var selectedFileUri: Uri? = null
    
    // 文件选择器
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedFileUri = uri
                binding.installButton.isEnabled = true
                showFileInfo(uri)
            }
        }
    }
    
    // 权限请求
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            showPermissionDeniedDialog()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferenceManager = com.apk1installer.utils.AppPreferenceManager(this)
        
        // 首次启动检查
        if (preferenceManager.isFirstLaunch()) {
            showFirstLaunchDialog()
        }
        
        setupViews()
        checkPermissions()
    }
    
    private fun setupViews() {
        binding.selectFileButton.setOnClickListener {
            openFilePicker()
        }
        
        binding.installButton.setOnClickListener {
            selectedFileUri?.let { uri ->
                startInstallActivity(uri)
            }
        }
        
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        binding.privacyButton.setOnClickListener {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }
    }
    
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/vnd.android.package-archive", "*/*"))
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "选择APK1文件"))
    }
    
    private fun showFileInfo(uri: Uri) {
        try {
            val fileName = getFileName(uri)
            Toast.makeText(this, "已选择: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "文件信息获取失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getFileName(uri: Uri): String {
        var fileName = "未知文件"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }
    
    private fun startInstallActivity(uri: Uri) {
        val intent = Intent(this, InstallActivity::class.java).apply {
            data = uri
        }
        startActivity(intent)
    }
    
    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        
        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }
    
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("权限需要")
            .setMessage("应用需要存储权限来访问APK文件，请在设置中授予权限。")
            .setPositiveButton("前往设置") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showFirstLaunchDialog() {
        AlertDialog.Builder(this)
            .setTitle("欢迎使用APK1安装器")
            .setMessage("首次使用需要授予安装权限。点击确定前往设置页面，找到\"安装未知应用\"并允许此应用安装其他应用。")
            .setPositiveButton("前往设置") { _, _ ->
                PermissionHelper.requestInstallPermission(this)
                preferenceManager.setFirstLaunchCompleted()
            }
            .setNegativeButton("稍后设置") { _, _ ->
                preferenceManager.setFirstLaunchCompleted()
            }
            .setCancelable(false)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        // 检查图标隐藏状态
        updateIconVisibility()
    }
    
    private fun updateIconVisibility() {
        val hideIcon = preferenceManager.shouldHideIcon()
        val componentName = ComponentName(this, "${packageName}.MainActivityAlias")
        val newState = if (hideIcon) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }
        
        packageManager.setComponentEnabledSetting(
            componentName,
            newState,
            PackageManager.DONT_KILL_APP
        )
    }
}