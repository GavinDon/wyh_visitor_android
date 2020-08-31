package com.stxx.wyhvisitorandroid.view.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.emailRegex
import com.gavindon.mvvm_lib.widgets.showToast
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.adapter.AlbumGridAdapter
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.enums.ComplaintEnum
import com.stxx.wyhvisitorandroid.graphics.GalleryLayoutManager
import com.stxx.wyhvisitorandroid.graphics.REQUEST_CODE_CHOOSE
import com.stxx.wyhvisitorandroid.graphics.SelectorGlideEngine
import com.stxx.wyhvisitorandroid.graphics.chooseAlbum
import com.stxx.wyhvisitorandroid.mplusvm.ComplaintVm
import com.yalantis.ucrop.util.ScreenUtils
import kotlinx.android.synthetic.main.fragment_complaint.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.support.v4.email
import java.util.regex.Pattern


/**
 * description:投诉建议
 * Created by liNan on  2020/2/18 13:44
 */
class ComplaintFragment : ToolbarFragment() {

    private var albumAdapter: AlbumGridAdapter? = null
    private var selectList: List<LocalMedia> = ArrayList()

    override val layoutId: Int = R.layout.fragment_complaint


    private lateinit var mViewModel: ComplaintVm
    //选中的投诉类型view索引
    private var typeViewPosition = 0

    private var typeAdapter: TypeAdapter? = null
    override val toolbarName: Int by lazy {
        ComplaintEnum.getPositionByValue(
            typeAdapter?.data?.get(
                typeViewPosition
            ).toString()
        ).getValue()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        complaintEtPhone?.setText(SpUtils.get(LOGIN_NAME_SP, ""))
        complaintEtPhone.isFocusable = false
        complaintEtPhone.isEnabled = false
        chooseImage()
        editFilter()
        btnComplaintSubmit.setOnClickListener {
            postComplaint()
        }
        initComplaintType()
    }

    private fun editFilter() {
        complaintEtMobile.filterSpaceEmoji(30)
        complaintEtContent.filterSpaceEmoji(140)
        complaintEtContact.filterSpaceEmoji()
        complaintEtPhone.filterSpaceEmoji(11)
    }

    private fun initComplaintType() {
        val types = resources.getStringArray(R.array.complaint_type)
        typeAdapter = TypeAdapter(R.layout.adapter_complaint_type, types.toMutableList())
        val layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
        rvComplaintType.layoutManager = layoutManager
        rvComplaintType.adapter = typeAdapter

        typeAdapter?.setOnItemClickListener { _, view, pos ->
            //把已经选中的背景色还原
            layoutManager.findViewByPosition(typeViewPosition)?.setBackgroundColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.dividerColor
                )
            )
            //选中的背景色
            view.setBackgroundColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.colorTabSelect
                )
            )
            typeViewPosition = pos
            app_tv_Title?.text = getString(
                ComplaintEnum.getPositionByValue(
                    typeAdapter?.data?.get(
                        typeViewPosition
                    ).toString()
                ).getValue()
            )
        }
        //设置第一个为选中状态
        typeAdapter?.getViewByPosition(0, R.id.adaRbComplaintType)
            ?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorTabSelect
                )
            )
        //设置第一个为选中状态
        rvComplaintType.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val w = rvComplaintType.width
                val h = rvComplaintType.height
                if (w > 0 && h > 0) {
                    rvComplaintType.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                val firstTypeView = rvComplaintType.layoutManager?.findViewByPosition(0)
                firstTypeView?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTabSelect
                    )
                )
            }
        })
    }

    private fun postComplaint() {
        mViewModel = getViewModel()
        lifecycle.addObserver(mViewModel)
        val email = complaintEtMobile.text.toString().trim()
        val content = complaintEtContent.text.toString().trim()
        val name = complaintEtContact.text.toString().trim()
        val phone = complaintEtPhone.text.toString().trim()
        when {
            name.isEmpty() -> {
                complaintEtContact.getFocus("请输入您的姓名,以方便联系您")
            }
            !Pattern.matches(emailRegex, email.trim()) -> {
                complaintEtMobile.getFocus("请输入正确的邮箱,以方便我们联系您!")
            }
            content.isEmpty() -> {
                complaintEtContent.getFocus("请输入内容")
            }
            else -> {
                val type = ComplaintEnum.getPositionByValue(
                    typeAdapter?.data?.get(
                        typeViewPosition
                    ).toString()
                ).ordinal
                val param = arrayListOf(
                    "suggested" to name,
                    "phone" to phone,
                    "content" to content,
                    "mailbox" to email,
                    "type" to type,
                    "sources" to BuildConfig.source
                )
                val mediaData = selectList.map { if (it.isCompressed) it.compressPath else it.path }
                mViewModel.complaint(param, mediaData).observe(this, Observer {
                    handlerResponseData(it, {
                        requireContext().showToast("操作成功")
                        findNavController().navigateUp()
                    }, {})
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            selectList = PictureSelector.obtainMultipleResult(data);
            albumAdapter?.setList(selectList)
            albumAdapter?.notifyDataSetChanged()
        }
    }

    private fun chooseImage() {
        val layoutManager =
            GalleryLayoutManager(this.context, 4, GridLayoutManager.VERTICAL, false)
        albumAdapter =
            AlbumGridAdapter(this.context, AlbumGridAdapter.onAddPicClickListener {
                requestPermission2(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    chooseAlbum(selectList)
                }
            })
        rvAlbum.apply {
            setLayoutManager(layoutManager)
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
}

class TypeAdapter(layoutResId: Int, data: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.adaRbComplaintType, item)
    }
}