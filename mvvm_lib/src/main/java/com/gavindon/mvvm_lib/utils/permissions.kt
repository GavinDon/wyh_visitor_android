package com.gavindon.mvvm_lib.utils

import android.content.Context
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.gavindon.mvvm_lib.R
import com.gavindon.mvvm_lib.base.PermissionCode
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission

/**
 * description:
 * Created by li Nan on 2020/1/6 10:31

 */

fun Context.requestPermission(
    vararg permission: String,
    onGrantedAction: () -> Unit
) {
    AndPermission.with(this)
        .runtime()
        .permission(permission)
        .onGranted {
            if (it.size == permission.size) {
                onGrantedAction()
            }
        }
        .onDenied {
            val permissionNames: List<String?> =
                Permission.transformText(this, it)
            val message = this.getString(
                R.string.message_permission_always_failed,
                TextUtils.join("\n", permissionNames)
            )
            //当点击禁止且勾选不再询问时
            if (AndPermission.hasAlwaysDeniedPermission(this, it)) {
                showDeniedPermission(this, message)
            } else {
                //当点击禁止时
                showDeniedPermission(this, message)
            }
        }.start()
}

/**
 * 提示缺少什么权限
 */
private fun showDeniedPermission(context: Context, message: String) {
    AlertDialog.Builder(context)
        .setMessage(message)
        .setPositiveButton("去设置") { _, _ ->
            AndPermission.with(context).runtime().setting()
                .start(PermissionCode.cameraCode)
        }.setNegativeButton("取消") { _, _ ->
        }.show()
}