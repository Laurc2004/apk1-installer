package com.apk1installer.receivers

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log

class PhoneStateReceiver : BroadcastReceiver() {
    
    companion object {
        private const val RECOVERY_CODE = "*#*#1234#*#*"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            
            // 检查是否是拨号状态且号码匹配恢复代码
            if (state == TelephonyManager.EXTRA_STATE_OFFHOOK && 
                incomingNumber != null && 
                incomingNumber.contains("1234")) {
                
                restoreAppIcon(context)
            }
        } else if (intent.action == "android.provider.Telephony.SECRET_CODE") {
            val host = intent.data?.host
            if (host == "1234") {
                restoreAppIcon(context)
            }
        }
    }
    
    private fun restoreAppIcon(context: Context) {
        val preferenceManager = com.apk1installer.utils.AppPreferenceManager(context)
        
        // 只有在图标被隐藏时才恢复
        if (preferenceManager.isIconHidden()) {
            val packageManager = context.packageManager
            val componentName = ComponentName(
                context,
                "com.apk1installer.MainActivityAlias"
            )
            
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            
            // 更新偏好设置
            preferenceManager.setIconHidden(false)
        }
    }
}