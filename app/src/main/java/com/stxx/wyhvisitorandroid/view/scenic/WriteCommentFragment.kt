package com.stxx.wyhvisitorandroid.view.scenic

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gavindon.mvvm_lib.widgets.showToast
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.adapter.AlbumGridAdapter
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.graphics.REQUEST_CODE_CHOOSE
import com.stxx.wyhvisitorandroid.graphics.SelectorGlideEngine
import com.stxx.wyhvisitorandroid.graphics.chooseAlbum
import com.stxx.wyhvisitorandroid.mplusvm.ComplaintVm
import com.yalantis.ucrop.util.ScreenUtils
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_complaint.*
import kotlinx.android.synthetic.main.fragment_write_comment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_title.*
import org.jetbrains.anko.support.v4.toast
import java.net.URLEncoder

/**
 * description:填写评论
 * Created by liNan on  2020/5/7 17:15
 */
class WriteCommentFragment : ToolbarFragment() {


    private var albumAdapter: AlbumGridAdapter? = null
    private var selectList: List<LocalMedia> = ArrayList()
    private val id by lazy { arguments?.getInt("id") }

    private lateinit var mViewModel: ComplaintVm
    override val toolbarName: Int = R.string.write_comment

    override val layoutId: Int = R.layout.fragment_write_comment

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        chooseImage()
        tv_menu.text = "发布"
        tv_menu.visibility = View.VISIBLE
        tv_menu.setOnClickListener {
            uploadData()
        }
    }

    /**
     * 上传评论数据
     */
    private fun uploadData() {
        val rating = writeRatingBar.rating
        val content = writeDetail.editText?.text.toString()
        val files = selectList.map { if (it.isCompressed) it.compressPath else it.path }
        when {
            rating <= 0 -> {
                this.context?.showToast("您还未评分")
            }
            content.isBlank() -> {
                this.context?.showToast("您还未填写评价")
            }
            else -> {
                mViewModel = getViewModel()
                lifecycle.addObserver(mViewModel)
                val param = listOf(
                    "score" to rating,
                    "evaluate" to URLEncoder.encode(content, "UTF-8"),
                    "shops_id" to id
                )
                mViewModel.upComment(param, files).observe(this, Observer {
                    handlerResponseData(it, {
                        toast("评论成功待审核后发布")
                        findNavController().navigateUp()
                    }, {})
                })

            }
        }

    }

    private fun chooseImage() {
        albumAdapter =
            AlbumGridAdapter(this.context, AlbumGridAdapter.onAddPicClickListener {
                requestPermission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    chooseAlbum(selectList)
                }
            })
        writeRvAlbum.apply {
            addItemDecoration(
                GridSpacingItemDecoration(
                    4,
                    ScreenUtils.dip2px(context, 2f),
                    false
                )
            )
            adapter = albumAdapter
        }
        albumAdapter?.setOnItemClickListener { _, position ->
            if (selectList.isNotEmpty()) {
                val media = selectList[position]
                when (PictureMimeType.getMimeType(media.mimeType)) {
                    PictureConfig.TYPE_VIDEO -> {
                        PictureSelector.create(this)
                            .externalPictureVideo(media.path)
                    }
                    else -> {
                        PictureSelector.create(this)
                            .themeStyle(R.style.selectPicture)
                            .isNotPreviewDownload(true)
                            .loadImageEngine(SelectorGlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                            .openExternalPreview(position, selectList)
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            selectList = PictureSelector.obtainMultipleResult(data)
            albumAdapter?.setList(selectList)
            albumAdapter?.notifyDataSetChanged()

        }
    }
}