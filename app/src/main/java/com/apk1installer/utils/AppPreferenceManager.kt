package com.apk1installer.utils

import android.content.Context
import android.content.SharedPreferences

class AppPreferenceManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "apk1_installer_prefs"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_HIDE_ICON = "hide_icon"
        private const val KEY_AUTO_INSTALL = "auto_install"
        private const val KEY_PRIVACY_ACCEPTED = "privacy_accepted"
        private const val KEY_LAST_USED_PATH = "last_used_path"
    }
    
    /**
     * 检查是否是首次启动
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    /**
     * 设置首次启动完成
     */
    fun setFirstLaunchCompleted() {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, false)
            .apply()
    }
    
    /**
     * 检查是否应该隐藏图标
     */
    fun shouldHideIcon(): Boolean {
        return sharedPreferences.getBoolean(KEY_HIDE_ICON, false)
    }
    
    /**
     * 设置隐藏图标
     */
    fun setHideIcon(hide: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_HIDE_ICON, hide)
            .apply()
    }
    
    /**
     * 检查是否启用自动安装
     */
    fun isAutoInstallEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTO_INSTALL, false)
    }
    
    /**
     * 设置自动安装
     */
    fun setAutoInstall(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_AUTO_INSTALL, enabled)
            .apply()
    }
    
    /**
     * 设置自动安装（别名方法）
     */
    fun setAutoInstallEnabled(enabled: Boolean) {
        setAutoInstall(enabled)
    }
    
    /**
     * 检查图标是否被隐藏（别名方法）
     */
    fun isIconHidden(): Boolean {
        return shouldHideIcon()
    }
    
    /**
     * 设置图标隐藏状态（别名方法）
     */
    fun setIconHidden(hidden: Boolean) {
        setHideIcon(hidden)
    }
    
    /**
     * 检查是否已接受隐私协议
     */
    fun isPrivacyAccepted(): Boolean {
        return sharedPreferences.getBoolean(KEY_PRIVACY_ACCEPTED, false)
    }
    
    /**
     * 设置隐私协议已接受
     */
    fun setPrivacyAccepted(accepted: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_PRIVACY_ACCEPTED, accepted)
            .apply()
    }
    
    /**
     * 获取上次使用的路径
     */
    fun getLastUsedPath(): String? {
        return sharedPreferences.getString(KEY_LAST_USED_PATH, null)
    }
    
    /**
     * 设置上次使用的路径
     */
    fun setLastUsedPath(path: String) {
        sharedPreferences.edit()
            .putString(KEY_LAST_USED_PATH, path)
            .apply()
    }
    
    /**
     * 清除所有设置
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * 获取所有设置的摘要
     */
    fun getSettingsSummary(): Map<String, Any> {
        return mapOf(
            "首次启动" to isFirstLaunch(),
            "隐藏图标" to shouldHideIcon(),
            "自动安装" to isAutoInstallEnabled(),
            "隐私协议已接受" to isPrivacyAccepted(),
            "上次使用路径" to (getLastUsedPath() ?: "无")
        )
    }
}