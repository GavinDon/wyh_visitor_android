package com.stxx.wyhvisitorandroid.view.helpers

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.stxx.wyhvisitorandroid.bean.OneKeyWifiResp
import org.jetbrains.anko.toast

/**
 * description:wifi管理类
 * Created by liNan on  2020/9/9 15:50
 */
object WifiUtil {

    private var oneKeyWifiResp: OneKeyWifiResp? = null
    private val wifiManager: WifiManager by lazy {
        MVVMBaseApplication.appContext.applicationContext.getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
    }


    /**
     * wifi是否打开状态
     */
    private val isWifiEnable: Boolean = wifiManager.isWifiEnabled

    /**
     * 打开wifi
     */
    private fun openWifi() {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }
    }


    fun scanResults(wifiResp: OneKeyWifiResp?): ScanResult? {
        val wifiList = wifiManager.scanResults
        for (i in wifiList) {
            if (i.SSID == wifiResp?.name) {
                return i
            }
        }
        return null

    }

    /**
     * 是否存在需要连接wifi的信息
     */
    private fun hasNeedWifiConfig(): WifiConfiguration? {
        val configs = wifiManager.configuredNetworks
        if (configs.isNullOrEmpty()) return null
        for (config in configs) {
            if (config.SSID == oneKeyWifiResp?.name) {
                return config
            }
        }
        return null
    }

    /**
     * 创建需要连接wifi的信息
     */
    private fun createWifiConfig(): WifiConfiguration {
        val config = WifiConfiguration()
        config.run {
            allowedAuthAlgorithms.clear()
            allowedGroupCiphers.clear()
            allowedKeyManagement.clear()
            allowedPairwiseCiphers.clear()
            allowedProtocols.clear()
            SSID = "\"${oneKeyWifiResp?.name}\""
        }
        val connWifiConfig = hasNeedWifiConfig()

        if (connWifiConfig != null) {
//            wifiManager.removeNetwork(connWifiConfig.networkId)
            return connWifiConfig
        } else {
            if (!oneKeyWifiResp?.password.isNullOrEmpty()) {
                config.hiddenSSID = true
                config.preSharedKey = "\"${oneKeyWifiResp?.password}\""
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                config.status = WifiConfiguration.Status.ENABLED
            } else {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
        }

        return config
    }

    /**
     * 连接wifi
     */
    fun connectWifi(wifiResp: OneKeyWifiResp?) {
        oneKeyWifiResp = wifiResp
        if (wifiResp == null) {
            MVVMBaseApplication.appContext.toast("暂无可用wifi")
        } else {
            openWifi()
            val hasConnectId = wifiManager.connectionInfo.networkId
            if (hasConnectId != -1) {
                wifiManager.disableNetwork(wifiManager.connectionInfo.networkId)
            }
            val netId = wifiManager.addNetwork(createWifiConfig())
            wifiManager.enableNetwork(netId, true)
        }

    }


}