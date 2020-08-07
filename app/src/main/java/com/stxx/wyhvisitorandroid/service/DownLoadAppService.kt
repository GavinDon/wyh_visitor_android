package com.stxx.wyhvisitorandroid.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.*
import android.os.Build
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.net.RxScheduler
import com.gavindon.mvvm_lib.widgets.showToast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.requests.DownloadRequest
import com.github.kittinunf.fuel.core.requests.tryCancel
import com.github.kittinunf.fuel.rx.rxResponse
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.NOTIFY_ID_DOWNLOAD
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.fileProviderAuth
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.runOnUiThread
import java.io.File
import kotlin.math.ceil

/**
 * description:下载ar科普App
 * Created by liNan on  2020/7/8 08:49
 */
class DownLoadAppService : Service() {


    companion object {
        const val REQUEST_CANCEL = "request_cancel"
    }

    private lateinit var downloadFile: File
    private lateinit var downloadRequest: DownloadRequest

    //创建notification和在8.0之上创建NotificationChannel使用
    private val channelId = "com.stxx.wyh.download"

    private var dialogMessage = "是否要下载AR科普apk?"

    //下载完成标志
    private var downloadFinishFlag: Boolean? = null
    private val compositeDisposable = CompositeDisposable()

    private val connectivityManager: ConnectivityManager by lazy {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    //创建通知栏下载进度样式(根布局只能使用frameLayout linearLayout relativeLayout)
    private val customRemoteViews: RemoteViews by lazy {
        RemoteViews(
            MVVMBaseApplication.appContext.packageName,
            R.layout.notify_download_app
        )
    }
    private val notification: Notification by lazy {
        NotificationCompat.Builder(this, channelId)
            .setTicker("正在下载AR科普apk..", customRemoteViews)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_icon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customRemoteViews)
            .setAutoCancel(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }
    private val notifyManager by lazy {
        NotificationManagerCompat.from(MVVMBaseApplication.appContext)
    }


    override fun onCreate() {
        super.onCreate()
        network()
        downloadFile = File(this.getExternalFilesDir(DIRECTORY_DOWNLOADS), "ar.apk")
    }


    private fun showDialog() {
        val builder =
            AlertDialog.Builder(this, R.style.downloadDialog)
                .setTitle("下载")
                .setMessage(dialogMessage)
                .setPositiveButton(R.string.confirm) { dialog, _ ->
                    downLoadApp()
                    showNotify()
                    dialog.dismiss()
                }.setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()

                }
        val dialog = builder.create()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }
        dialog.setOnShowListener { dialogs ->
            val negativeButton =
                (dialogs as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.light))
        }
        dialog.show()
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, tartId: Int): Int {

        /**
         * 1.判断是否已经有下载好的apk
         * 2.判断是否正在下载
         */
        if (downloadFile.exists() && downloadFile.length() >= 36000000) {
            Logger.i("${downloadFile.length()}")
            installApp()
        } else {
            if (::downloadRequest.isInitialized) {
                if (downloadFinishFlag == true) {
                    showDialog()
                } else {
                    MVVMBaseApplication.appContext.showToast("正在下载..")
                }
            } else {
                showDialog()
            }
        }
        return START_NOT_STICKY
    }

    private var lastUpdate = 0L
    private fun downLoadApp() {
        downloadRequest =
            Fuel.download("http://manage.wenyuriverpark.com:8082/apk/ar.apk")
        //  构建FileOutputStream对象,文件不存在会自动新建
        compositeDisposable.add(downloadRequest.fileDestination { _, _ ->
            downloadFile
        }.progress { readBytes, totalBytes ->
            if (totalBytes != -1L) {
                //  allow 2 updates/second max - more than 10/second will be blocked
                if (System.currentTimeMillis() - lastUpdate > 600) {
                    lastUpdate = System.currentTimeMillis()
                    Logger.i("$readBytes/$totalBytes")
                    val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
                    updateProgress(progress)
                    downloadFinishFlag = false
                }
            } else {
                //文件错误
                runOnUiThread {
                    MVVMBaseApplication.appContext.showToast("文件破损！！")
                }
                downloadFinishFlag = true
            }
        }.rxResponse()
            .compose(RxScheduler.applySingleScheduler())
            .subscribe({
                notifyManager.cancel(NOTIFY_ID_DOWNLOAD)
                installApp()
                downloadFinishFlag = true
            }, {
                Logger.i(it.message.toString())
                downloadFinishFlag = false
            })
        )
    }

    private fun updateProgress(progress: Float) {
        customRemoteViews.setProgressBar(
            R.id.notify_progress,
            100,
            ceil(progress).toInt(),
            false
        )
        customRemoteViews.setTextViewText(
            R.id.tvProgress,
            "${ceil(progress).toInt()}%"
        )
        notifyManager.notify(NOTIFY_ID_DOWNLOAD, notification)
    }

    private fun installApp() {
        if (downloadFile.isFile) {
            // 通过Intent安装APK文件
            val intents = Intent(Intent.ACTION_INSTALL_PACKAGE)
            val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intents.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                FileProvider.getUriForFile(this, fileProviderAuth, downloadFile)
            } else {
                Uri.fromFile(downloadFile)
            }
            grantUriPermission(packageName, uri, FLAG_GRANT_READ_URI_PERMISSION)
            intents.addFlags(FLAG_ACTIVITY_NEW_TASK)
            intents.setDataAndType(uri, "application/vnd.android.package-archive")
            startActivity(intents)
        }
    }

    private fun showNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    channelId,
                    "download",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notifyManager.createNotificationChannel(notificationChannel)
        }
        notifyManager.notify(NOTIFY_ID_DOWNLOAD, notification)
    }

    private fun network() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network?,
                    networkCapabilities: NetworkCapabilities
                ) {
                    //当网络改变时
                    notifyManager.cancel(NOTIFY_ID_DOWNLOAD)
                    if (::downloadRequest.isInitialized) {
                        downloadRequest.tryCancel()
                    }
                    downloadFinishFlag = true
                    network(networkCapabilities)
                }
            }
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                networkCallback
            )

            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            network(networkCapabilities)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun network(networkCapabilities: NetworkCapabilities) {
        val hasWifi =
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val hasCellular =
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        //查看是否有网(必须有wifi连接或者移动网络否则会抛出异常)
        val isValidated =
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        dialogMessage = if (hasWifi) {
            if (isValidated) {
                "您当前处于WIFI状态下\n是否要下载AR科普apk?"
            } else {
                "您当前网络不可用!\n请连接网络之后可继续下载AR科普apk"
            }
        } else if (hasCellular) {
            if (isValidated) {
                "您当前未连接WIFI\n是否要下载AR科普apk?"
            } else {
                "您当前网络不可用!\n请连接网络之后可继续下载AR科普apk"
            }
        } else {
            "您当前网络不可用!\n请连接网络之后可继续下载AR科普apk"
        }
    }


    override fun onDestroy() {
        notifyManager.cancel(NOTIFY_ID_DOWNLOAD)
        compositeDisposable.clear()
        if (networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
        stopSelf()
    }


}