package com.stxx.wyhvisitorandroid.view.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Parcelable
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.net.parse2
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.gson.reflect.TypeToken
import com.stxx.wyhvisitorandroid.ApiService.QUERY_WIFI
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.OneKeyWifiResp
import com.stxx.wyhvisitorandroid.view.helpers.WifiUtil
import kotlinx.android.synthetic.main.fragment_one_key_wifi.*
import org.jetbrains.anko.support.v4.toast

/**
 * description: 一键wifi
 * Created by liNan on  2020/9/15 08:56
 */
class OneKeyWifiFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.visitor_wifi_conn
    override val layoutId: Int = R.layout.fragment_one_key_wifi
    private val intentFilter: IntentFilter = IntentFilter()

    private var wifiResp: OneKeyWifiResp? = null

    private lateinit var wifiBroadcastReceiver: BroadcastReceiver
    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        getWifiInfo()
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED")
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        intentFilter.addAction("android.net.wifi.STATE_CHANGE")

        wifiBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                if (WifiManager.NETWORK_STATE_CHANGED_ACTION == action) {
                    val parcelable: Parcelable =
                        intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
                    val netWorkInfo = parcelable as NetworkInfo
                    //如果禁止打开wifi 取消并提示
                    val state = netWorkInfo.isConnected
                    if (state) {
                        context?.showToast("已成功连接")
                        wifiLottie?.frame = 100
                        wifiLottie?.cancelAnimation()
                    }

                }
            }
        }
        btnWifiConnect.setOnClickListener {
            connectWifi()
        }
    }

    private fun connectWifi() {
        wifiLottie.playAnimation()
        val hasWifi = WifiUtil.scanResults(wifiResp)
        if (hasWifi != null) {
            this.context?.registerReceiver(wifiBroadcastReceiver, intentFilter)
            WifiUtil.connectWifi(wifiResp)
        } else {
            context?.showToast("未找到可连接wifi")
            wifiLottie?.frame = 100
            wifiLottie?.cancelAnimation()
        }

    }

    private fun getWifiInfo() {
        val type = object : TypeToken<BR<List<OneKeyWifiResp>>>() {}.type
        http?.get(QUERY_WIFI)?.parse2<BR<List<OneKeyWifiResp>>>(type, {
            if (it.code == 0) {
                if (it.data.isNullOrEmpty()) {
                    wifiResp = OneKeyWifiResp("WYHGY-WX", "WX123456")
                } else {
                    wifiResp = it.data[0]
                }
            } else {
                wifiResp = OneKeyWifiResp("WYHGY-WX", "WX123456")
                toast(it.msg)
            }
        }, {
            wifiResp = OneKeyWifiResp("WYHGY-WX", "WX123456")
            toast(it.message.toString())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::wifiBroadcastReceiver.isInitialized)
            try {
                this.context?.unregisterReceiver(wifiBroadcastReceiver)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
    }

}