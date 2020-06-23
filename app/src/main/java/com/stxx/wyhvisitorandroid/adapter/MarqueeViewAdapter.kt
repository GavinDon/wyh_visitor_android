package com.stxx.wyhvisitorandroid.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.NoticeResp
import com.stxx.wyhvisitorandroid.navOption
import com.stxx.wyhvisitorandroid.widgets.HtmlUtil
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.annotations.NotNull

/**
 * desc:跑马灯效果
 * create by linan on 2018/11/14 16:55
 */

class MarqueeViewAdapter(private val lst: MutableList<NoticeResp>) :
    BaseAdapter() {


    @NotNull
    override fun getItem(position: Int) = lst[position]

    override fun getItemId(position: Int) = position.toLong()
    override fun getCount() = lst.size
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val holder: MarqueeViewHolder
        var itemView: View? = null
        if (convertView == null) {
            itemView = TextView(parent!!.context)
            itemView.textSize = 12f
            itemView.textColor = Color.parseColor("#232323")
            itemView.gravity = Gravity.CENTER_VERTICAL
            itemView.textColor = ContextCompat.getColor(parent.context, R.color.light)
            itemView.setSingleLine()
            itemView.ellipsize = TextUtils.TruncateAt.END
            holder = MarqueeViewHolder()
            holder.textView = itemView
            itemView.tag = holder

        } else {
            holder = itemView?.tag as MarqueeViewHolder
            itemView = convertView
        }
        holder.textView.text =
            HtmlCompat.fromHtml(lst[position].title ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT)
        holder.textView.setOnClickListener {
            it.findNavController()
                .navigate(
                    R.id.fragment_notice_detail,
                    bundleOf("detail" to lst[position].content.toString()),
                    navOption
                )
        }
        return itemView
    }


    class MarqueeViewHolder {
        lateinit var textView: TextView
    }
}