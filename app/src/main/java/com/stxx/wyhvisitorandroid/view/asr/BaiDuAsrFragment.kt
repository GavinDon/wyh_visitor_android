package com.stxx.wyhvisitorandroid.view.asr

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.utils.GsonUtil
import com.gavindon.mvvm_lib.utils.genericType
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.adapter.BDBotChatAdapter
import com.stxx.wyhvisitorandroid.adapter.DelegateMultiEntity
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.bean.BdBotResult
import com.stxx.wyhvisitorandroid.mplusvm.BaiDuAsr
import com.stxx.wyhvisitorandroid.view.asr.recog.MyRecognizer
import com.stxx.wyhvisitorandroid.view.asr.recog.RecogResult
import com.stxx.wyhvisitorandroid.view.asr.recog.listener.ChainRecogListener
import com.stxx.wyhvisitorandroid.view.asr.recog.listener.IRecogListener
import kotlinx.android.synthetic.main.fragment_asr.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_title.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject
import kotlin.concurrent.thread

/**
 * description:
 * Created by liNan on 2020/4/10 11:30

 */
class BaiDuAsrFragment : BaseFragment() {

    //    private lateinit var input: DigitalDialogInput
    private lateinit var chainRecogListener: ChainRecogListener
    private lateinit var myRecognizer: MyRecognizer
    private var running: Boolean = false
    private lateinit var mSpeechSynthesizer: SpeechSynthesizer

    private var token = ""

    private lateinit var vm: BaiDuAsr
    private lateinit var mBotChatAdapter: BDBotChatAdapter

    //对话记录
    private val chatList = mutableListOf<DelegateMultiEntity>()


    override val layoutId: Int = R.layout.fragment_asr


    private fun initAsr() {
        vm = getViewModel()
        chainRecogListener = ChainRecogListener()
        myRecognizer = MyRecognizer(this.context, chainRecogListener)
        myRecognizer.setEventListener(MessageListener())
        initSpeech()
    }

