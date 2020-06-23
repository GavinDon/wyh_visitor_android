package com.stxx.wyhvisitorandroid.widgets

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.getResourceIdOrThrow
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.gavindon.mvvm_lib.net.RxScheduler
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.stxx.wyhvisitorandroid.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import java.util.regex.Pattern

/**
 * description:输入框和‘发送验证码’按钮
 * <features>倒计时60s
 * Created by liNan on 2018/10/25 9:16
 */
class SendSmsView : LinearLayout {
    // 发送短信时间最短间隔
    private val minTime = 40L
    //发送短信button
    private lateinit var btnSms: TextView
    private lateinit var smsEdit: EditText
    private lateinit var compositeDisposable: CompositeDisposable

    private var viewBackGround: Drawable? = null

    private var leftDrawable: Int = 0

    private var editPadding = 0f

    //输入手机号码的editText
    private var etPhone: EditText? = null

    //输入手机号码的editText id
    private var etPhoneId = -1

    companion object {
        const val SP_SMS_SAVE = "sms_save"
        const val SP_IS_CLICK = "is_click"
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        val td = context.obtainStyledAttributes(attrs, R.styleable.SendSmsView)

        //整个view背景（可以是下划线或者其他的）
        viewBackGround = td.getDrawable(R.styleable.SendSmsView_background)
        //editText左边的图片
        leftDrawable = td.getResourceId(R.styleable.SendSmsView_leftDrawable, 0)
        editPadding = td.getDimension(R.styleable.SendSmsView_editPadding, 0f)
        etPhoneId = td.getResourceId(R.styleable.SendSmsView_etPhoneId, 0)
        td.recycle()

        removeAllViews()
        val view = context.frameLayout {
            layoutParams = LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER_VERTICAL
            background = viewBackGround
            //输入验证码
            smsEdit = editText {
                hint = resources.getString(R.string.hint_fill_sms_code)
                background = null
                inputType = InputType.TYPE_CLASS_PHONE
                maxLines = 1
                textSize = 16f

                //如果没有设置左边的图片则不设置padding等
                if (leftDrawable != 0) {
                    val leftDrawable = ContextCompat.getDrawable(context, leftDrawable)
                    compoundDrawablePadding = dip(10)
                    //否则会往右偏移
                    horizontalPadding = 0
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null)
                }
                textColor = ContextCompat.getColor(context, R.color.black)
            }.lparams(matchParent, matchParent) {
                leftPadding = editPadding.toInt()
            }
            //发送验证码按钮
            btnSms = textView {
                textColor = ContextCompat.getColor(context, R.color.colorSendSms)
//                background = ContextCompat.getDrawable(context, R.drawable.sel_theme_btn)
                horizontalPadding = dip(12)
                verticalPadding = dip(8)
                gravity = Gravity.CENTER_VERTICAL
                setOnClickListener {
                    if (Pattern.matches(phoneRegex, etPhone?.text?.toString() ?: "")) {
                        //防抖 enable设为false
                        isEnabled = false
                        SpUtils.put(SP_SMS_SAVE, System.currentTimeMillis())
                        downTime(minTime)
                        mListener?.invoke(etPhone?.text?.toString() ?: "")
                    } else {
                        context.showToast(resources.getString(R.string.input_right_phone))
                    }
                }
            }.lparams(wrapContent, matchParent) {
                gravity = Gravity.END
            }
            initBtnText()
        }
        this.addView(view)
    }

    /**
     * 点击发送按钮回调
     */
    private var mListener: ((telPhone: String) -> Unit)? = null

    fun setOnSmsClickListener(listener: (telPhone: String) -> Unit) {
        this.mListener = listener
    }

    fun getEditText() = smsEdit

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val c = this.context
        etPhone = if (c is AppCompatActivity) {
            c.delegate.findViewById(etPhoneId)
        } else {
            this.rootView.findViewById(etPhoneId)
        }
    }

    /**
     * 判断是否可以发送短信
     * 发送上一条短信的间隔 是否小于60
     */
    private fun initBtnText() {
        val saveTime = SpUtils.get(SP_SMS_SAVE, -1L)
        val isClick = SpUtils.get(SP_IS_CLICK, false)
        if (saveTime == -1L) {
            btnSms.text = resources.getString(R.string.str_send_sms_code)
        } else {
            if (!isClick) {
                btnSms.text = resources.getString(R.string.str_send_sms_code)
            } else {
                val timeDiff = (System.currentTimeMillis() - saveTime) / 1000
                if (timeDiff in 1 until minTime) {
                    downTime(minTime - timeDiff)
                } else {
                    btnSms.text = resources.getString(R.string.str_send_sms_code)
                    SpUtils.clearName(SP_SMS_SAVE)
                }
            }
        }
    }

    /**
     * 倒计时60s
     */
    private fun downTime(cTime: Long) {
        compositeDisposable = CompositeDisposable()
        //period 周期
        val interval = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .compose(RxScheduler.applyScheduler())
            .map {
                cTime - it
            }
            .take(cTime + 1)
            .subscribe({
                if (it != 0L) {
                    SpUtils.put(SP_IS_CLICK, true)
                    btnSms.apply {
                        isEnabled = false
//                        background =
//                            ContextCompat.getDrawable(context, R.drawable.sel_theme_btn_unenable)
                        text = "${it}s${resources.getString(R.string.str_go_send)}"
                    }
                } else {
                    btnSms.apply {
                        isEnabled = true
//                        background = ContextCompat.getDrawable(context, R.drawable.sel_theme_btn)
                        text = resources.getString(R.string.str_send_sms_code)
                    }
                    SpUtils.clearName(SP_SMS_SAVE)
                }
            }, {})
        //在退出时取消订阅
        compositeDisposable.add(interval)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (this::compositeDisposable.isInitialized) {
            compositeDisposable.clear()
        }
    }

}