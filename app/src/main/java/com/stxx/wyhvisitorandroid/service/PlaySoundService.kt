package com.stxx.wyhvisitorandroid.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.MediaController
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.stxx.wyhvisitorandroid.NOTIFY_ID_SOUND
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.ServerPointResp
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.net.URL
import kotlin.concurrent.thread


/**
 * description:播放声音service
 * 可以考虑使用MediaBrowserService来实现
 * Created by liNan on  2020/5/12 18:57
 */
class PlaySoundService : Service() {

    private val binder = PlaySoundBinder()
    private val player by lazy { MediaPlayer() }
    private var currentLocation: Int = 0

    private val channelId = "com.stxx.wyh"

    //
    private val channelName = "语音讲解"
    private val broadCastRequestCode = 1

    //当前播放状态0正在播放,1暂停播放


    //广播接收的action
    private val playAction = "com.stxx.play"

    //关闭
    private val closeAction = "com.stxx.close"

    //浮动窗口
    private val floatAction = "com.stxx.float"

    //处理点击事件广播
    private val receiver by lazy { PendingIntentBroadCast() }

    //上一次点击语音的url
    private var lastVoiceUrl = ""


    //创建通知栏播放声音样式(根布局只能使用frameLayout linearLayout relativeLayout)
    private val customRemoteViews: RemoteViews by lazy {
        RemoteViews(
            MVVMBaseApplication.appContext.packageName,
            R.layout.notify_play_sound
        )
    }
    private val notification: Notification by lazy {
        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_icon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customRemoteViews)
            .setContentIntent(
                PendingIntent.getBroadcast(
                    this, broadCastRequestCode,
                    Intent(), PendingIntent.FLAG_NO_CREATE
                )
            )
            .build()
    }
    private val notifyManager by lazy {
        NotificationManagerCompat.from(MVVMBaseApplication.appContext)
    }

    companion object {
        //点击某条的数据
        const val SOUND_SOURCE = "sound"

        //所有可播放的数据
        const val ALL_SOUND = "all_sound"
    }

    override fun onCreate() {
        super.onCreate()
        player.let {
            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            it.setAudioAttributes(attributes)
        }
        //添加广播处理点击事件
        val intentFilter = IntentFilter()
        intentFilter.addAction(playAction)
        intentFilter.addAction(closeAction)
        intentFilter.addAction(floatAction)
        this.registerReceiver(receiver, intentFilter)
        val playPendingIntent = PendingIntent.getBroadcast(
            MVVMBaseApplication.appContext,
            broadCastRequestCode,
            Intent(playAction),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        customRemoteViews.setOnClickPendingIntent(R.id.ivPlayButton, playPendingIntent)

        val closePendingIntent = PendingIntent.getBroadcast(
            MVVMBaseApplication.appContext,
            broadCastRequestCode,
            Intent(closeAction),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        customRemoteViews.setOnClickPendingIntent(R.id.iv_close_notify, closePendingIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val sourceData = intent?.getSerializableExtra(SOUND_SOURCE) as ServerPointResp
            playSound(sourceData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_NOT_STICKY
    }

    fun getMediaPlay() = player

    private fun playSound(data: ServerPointResp) {
        if (!data.explain.isNullOrEmpty() && data.explain.startsWith("http")) {
            //当重复点击同一个语音时 判断是暂停播放还是开始播放
            if (data.explain == lastVoiceUrl) {
                if (player.isPlaying) {
                    pauseSound()
                } else {
                    //进度为0 说明从头播放
                    if (currentLocation != 0) {
                        resumeSound()
                    } else {
                        startPlay(data)
                    }
                }
            } else {
                startPlay(data)
            }
        }
    }

    private fun startPlay(data: ServerPointResp) {
        player.reset()
        currentLocation = 0
        player.setDataSource(data.explain)
        player.prepareAsync()
        updateNotification(data)
        player.setOnPreparedListener {
            player.start()
            updateNotification(data)
        }
        player.setOnCompletionListener {
            currentLocation = 0
            closeNotify()
        }
        player.setOnErrorListener { mp, what, extra ->
            currentLocation = 0
            closeNotify()
            return@setOnErrorListener false
        }
        lastVoiceUrl = data.explain.toString()
    }

    fun pauseSound() {
        if (player.isPlaying) {
            player.pause()
            currentLocation = player.currentPosition
            customRemoteViews.setImageViewResource(R.id.ivPlayButton, R.mipmap.ic_play)
            startForeground(NOTIFY_ID_SOUND, notification)

        }
    }

    fun resumeSound() {
        if (!player.isPlaying) {
            player.seekTo(currentLocation)
            player.setOnSeekCompleteListener {
                player.start()
            }
            customRemoteViews.setImageViewResource(R.id.ivPlayButton, R.mipmap.ic_pause)
            startForeground(NOTIFY_ID_SOUND, notification)

        }
    }

    //前台服务不能使用cancel取消通知栏
    private fun closeNotify() {
        /*    if (player.isPlaying) {
                player.stop()
            }
            player.release()
            notifyManager.cancelAll()*/
        stopForeground(true)
        onDestroy()
    }

    /**
     * 开始播放时更新notify
     */
    private fun updateNotification(data: ServerPointResp) {
        customRemoteViews.setTextViewText(R.id.tvNotifyName, data.name)
        customRemoteViews.setTextViewText(R.id.tvNofifyTip, data.position ?: "")
        customRemoteViews.setImageViewResource(R.id.ivPlayButton, R.mipmap.ic_pause)
        thread {
            if (data.imgurl.isNullOrEmpty()) {
                return@thread
            }
            val url = URL(data.imgurl)
            val bitmap = BitmapFactory.decodeStream(url.openStream())
            runOnUiThread {
                customRemoteViews.setImageViewBitmap(R.id.ivNotify, bitmap)
            }
        }
        notification.fullScreenIntent = PendingIntent.getBroadcast(
            this,
            broadCastRequestCode,
            Intent(floatAction),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            with(NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)) {
                notifyManager.createNotificationChannel(this)
                enableLights(true)
                enableVibration(true)
            }
        }
        startForeground(NOTIFY_ID_SOUND, notification)

    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    override fun onDestroy() {
        try {
            this.unregisterReceiver(this.receiver)
            player.release()
            stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    inner class PlaySoundBinder : Binder() {
        fun getService() = this@PlaySoundService
    }

    inner class PendingIntentBroadCast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == playAction) {
                //正在播放
                if (player.isPlaying) {
                    pauseSound()
                } else {
                    resumeSound()
                }
            } else if (action == closeAction) {
                closeNotify()
            }
        }
    }
}