    private fun initSpeech() {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance()
        val auth = Auth.getInstance(MVVMBaseApplication.appContext)
        mSpeechSynthesizer.apply {
            setApiKey(auth.appKey, auth.secretKey)
            setAppId(auth.appId)
            setContext(this@BaiDuAsrFragment.context)
            //设置声音
            setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
            //初始化合成引擎
            initTts(TtsMode.ONLINE)
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        initAsr()
        speakView.setOnClickListener {
            mSpeechSynthesizer.stop()
            myRecognizer.cancel()
            startRecognition()
        }
        if (vm.sLiveData.value != null) {
            token = vm.sLiveData.value!!.access_token
        } else {
            vm.getAuth().observe(this, Observer {
                token = it.access_token
            })
        }
//        if (!chatList.isNullOrEmpty()) {
//            cslTip?.visibility = View.VISIBLE
//            tvAsrResult?.visibility = View.VISIBLE
//        }
        mBotChatAdapter = BDBotChatAdapter(chatList)
        rvBot.adapter = mBotChatAdapter

    }

    private fun showResult(result: String) {
        cslTip?.visibility = View.GONE
        tvAsrResult?.visibility = View.VISIBLE
        tvAsrResult?.text = result
    }


    /**
     * 启动识别
     */
    private fun startRecognition() {
        running = true
        speakView.setAnimation(R.raw.voice)
        speakView.playAnimation()

//        input = DigitalDialogInput(
//            myRecognizer,
//            chainRecogListener,
//            mapOf(
//                "accept-audio-volume" to true,
//                "accept-audio-data" to false,
//                "disable-punctuation" to false,
//                "pid" to "15373"
//            )
//        )
        myRecognizer.start(
            mapOf(
                "accept-audio-volume" to true,
                "accept-audio-data" to false,
                "disable-punctuation" to false,
                "pid" to "15373"
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        myRecognizer.setEventListener(null)
        myRecognizer.release()
    }

    override fun setStatusBar() {
        frame_layout_title.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        toolbar_back.setOnClickListener { this.findNavController().navigateUp() }
        titleBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        titleBar.layoutParams.height = getStatusBarHeight(this.requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .transparentStatusBar()
            .init()
    }

    fun rejectFun(funName: String, keyWord: String) {
        try {
            val clazz = ReflectUtil::class.java
            val method =
                clazz.getDeclaredMethod(funName, NavController::class.java, String::class.java)
            method.invoke(clazz.newInstance(), findNavController(), keyWord)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 语音识别结果回调
     */
    inner class MessageListener : IRecogListener {

        //机器会话id可进行流畅对话
        private var sessionId: String? = null
        private var logId: String? = null

        override fun onAsrVolume(volumePercent: Int, volume: Int) {
        }

        override fun onAsrEnd() {
            speakView?.frame = 0
            speakView?.cancelAnimation()
        }

        override fun onAsrFinishError(
            errorCode: Int,
            subErrorCode: Int,
            descMessage: String?,
            recogResult: RecogResult?
        ) {
        }

        override fun onAsrExit() {
        }

        //onAsrFinalResult 会回调两次
        private var hasBack = true

        override fun onAsrFinalResult(results: Array<out String>?, recogResult: RecogResult?) {
            try {
                running = false
                if (results != null && hasBack) {
                    val result = results.asList()[0]
                    showResult(result)
                    val delegateMultiEntity = DelegateMultiEntity()
                    delegateMultiEntity.chat = result
                    delegateMultiEntity.itemType = DelegateMultiEntity.BOT_RIGHT
                    mBotChatAdapter.addData(delegateMultiEntity)
                    rvBot.smoothScrollToPosition(mBotChatAdapter.data.size - 1)
                    // 请求参数
                    thread {
                        val accessToken = token
                        val httpResult =
                            HttpUtil.post(
                                ApiService.BAIDU_BOT,
                                accessToken,
                                "application/json",
                                AsrUtil.generateJson(result, sessionId, logId)
                            )
                        val botResult =
                            GsonUtil.str2Obj<BdBotResult>(httpResult, genericType<BdBotResult>())
                        logId = botResult?.result?.log_id
                        sessionId = botResult?.result?.session_id
                        Logger.i(httpResult)
                        //防止已经关闭页面时更新ui
                        if (!this@BaiDuAsrFragment.isDetached)
                            runOnUiThread {
                                if (botResult?.error_code == 0) {
                                    if (!botResult.result.response_list.isNullOrEmpty()) {

                                        val actionResp =
                                            botResult.result.response_list[0].action_list[0]
                                        //如果unit设置的是函数名 解析函数名并进行执行函数
                                        val action = actionResp.type.toUpperCase()
                                        //需要对话
                                        if (action == ActionTypeEnum.EVENT.name) {
                                            //执行动作
                                            val eventFun = actionResp.custom_reply
                                            if (eventFun.isNotEmpty()) {
                                                val funName = JSONObject(eventFun).getString("func")
                                                mSpeechSynthesizer.speak("好的")
                                                val keyWord =
                                                    botResult.result.response_list[0].schema.slots[0].original_word
                                                if (funName.isNotEmpty()) {
                                                    rejectFun(funName, keyWord)
                                                }
                                            }
                                        } else {
                                            //回复的话语
                                            val say = actionResp.say
                                            mSpeechSynthesizer.speak(if (say.isNotEmpty()) say else "好的")
                                            val robotSayBean =
                                                DelegateMultiEntity().apply {
                                                    chat = say
                                                    itemType = DelegateMultiEntity.BOT_LEFT
                                                }
                                            mBotChatAdapter.addData(robotSayBean)
                                            rvBot.smoothScrollToPosition(mBotChatAdapter.data.size - 1)
                                        }
                                    }
                                }
                            }
                    }
                }
                hasBack = !hasBack
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        override fun onAsrPartialResult(results: Array<out String>?, recogResult: RecogResult) {
            if (results != null && results.isNotEmpty()) {
                showResult(results.asList()[0])
            }
        }

        override fun onAsrReady() {
            tvListenering?.visibility = View.VISIBLE
            tvAsrTipSpeak?.visibility = View.GONE
            tvAsrYouCan?.visibility = View.GONE
        }

        override fun onAsrFinish(recogResult: RecogResult?) {
            hasBack = true
            //停止动画
            speakView?.frame = 0
            speakView?.cancelAnimation()
        }

        override fun onAsrAudio(data: ByteArray?, offset: Int, length: Int) {
        }

        override fun onAsrBegin() {

        }

        override fun onAsrLongFinish() {
        }

        override fun onAsrOnlineNluResult(nluResult: String?) {
            Logger.i("${nluResult}===========================================")
        }

        override fun onOfflineUnLoaded() {
        }

        override fun onOfflineLoaded() {
        }

    }
}

