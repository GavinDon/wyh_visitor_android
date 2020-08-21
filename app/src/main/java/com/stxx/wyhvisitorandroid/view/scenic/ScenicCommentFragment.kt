package com.stxx.wyhvisitorandroid.view.scenic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.SparseBooleanArray
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.baidu.geofence.GeoFence
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.utils.NotificationUtil
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.adapter.ScenicCommentAdapter
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.bean.ServerPointResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.location.showWakeApp
import com.stxx.wyhvisitorandroid.mplusvm.CommentViewModel
import com.stxx.wyhvisitorandroid.mplusvm.ScenicVm
import com.stxx.wyhvisitorandroid.service.PlaySoundService
import com.stxx.wyhvisitorandroid.transformer.PicassoCircleImage
import com.stxx.wyhvisitorandroid.widgets.ExpandableTextView
import com.stxx.wyhvisitorandroid.widgets.NineGridView
import kotlinx.android.synthetic.main.fragment_comment.*


/**
 * description:景点详情带评论
 * Created by liNan on 2020/3/11 10:10

 */
class ScenicCommentFragment : BaseFragment() {


    private lateinit var mViewModel: CommentViewModel
    //获取从景点页面传过来的值
    private var detailData: ServerPointResp? = null
    //当前经纬度
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    //从搜索过来的值
    private val scenicType by lazy { arguments?.getString("type") }
    private var scenicId: Int = -1


    override val layoutId: Int = R.layout.fragment_comment

    private val mAdapter: ScenicCommentAdapter by lazy {
        ScenicCommentAdapter(
            R.layout.adapter_comment,
            null
        )
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mViewModel = getViewModel()
        lifecycle.addObserver(mViewModel)
        rvComment?.let {
            it.addItemDecoration(DividerItemDecoration(this.context, RecyclerView.VERTICAL))
            it.adapter = mAdapter
        }
        //获取景点的详情
        if (arguments?.getSerializable(BUNDLE_SCENIC_DETAIL) != null) {
            detailData = arguments?.getSerializable(BUNDLE_SCENIC_DETAIL) as ServerPointResp
            scenicId = detailData!!.id
            loadView()
            loadCommentData()
            loadMoreCommentData()
        } else if (!scenicType.isNullOrEmpty()) {
            //从搜索界面过来 需要获取景点id查找详情
            scenicId = arguments?.getInt("id") ?: -1
            //通过id跳转对应信息界面 1主要景点、2特色展区、3景区植物、4商铺、5停车场、6服务区 7厕所
            when (scenicType?.toInt()) {
                1, 2, 3, 4, 6 -> {
                    loadData(ApiService.SCENIC_MAP_POINT_ID)
                }
                5 -> {
                    //停车场
                    loadData(ApiService.PARK_LST_URL_ID)
                }
                7 -> {
                    //厕所
                    loadData(ApiService.TOILET_LST_URL_ID)
                }
            }
        }

        tvWriteComment.setOnClickListener {
            if (judgeLogin().isNotEmpty()) {
                findNavController().navigate(
                    R.id.fragment_write_comment,
                    bundleOf("id" to scenicId),
                    navOption
                )
            } else {
                findNavController().navigate(R.id.login_activity, null, navOption)
            }
        }
        commentNav.setOnClickListener {
            if (detailData?.lngLat.isNullOrEmpty()) {
                this.context?.showToast("点位正在采集中,暂不能导航")
            } else {
                findNavController().navigate(
                    R.id.fragment_route_plant,
                    bundleOf(BUNDLE_SCENIC_DETAIL to detailData),
                    navOption
                )
            }

        }
        tvVoiceExplain.setOnClickListener {
            if (!NotificationUtil.hasNotifyEnable()) {
                this.context?.showToast("使用该功能需要打开通知权限!")
                NotificationUtil.settingNotify()
            } else {
                Intent(this.context, PlaySoundService::class.java).also {
                    it.putExtra(PlaySoundService.SOUND_SOURCE, detailData)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this.context?.startForegroundService(it)
                    } else {
                        this.context?.startService(it)
                    }
                }
            }
        }

    }

    private fun loadView() {
        ImageLoader.with().load(detailData?.imgurl).into(ivCommentDetailHead)
        tvNCommentDetailTitle.text = detailData?.name
        tvCommentDetailDate.text = detailData?.gmt_create
        tvCommentDetailContent.text = detailData?.introduction
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
//        toolbar.title = detailData?.name
    }

    /**
     * @param url
     * 根据type和id获取具体的景点数据
     */
    private fun loadData(url: String) {
        mViewModel.getServicePointById(scenicId, url)
            .observe(this, Observer {
                handlerResponseData(it, { resp ->
                    detailData = resp.data
                    loadView()
                    loadCommentData()
                    loadMoreCommentData()
                }, {
                    this.context?.showToast("暂无数据")
                })
            })
    }

    //加载第几页
    private var pageNum: Int = 1
    private var dataCount: Int = 0
    @SuppressLint("SetTextI18n")
    private fun loadCommentData() {

        if (!mAdapter.data.isNullOrEmpty()) {
            tvAllCommentNum.text =
                "${resources.getString(R.string.all_comment)}(${dataCount})"
            return
        }
//        tvAllCommentNum.text =
//            "${resources.getString(R.string.all_comment)}(${dataCount})"
        //获取评论列表
        mViewModel.getComment(pageNum, scenicId).observe(this, Observer {
            handlerResponseData(it, { resp ->
                dataCount = resp.count
                pageNum++
                tvAllCommentNum.text =
                    "${resources.getString(R.string.all_comment)}(${dataCount})"
                mAdapter.setList(resp.data.toMutableList())

            }, {
            })
        })
    }

    private fun loadMoreCommentData() {
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            if (pageNum < dataCount.pageTotalNum) {
                //获取评论列表
                mViewModel.getComment(pageNum, scenicId).observe(this, Observer {
                    handlerResponseData(it, { resp ->
                        pageNum++
                        mAdapter.addData(resp.data.toMutableList())
                        mAdapter.loadMoreModule.loadMoreComplete()
                    }, {
                        mAdapter.loadMoreModule.loadMoreFail()
                    })
                })
            } else {
                mAdapter.loadMoreModule.loadMoreEnd()
            }
        }
    }

    override fun setStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .init()
    }
}