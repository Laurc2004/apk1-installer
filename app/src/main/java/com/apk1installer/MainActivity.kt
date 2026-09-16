package com.apk1installer

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.apk1installer.databinding.ActivityMainBinding
import com.apk1installer.utils.PermissionHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: com.apk1installer.utils.AppPreferenceManager
    private var selectedFileUri: Uri? = null

    // 文件选择器：通过 SAF 访问用户选中的文件，不需要存储权限
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
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
        val componentName = ComponentName(this, "com.apk1installer.MainActivityAlias")
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
