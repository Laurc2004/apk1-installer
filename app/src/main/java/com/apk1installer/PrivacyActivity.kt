package com.apk1installer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.apk1installer.databinding.ActivityPrivacyBinding

class PrivacyActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPrivacyBinding
    private lateinit var preferenceManager: com.apk1installer.utils.AppPreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferenceManager = com.apk1installer.utils.AppPreferenceManager(this)
        
        setupViews()
        loadPrivacyContent()
    }
    
    private fun setupViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "隐私协议"
        
        binding.privacyContentTextView.movementMethod = ScrollingMovementMethod()
        
        binding.agreeButton.setOnClickListener {
            preferenceManager.setPrivacyAccepted(true)
            finish()
        }
        
        binding.disagreeButton.setOnClickListener {
            finish()
        }
        
        // 如果已经同意过，隐藏按钮
        if (preferenceManager.isPrivacyAccepted()) {
            binding.buttonLayout.visibility = android.view.View.GONE
        }
    }
    
    private fun loadPrivacyContent() {
        val privacyText = """
            APK1安装器隐私协议
            
            生效日期：2024年1月1日
            
            感谢您使用APK1安装器！我们非常重视您的隐私保护。本隐私协议说明了我们如何收集、使用和保护您的信息。
            
            1. 信息收集
            • 我们不会收集您的个人身份信息
            • 我们不会上传您的文件到任何服务器
            • 我们不会访问您的通讯录、短信或通话记录
            • 我们只会访问您明确选择的APK文件
            
            2. 权限使用
            • 存储权限：用于读取您选择的APK文件
            • 安装权限：用于安装处理后的APK文件
            • 网络权限：仅用于检查更新（可选）
            
            3. 数据处理
            • 所有文件处理都在本地进行
            • 临时文件会在安装完成后自动删除
            • 我们不会保存或分析您的文件内容
            
            4. 第三方服务
            • 本应用不包含任何第三方广告SDK
            • 不会向第三方分享您的任何信息
            
            5. 数据安全
            • 我们采用适当的技术措施保护您的数据
            • 所有操作都在您的设备本地完成
            
            免责声明
            
            1. 使用风险
            • 请确保您安装的APK文件来源可靠
            • 本应用仅提供文件格式转换功能
            • 用户需自行承担安装第三方应用的风险
            
            2. 法律责任
            • 本应用不对用户安装的第三方应用承担任何责任
            • 用户应遵守当地法律法规
            • 禁止使用本应用进行任何违法活动
            
            3. 服务可用性
            • 我们不保证服务的持续可用性
            • 我们保留随时修改或终止服务的权利
            
            4. 知识产权
            • 用户应尊重第三方应用的知识产权
            • 不得使用本应用传播盗版软件
            
            联系我们
            
            如果您对本隐私协议有任何疑问，请通过以下方式联系我们：
            邮箱：liurc2004@outlook.com
            
            本协议的最终解释权归Edan Liu所有。
        """.trimIndent()
        
        binding.privacyContentTextView.text = privacyText
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}