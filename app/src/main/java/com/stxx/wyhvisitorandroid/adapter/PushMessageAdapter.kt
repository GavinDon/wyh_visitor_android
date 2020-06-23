package com.stxx.wyhvisitorandroid.adapter

import android.widget.ImageView
import androidx.core.text.HtmlCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.utils.getCurrentDate
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.PushMessageResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.transformer.RoundedCornersTransformation
import com.stxx.wyhvisitorandroid.widgets.HtmlUtil

/**
 * description:
 * Created by liNan on 2020/4/9 9:37

 */
class PushMessageAdapter(layoutResId: Int, data: MutableList<PushMessageResp>?) :
    BaseQuickAdapter<PushMessageResp, BaseViewHolder>(layoutResId, data),LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: PushMessageResp) {

        holder.setText(R.id.adaPushMessageTitle, item.des)
            .setText(R.id.adaPushMessageDate, item.gmt_modfy)
            .setText(
                R.id.adaPushMessageContent,
                HtmlCompat.fromHtml(item.content, HtmlCompat.FROM_HTML_MODE_COMPACT, null, null)
            )

        val imageView = holder.getView<ImageView>(R.id.adaIvPushMessage)

        ImageLoader.with().load(item.imgurl)
            .error(R.mipmap.banner)
            .transForm(RoundedCornersTransformation(20, 0))
            .placeHolder(R.mipmap.banner)
            .into(imageView)
    }
}