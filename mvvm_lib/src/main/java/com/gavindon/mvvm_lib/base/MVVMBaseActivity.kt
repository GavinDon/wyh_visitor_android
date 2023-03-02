package com.gavindon.mvvm_lib.base

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gavindon.mvvm_lib.R
import com.gavindon.mvvm_lib.base.PermissionCode.cameraCode
import com.gavindon.mvvm_lib.base.my_interface.IView
import com.gavindon.mvvm_lib.status.StatusView
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission


/**
 * description:
 * Created by liNan on 2019/12/27 11:22

 */
abstract class MVVMBaseActivity : AppCompatActivity(), IView {
    //提示缺少什么权限对话框
    private var permissionDialog: AlertDialog? = null
    val mStatusView: StatusView? by lazy {
        if (mStatusViewId != null) {
            findViewById<StatusView>(mStatusViewId!!)
        } else {
            null
        }
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        onInit(savedInstanceState)
    }

    @get:LayoutRes
    abstract val layoutId: Int

    @get:IdRes
    abstract val mStatusViewId: Int?

    protected abstract fun onInit(savedInstanceState: Bundle?)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionForResult()
    }

    fun requestPermission(
        context: Activity,
        vararg permission: String,
        onGrantedAction: () -> Unit
    ) {
        AndPermission.with(context)
            .runtime()
            .permission(permission)
            .onGranted {
                if (it.size == permission.size) {
                    onGrantedAction()
                }
            }
            .onDenied {
                val permissionNames: List<String?> =
                    Permission.transformText(context, it)
                val message = context.getString(
                    R.string.message_permission_always_failed,
                    TextUtils.join("\n", permissionNames)
                )
                //当点击禁止且勾选不再询问时
                if (AndPermission.hasAlwaysDeniedPermission(context, it)) {
                    showDeniedPermission(message)
                } else {
                    //当点击禁止时
                    showDeniedPermission(message)

                }
            }.start()
    }

    /**
     * 提示缺少什么权限
     */
    private fun showDeniedPermission(message: String) {
        permissionDialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("去设置") { _, _ ->
                AndPermission.with(this).runtime().setting()
                    .start(cameraCode)
            }.setNegativeButton("取消") { _, _ ->
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionDialog?.dismiss()
    }

/*    override fun onPause() {
        super.onPause()
        var topActivityPackage = ""
        val activityManager: ActivityManager =
            (getSystemService(android.content.Context.ACTIVITY_SERVICE)) as ActivityManager
        val runningTaskInfos: List<ActivityManager.RunningTaskInfo> = activityManager
            .getRunningTasks(1)
        if (runningTaskInfos != null) {
            val f = runningTaskInfos[0].topActivity
            topActivityPackage = f.packageName
            if (topActivityPackage == packageName) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("警告")
                builder.setPositiveButton("检测到窗口被劫持", null)
                builder.show()
            }
        }

    }*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

}

object PermissionCode {
    const val cameraCode = 0x1001
}