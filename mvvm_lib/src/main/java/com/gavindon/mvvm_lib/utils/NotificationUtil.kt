package com.gavindon.mvvm_lib.utils

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import java.lang.reflect.Field


/**
 * description:
 * Created by liNan on  2020/5/13 10:05
 */
class NotificationUtil {


    companion object {
        private const val CHECK_OP_NO_THROW = "checkOpNoThrow"
        private const val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"
        private val context = MVVMBaseApplication.appContext

        fun isNotifyEnable(): Boolean {
            val appInfo = context.applicationInfo
            val pkg = appInfo.packageName
            val uid = appInfo.uid
            //5.0以上8.0以下
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val mAppOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                var appOpsClass: Class<*>? = null
                try {
                    appOpsClass = Class.forName(AppOpsManager::class.java.name)
                    val checkOpNoThrowMethod =
                        appOpsClass.getMethod(
                            CHECK_OP_NO_THROW,
                            Integer.TYPE,
                            Integer.TYPE,
                            String::class.java
                        )
                    val opPostNotificationValue: Field =
                        appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)

                    val value = opPostNotificationValue[Int::class.java] as Int
                    return (checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) as Int ==
                            AppOpsManager.MODE_ALLOWED)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                return false

            } else {
                try {
                    return NotificationManagerCompat.from(context).areNotificationsEnabled()
                    /*
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                     val sServiceField = notificationManager::class.java.getDeclaredMethod("getService")
                      sServiceField.isAccessible = true
                      val sService = sServiceField.invoke(notificationManager)
                      val method = sService::class.java.getDeclaredMethod(
                          "areNotificationsEnabledForPackage"
                          , String::class.java, Integer.TYPE
                      )
                      method.isAccessible = true
                      return method.invoke(sService, pkg, uid) as Boolean*/
                } catch (ex: Exception) {
                    return true
                }
            }
        }

        fun hasNotifyEnable(): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        fun settingNotify() {
            val localIntent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                localIntent.data =
                    Uri.fromParts("package", context.applicationInfo.packageName, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
                localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                localIntent.putExtra("app_package", context.applicationInfo.packageName)
                localIntent.putExtra("app_uid", context.applicationInfo.uid)
            }
            context.startActivity(localIntent)
        }
    }
}