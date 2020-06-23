package com.stxx.wyhvisitorandroid.view.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.stxx.wyhvisitorandroid.R

/**
 * description:
 * Created by liNan on 2020/3/4 16:51

 */
open class BaseDialogFragment : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.fullDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            setCanceledOnTouchOutside(true)
        }
        dialog?.window?.setWindowAnimations(R.style.RightAnimation)
    }

}