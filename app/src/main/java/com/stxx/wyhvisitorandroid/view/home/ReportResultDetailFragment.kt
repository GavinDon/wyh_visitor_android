package com.stxx.wyhvisitorandroid.view.home

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.net.RxScheduler
import com.gavindon.mvvm_lib.utils.url2File
import com.github.kittinunf.fuel.Fuel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import com.orhanobut.logger.Logger
import com.squareup.picasso.Picasso
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.adapter.AlbumOnlyShowAdapter
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.ReportResultResp
import com.stxx.wyhvisitorandroid.enums.ComplaintEnum
import com.stxx.wyhvisitorandroid.graphics.SelectorGlideEngine
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_report_detail.*
import java.io.File
import java.net.URI
import java.net.URL
import kotlin.concurrent.thread

/**
 * description:举报查询详情
 * Created by liNan on  2020/5/16 14:43
 */
class ReportResultDetailFragment : ToolbarFragment() {
    override val layoutId: Int = R.layout.fragment_report_detail

    private val imageAdapter: AlbumOnlyShowAdapter by lazy {
        AlbumOnlyShowAdapter(
            this.context,
            null
        )
    }

    private val reportResultResp by lazy { arguments?.getSerializable("detail") }

    override val toolbarName: Int by lazy {
        if (reportResultResp != null) {
            val data = reportResultResp as ReportResultResp
            return@lazy ComplaintEnum.getItem(data.type.toInt()).getValue()
        }
        R.string.app_name
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        if (reportResultResp != null) {
            val data = reportResultResp as ReportResultResp
            tvReplyTypeTx.text = getString(ComplaintEnum.getItem(data.type.toInt()).getValue())
            tvReplyContentTx.text = data.content
            tvReplyEmailTx.text = data.mailbox
            tvReplyNameTx.text = data.suggested
            tvReplyPhoneTx.text = data.phone

            if (data.status == "1") {
                tvReplyStatusTx.text = "已处理"
                cslReply.visibility = View.VISIBLE
                tvReplyTimeTx.text = data.replytime
                tvReplyContent2Tx.text = data.replyinfo
            } else {
                tvReplyStatusTx.text = "未处理"
                cslReply.visibility = View.GONE
            }

            val imageUrls = data.enclosure
            if (imageUrls != null && imageUrls.isNotEmpty()) {
                val urls = imageUrls.split(",")
                if (urls.isNotEmpty()) {
                    tvAttachImage.visibility = View.VISIBLE
                    val localMediaLst = mutableListOf<LocalMedia>()
                    imageAdapter.setList(localMediaLst)
                    rvImage.adapter = imageAdapter
                    rvImage.addItemDecoration(GridSpacingItemDecoration(4, 2, false))
                    for (url in urls) {
                        url2File(url) { convertUrl: String, file: File ->
                            val localMedia = LocalMedia().apply {
                                if (convertUrl.endsWith("mp4") || convertUrl.endsWith("avi")) {
                                    //0是video
                                    this.chooseModel = 0
                                    this.mimeType = PictureMimeType.MIME_TYPE_VIDEO
                                    this.fileName = file.name
                                }
                                path = convertUrl
                            }
                            imageAdapter.addData(listOf(localMedia))
                        }
                    }
                    imageAdapter.setOnItemClickListener { _, position ->
                        if (localMediaLst.isNotEmpty()) {
                            val media = localMediaLst[position]
                            PictureSelector.create(MVVMBaseApplication.getCurActivity())
                                .themeStyle(R.style.picture_default_style)
                                .isNotPreviewDownload(true)
                                .loadImageEngine(SelectorGlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                                .openExternalPreview(
                                    position,
                                    imageAdapter.data
                                )
                            /*         when (media.chooseModel) {
                                         0 -> {
                                             PictureSelector.create(this)
                                                 .externalPictureVideo(media.path)
                                         }
                                         else -> {
                                             PictureSelector.create(MVVMBaseApplication.getCurActivity())
                                                 .themeStyle(R.style.picture_default_style)
                                                 .isNotPreviewDownload(true)
                                                 .loadImageEngine(SelectorGlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                                                 .openExternalPreview(
                                                     position,
                                                     imageAdapter.data
                                                 )
                                         }
                                     }*/
                        }
                    }
                } else {
                    tvAttachImage.visibility = View.GONE
                }
            }
        }

    }

}