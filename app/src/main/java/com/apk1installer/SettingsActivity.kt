package com.apk1installer

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.apk1installer.utils.AppPreferenceManager

class SettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "设置"
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        
        private lateinit var preferenceManager: AppPreferenceManager
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            
            preferenceManager = AppPreferenceManager(requireContext())
            
            setupPreferences()
        }
        
        private fun setupPreferences() {
            // 隐藏图标设置
            findPreference<SwitchPreferenceCompat>("hide_icon")?.apply {
                isChecked = this@SettingsFragment.preferenceManager.shouldHideIcon()
                setOnPreferenceChangeListener { _, newValue ->
                    val hide = newValue as Boolean
                    if (hide) {
                        showHideIconConfirmDialog {
                            this@SettingsFragment.preferenceManager.setHideIcon(true)
                            updateIconVisibility(true)
                            Toast.makeText(context, "图标已隐藏，拨号*#*#1234#*#*可恢复", Toast.LENGTH_LONG).show()
                        }
                        false // 先不更新UI，等用户确认后再更新
                    } else {
                        this@SettingsFragment.preferenceManager.setHideIcon(false)
                        updateIconVisibility(false)
                        Toast.makeText(context, "图标已显示", Toast.LENGTH_SHORT).show()
                        true
                    }
                }
            }
            
            // 自动安装设置
            findPreference<SwitchPreferenceCompat>("auto_install")?.apply {
                isChecked = this@SettingsFragment.preferenceManager.isAutoInstallEnabled()
                setOnPreferenceChangeListener { _, newValue ->
                    this@SettingsFragment.preferenceManager.setAutoInstall(newValue as Boolean)
                    true
                }
            }
            
            // 关于信息
            findPreference<Preference>("about")?.apply {
                summary = "版本 ${getVersionName()}"
                setOnPreferenceClickListener {
                    showAboutDialog()
                    true
                }
            }
            
            // 恢复图标（隐藏的选项）
            findPreference<Preference>("restore_icon")?.apply {
                isVisible = this@SettingsFragment.preferenceManager.shouldHideIcon()
                setOnPreferenceClickListener {
                    this@SettingsFragment.preferenceManager.setHideIcon(false)
                    updateIconVisibility(false)
                    findPreference<SwitchPreferenceCompat>("hide_icon")?.isChecked = false
                    isVisible = false
                    Toast.makeText(context, "图标已恢复显示", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
        
        private fun showHideIconConfirmDialog(onConfirm: () -> Unit) {
            AlertDialog.Builder(requireContext())
                .setTitle("隐藏应用图标")
                .setMessage("确定要隐藏应用图标吗？\n\n隐藏后可以通过以下方式恢复：\n1. 拨号输入 *#*#1234#*#*\n2. 通过文件管理器打开APK1文件\n3. 在设置中恢复显示")
                .setPositiveButton("确定") { _, _ ->
                    onConfirm()
                }
                .setNegativeButton("取消", null)
                .show()
        }
        
        private fun updateIconVisibility(hide: Boolean) {
            val componentName = ComponentName(requireContext(), "${requireContext().packageName}.MainActivityAlias")
            val newState = if (hide) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }
            
            requireContext().packageManager.setComponentEnabledSetting(
                componentName,
                newState,
                PackageManager.DONT_KILL_APP
            )
        }
        
        private fun showAboutDialog() {
            AlertDialog.Builder(requireContext())
                .setTitle("关于APK1安装器")
                .setMessage("""
                    版本：${getVersionName()}
                    
                    功能：
                    • 处理QQ、微信传输的.apk.1文件
                    • 自动重命名并安装APK应用
                    • 支持隐藏应用图标
                    • 简洁易用的界面设计
                    
                    使用方法：
                    1. 在QQ或微信中点击APK1文件
                    2. 选择"用其他应用打开"
                    3. 选择"APK1安装器"
                    4. 点击"处理并安装"
                    
                    开发者：Edan Liu
                """.trimIndent())
                .setPositiveButton("确定", null)
                .show()
        }
        
        private fun getVersionName(): String {
            return try {
                val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
                packageInfo.versionName ?: "1.0"
            } catch (e: Exception) {
                "1.0"
            }
        }
    }
}