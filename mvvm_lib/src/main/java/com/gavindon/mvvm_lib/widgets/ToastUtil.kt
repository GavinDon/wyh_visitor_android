package com.gavindon.mvvm_lib.widgets

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.gavindon.mvvm_lib.R


/**
 * description: toast类使用自定义提示样式
 * Created by liNan on 2019/12/30 15:08

 */
class ToastUtil private constructor(private val context: Context) {


    private val toastView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.toast, null, false)
    }
    private val tvText: TextView by lazy {
        toastView.findViewById<TextView>(R.id.tv_toast)
    }

    private var lastTxt = "@_@"

    private var toast: Toast? = null


    companion object {
        var instance: ToastUtil? = null
        fun getInstance(context: Context): ToastUtil? {
            if (instance == null) {
                instance = ToastUtil(context)
            }
            return instance
        }
    }

    fun show(txt: String, duration: Int) {

        if (toast == null) {
            toast = Toast(context)
        }
        tvText.text = txt

        if (txt == lastTxt) {
            toast?.cancel()
        }

        toast?.let {
            it.setGravity(Gravity.CENTER, 0, 0)
            it.duration = duration
            it.view = toastView
        }
        toast?.show()

    }
}

fun Context.showToast(txt: String, duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.getInstance(this)?.show(txt, duration)
}

fun Context.showToast(txt: Int, duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.getInstance(this)?.show(txt.toString(), duration)
}

