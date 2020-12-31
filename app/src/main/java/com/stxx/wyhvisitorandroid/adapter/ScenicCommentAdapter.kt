package com.stxx.wyhvisitorandroid.adapter

import android.util.SparseBooleanArray
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.utils.formatDate2YMD
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.ScenicCommentResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.graphics.SelectorGlideEngine
import com.stxx.wyhvisitorandroid.transformer.PicassoCircleImage
import com.stxx.wyhvisitorandroid.widgets.ExpandableTextView
import com.stxx.wyhvisitorandroid.widgets.NineGridView
import java.net.URLDecoder

/**
 * description:景点评论
 * Created by liNan on  2020/5/7 16:00
 */
class ScenicCommentAdapter(layoutResId: Int, data: MutableList<ScenicCommentResp>?) :
    BaseQuickAdapter<ScenicCommentResp, BaseViewHolder>(layoutResId, data), LoadMoreModule {

    private val stateArray: SparseBooleanArray = SparseBooleanArray()


    init {
        if (data != null) {
            val stateArray = SparseBooleanArray()
            for (i in 0 until data.size) {
                stateArray.put(i, true)
            }
        }
    }


    override fun convert(holder: BaseViewHolder, item: ScenicCommentResp) {

        holder.setText(R.id.adaCommentTime, formatDate2YMD(item.create_date))
            .getView<RatingBar>(R.id.adaCommentRatingBar)
            .rating = item.score.toFloat()
        //用户名
        val tvUserName = holder.getView<TextView>(R.id.adaTvCommentName)
        var userName = ""
        if (item.name.length > 8) {
            userName = item.name.substring(0, 3) + "****" + item.name.substring(7, item.name.length)
        }
        tvUserName.text = item.true_name ?: userName
        val foldingLayout = holder.getView<ExpandableTextView>(R.id.adaCommentContent)
        //用户头像
        val icon = holder.getView<ImageView>(R.id.adaIvCommentIcon)
        ImageLoader.with().load(item.icon).error(R.mipmap.default_icon)
            .transForm(PicassoCircleImage())
            .into(icon)
//        val decoderComment = URLDecoder.decode(item.evaluate, "UTF-8")
        val decoderComment = item.evaluate
        foldingLayout.setText(
            decoderComment, stateArray, holder.adapterPosition
        )
        foldingLayout.tag = holder.adapterPosition
        foldingLayout.setOnExpandStateChangeListener { textView, isExpanded ->
            val position = textView.tag as Int
            stateArray.put(position, !isExpanded)
        }
        val galleryAdapter by lazy { AlbumOnlyShowAdapter(this.context, null) }
        if (item.url != null && item.url.isNotEmpty()) {
            val urls = item.url.split(",")
            if (urls.isNotEmpty()) {
                val rvGallery = holder.getView<RecyclerView>(R.id.rvShowGallery)
                val localMediaLst = mutableListOf<LocalMedia>()
                for (i in urls) {
                    val localMedia = LocalMedia().apply {
                        path = i
                    }
                    localMediaLst.add(localMedia)
                }
                galleryAdapter.setList(localMediaLst)
                rvGallery.adapter = galleryAdapter
                rvGallery.addItemDecoration(GridSpacingItemDecoration(4, 2, false))
                galleryAdapter.setOnItemClickListener { v, position ->
                    PictureSelector.create(MVVMBaseApplication.getCurActivity())
                        .themeStyle(R.style.picture_default_style)
                        .isNotPreviewDownload(true)
                        .loadImageEngine(SelectorGlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                        .openExternalPreview(position, galleryAdapter.data)
                }
            }
        }


    }

}