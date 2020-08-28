package com.stxx.wyhvisitorandroid.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.stxx.wyhvisitorandroid.R
import kotlinx.android.synthetic.main.dialog_smart_tip.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.wrapContent

/**
 * description:AR智能提示dialog
 * Created by liNan on  2020/8/28 09:15
 */
class SmartTipDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.halfDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(phoneWidth - dip(80), wrapContent)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.dialog_smart_tip, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        smartTipTvTitle.text = "温馨提示"
    }
}