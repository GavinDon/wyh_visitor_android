package com.stxx.wyhvisitorandroid.view.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * description: 注册微信
 * Created by liNan on  2020/9/11 14:11
 */
object WeChatRegister {

    var broadcastReceiver: BroadcastReceiver? = null
    var wxApi: IWXAPI? = null

    fun register(context: Context) {
        val appid = "wx697de48974c13c39"
        wxApi = WXAPIFactory.createWXAPI(context, appid, true)
        //建议动态监听微信启动广播进行注册到微信
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // 将该app注册到微信
                wxApi?.registerApp(appid)
            }
        }
        context.registerReceiver(broadcastReceiver, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))

    }

}