package com.stxx.wyhvisitorandroid.view.asr

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.utils.SpUtils
import org.json.JSONObject
import java.util.*
import kotlin.random.Random


/**
 * description:
 * Created by liNan on  2020/4/24 10:20
 */
class AsrUtil {

    companion object {
        /**
         * 对话时上传的json
         * @param query 用户说的话
         * @param sessionId session保存机器人的历史会话信息
         * @param logId 客户端生成的唯一id，用来定位请求
         */
        fun generateJson(
            query: String,
            sessionId: String? = null,
            logId: String? = null
        ): String {
            val param =
                mapOf(
                    "version" to "2.0",
                    "service_id" to "S29196",
                    "session_id" to (sessionId ?: generateLogId()),
                    "log_id" to (logId ?: generateLogId()),
                    "request" to mapOf(
                        "user_id" to generateLogId(),
                        "query" to query,
                        "queryInfo" to mapOf("type" to "TEXT", "source" to "ASR")
                    ),
                    "dialog_state" to mapOf(
                        "contexts" to mapOf(
                            "SYS_REMEMBERED_SKILLS" to listOf(1025492, 1030933)
                        )
                    )
                )
            return JSONObject(param).toString()
        }

        /**
         * 客户端生成的唯一id
         */
        private fun generateLogId(): String {
            var uuid = loadDeviceId()
            if (uuid.isEmpty()) {
                uuid = buildDeviceUuid()
                saveDeviceId(uuid)
            }
            com.orhanobut.logger.Logger.i(uuid)
            return uuid
        }

        @SuppressLint("HardwareIds")
        private fun getAndroidId(): String? {
            return Settings.Secure.getString(
                MVVMBaseApplication.appContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )

        }

        private fun buildDeviceUuid(): String {
            var androidId =
                getAndroidId()
            if ("9774d56d682e549c" != androidId) {
                val random = Random(10)
                androidId =
                    Integer.toHexString(random.nextInt()) + Integer.toHexString(random.nextInt()) + Integer.toHexString(
                        random.nextInt()
                    )
            }
            return UUID(
                androidId.hashCode().toLong(),
                getBuildInfo().hashCode().toString().toLong()
            ).toString()
        }

        private fun getBuildInfo(): String {
            val sb = StringBuilder()
            sb.append(Build.BRAND).append("/")
                .append(Build.PRODUCT).append("/")
                .append(Build.DEVICE).append("/")
                .append(Build.ID).append("/")
                .append(Build.VERSION.INCREMENTAL)
            return sb.toString()
        }

        private fun saveDeviceId(uuid: String) {
            SpUtils.put("uuid", uuid)
        }

        private fun loadDeviceId(): String {
            return SpUtils.get("uuid", "")
        }

    }
}