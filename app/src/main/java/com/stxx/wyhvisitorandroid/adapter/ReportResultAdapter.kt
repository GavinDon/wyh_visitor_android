package com.stxx.wyhvisitorandroid.adapter

import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.ReportResultResp
import com.stxx.wyhvisitorandroid.enums.ComplaintEnum

/**
 * description:
 * Created by liNan on  2020/4/29 12:32e
 */
class ReportResultAdapter(layoutResId: Int, data: MutableList<ReportResultResp>?) :
    BaseQuickAdapter<ReportResultResp, BaseViewHolder>(layoutResId, data) {

    override fun convert(holder: BaseViewHolder, item: ReportResultResp) {
        holder.setText(R.id.adaReportType, ComplaintEnum.getItem(item.type.toInt()).getValue())
            ?.setText(R.id.adaTvReportContent, item.content)
            ?.setText(R.id.adaTVReportTime, item.ctime)

        val result = holder.getView<TextView>(R.id.adaTVReportResult)
        if (item.status == "1") {
            result.apply {
                text = "已处理"
                setTextColor(Color.parseColor("#FDD655"))
            }
        } else {
            result.apply {
                text = "未处理"
                setTextColor(Color.parseColor("#999999"))
            }
        }

    }
}