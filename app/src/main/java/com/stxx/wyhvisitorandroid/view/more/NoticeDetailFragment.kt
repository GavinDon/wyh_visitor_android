package com.stxx.wyhvisitorandroid.view.more

import android.os.Bundle
import android.os.Handler
import android.text.Spanned
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.widgets.HtmlUtil
import kotlinx.android.synthetic.main.fragment_notice.*

/**
 * description:通知公告详情
 * Created by liNan on  2020/5/18 17:22
 */
class NoticeDetailFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.notice
    override val layoutId: Int = R.layout.fragment_notice

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        val detail = arguments?.getString("detail").toString()
        HtmlUtil().show(this.requireContext(), detail, Handler {
            tvNoticeDetail.text = it.obj as Spanned
            return@Handler false
        })
    }
}