package com.stxx.wyhvisitorandroid.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stxx.wyhvisitorandroid.R
import kotlinx.android.synthetic.main.dialog_loading.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.windowManager

/**
 * description:
 * Created by liNan on  2020/5/20 15:42
 */
class DialogSureCancel : BaseDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen
        setStyle(STYLE_NORMAL, R.style.halfDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(dip(300), dip(150))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_loading, null, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.context?.windowManager
        tvLoadText.text = "获取中..."
    }

}