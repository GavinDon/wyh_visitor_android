package com.stxx.wyhvisitorandroid.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import cn.jpush.android.api.*
import cn.jpush.android.service.JPushMessageReceiver
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.WEB_VIEW_URL
import com.stxx.wyhvisitorandroid.WebViewUrl
import com.stxx.wyhvisitorandroid.bean.PushExtraData
import com.stxx.wyhvisitorandroid.view.PushReceiveActivity
import com.stxx.wyhvisitorandroid.view.splash.MultiFragments
import org.json.JSONObject


/**
 * description:
 * Created by liNan on  2020/5/25 10:33
 */
class PushMessageReceive : JPushMessageReceiver() {

    private val TAG = "PushMessageReceiver"
    override fun onMessage(
        context: Context,
        customMessage: CustomMessage
    ) {
        Log.e(TAG, "[onMessage] $customMessage")
//        if (customMessage.extra.isNotEmpty()) {
//            initChannel(context)
//        }
        processCustomMessage(context, customMessage)
    }

    override fun onNotifyMessageOpened(
        context: Context,
        message: NotificationMessage
    ) {
        Log.e(TAG, "[onNotifyMessageOpened] $message")
        val extral = message.notificationExtras
        if (!extral.isNullOrEmpty()) {
            val type = object : TypeToken<PushExtraData>() {}.type
            val pushData = GsonUtil.str2Obj<PushExtraData>(extral, type)
            val pushId = pushData?.data?.id
            val linkUrl = "${WebViewUrl.NEWS_DETAIL}$pushId&title=${message.notificationTitle}"
            try { //打开自定义的Activity
                val i = Intent(context, PushReceiveActivity::class.java)
                i.putExtra(WEB_VIEW_URL, linkUrl)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(i)
            } catch (throwable: Throwable) {
                Logger.i(throwable.localizedMessage)
            }
        }


    }

    override fun onMultiActionClicked(
        context: Context?,
        intent: Intent
    ) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮")
        val nActionExtra =
            intent.extras.getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA)
        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            Log.d(TAG, "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null")
            return
        }
        if (nActionExtra == "my_extra1") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一")
        } else if (nActionExtra == "my_extra2") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二")
        } else if (nActionExtra == "my_extra3") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三")
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义")
        }
    }

    override fun onNotifyMessageArrived(
        context: Context?,
        message: NotificationMessage
    ) {
        val extras = message.notificationExtras

        Log.e(TAG, "[onNotifyMessageArrived] $message")
    }

    override fun onNotifyMessageDismiss(
        context: Context?,
        message: NotificationMessage
    ) {
        Log.e(TAG, "[onNotifyMessageDismiss] $message")
    }


    override fun onRegister(
        context: Context?,
        registrationId: String
    ) {
        Log.e(TAG, "[onRegister] $registrationId")
    }

    override fun onConnected(
        context: Context?,
        isConnected: Boolean
    ) {
        Log.e(TAG, "[onConnected] $isConnected")
    }

    override fun onCommandResult(
        context: Context?,
        cmdMessage: CmdMessage
    ) {
        Log.e(TAG, "[onCommandResult] $cmdMessage")
    }

    override fun onTagOperatorResult(
        context: Context?,
        jPushMessage: JPushMessage?
    ) {
        super.onTagOperatorResult(context, jPushMessage)
    }

    override fun onCheckTagOperatorResult(
        context: Context?,
        jPushMessage: JPushMessage?
    ) {
        super.onCheckTagOperatorResult(context, jPushMessage)
    }

    override fun onAliasOperatorResult(
        context: Context?,
        jPushMessage: JPushMessage?
    ) {
        super.onAliasOperatorResult(context, jPushMessage)
    }

    override fun onMobileNumberOperatorResult(
        context: Context?,
        jPushMessage: JPushMessage?
    ) {
        super.onMobileNumberOperatorResult(context, jPushMessage)
    }

    //send msg to MainActivity
    private fun processCustomMessage(
        context: Context,
        customMessage: CustomMessage
    ) {


        /*       if (MainActivity.isForeground) {
                   val message = customMessage.message
                   val extras = customMessage.extra
                   val msgIntent =
                       Intent(MainActivity.MESSAGE_RECEIVED_ACTION)
                   msgIntent.putExtra(MainActivity.KEY_MESSAGE, message)
                   if (!ExampleUtil.isEmpty(extras)) {
                       try {
                           val extraJson = JSONObject(extras)
                           if (extraJson.length() > 0) {
                               msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras)
                           }
                       } catch (e: JSONException) {
                       }
                   }
                   LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent)
               }*/
    }

    override fun onNotificationSettingsCheck(
        context: Context?,
        isOn: Boolean,
        source: Int
    ) {
        super.onNotificationSettingsCheck(context, isOn, source)
        Log.e(TAG, "[onNotificationSettingsCheck] isOn:$isOn,source:$source")
    }

    override fun isNeedShowNotification(
        p0: Context?,
        p1: NotificationMessage?,
        p2: String?
    ): Boolean {
        return true
    }


}