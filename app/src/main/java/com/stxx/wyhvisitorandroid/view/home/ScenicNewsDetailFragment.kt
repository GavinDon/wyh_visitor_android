package com.stxx.wyhvisitorandroid.view.home

import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.transition.TransitionInflater
import com.gavindon.mvvm_lib.net.http
import com.gavindon.mvvm_lib.net.parse
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.utils.phoneHeight
import com.gyf.immersionbar.ImmersionBar
import com.orhanobut.logger.Logger
import com.stxx.wyhvisitorandroid.ApiService
import com.stxx.wyhvisitorandroid.BUNDLE_DETAIL
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.*
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.widgets.HtmlUtil
import kotlinx.android.synthetic.main.fragment_scenic_news_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_title.*
import org.jetbrains.anko.support.v4.dip


/**
 * description: 景点详情
 * Created by liNan on  2020/3/6 10:54
 */
class ScenicNewsDetailFragment : ToolbarFragment() {
    override val layoutId: Int = R.layout.fragment_scenic_news_detail

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        frame_layout_title.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        /*热门推荐,新闻资讯, 景区百科*/
        when (val detailData = arguments?.getSerializable(BUNDLE_DETAIL)) {
            is ScenicNewsResp -> {
                //景区资讯
                findById(ApiService.SCENIC_NEWS_ID, detailData.id)
                initView(
                    detailData.imgurl,
                    detailData.title,
                    detailData.content,
                    detailData.gmt_modfy
                )
            }
            is VegetationWikiResp -> {
                //景区百科
                findById(ApiService.SCENIC_WIKI_ID, detailData.id)
                initView(detailData.img, detailData.name, detailData.content, detailData.synopsis)
            }
            is HotRecommendResp -> {
                //热门推荐
                findById(ApiService.HOT_RECOMMEND_ID, detailData.id)
                initView(
                    detailData.imgurl,
                    detailData.title,
                    detailData.content?:"",
                    detailData.gmt_modfy
                )
            }
            is LineRecommendResp -> {
                //推荐路线
                findById(ApiService.VISIT_PARK_LINE_ID, detailData.id)
                initView(
                    detailData.imgurl,
                    detailData.title,
                    detailData.content,
                    detailData.gmt_modfy
                )
            }
            is PushMessageResp -> {
                initView(
                    detailData.imgurl,
                    detailData.title,
                    detailData.content,
                    detailData.gmt_modfy
                )
            }
        }


        sharedElementEnterTransition =
            TransitionInflater.from(this.requireContext()).inflateTransition(
                android.R.transition.move
            )

    }


    /**
     * 查找景区百科根据id(详情不根据此获取)
     * 方法只是用来上传服务器 获取足迹使用
     *
     */
    private fun findById(url: String, id: Int) {
        http?.getWithoutLoading(url, listOf("id" to id))
    }

    private fun initView(url: String, title: String, content: String, key: String) {
        ImageLoader.with().load(url).into(ivNewsDetailHead)
        tvNewsDetailTitle.text = title
        tvNewsDetailDate.text = key
        HtmlUtil().show(this.context, content, Handler {
            //        tvNewsDetailContent.text = it.obj.toString()
            tvNewsDetailContent?.text = it.obj as Spanned
            return@Handler false
        })
        tvNewsDetailContent?.movementMethod = LinkMovementMethod.getInstance()
    }


    private var isUpScroll = false
    private var isDownScroll = false

/*    private fun initScroll() {
        var posY = 0f
        var curY = 0f
        //未滚动时在Y轴的值
        val initScrollTop = scrollNews.top
        scrollNews.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    posY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    curY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    if (curY - posY <= 10) {
                        if (!isUpScroll) {
                            val animUp =
                                ObjectAnimator.ofFloat(
                                    scrollNews,
                                    "y",
                                    (scrollNews.top.toFloat()),
                                    100f
                                )
                            animUp.duration = 200
                            animUp.start()
                            isUpScroll = true
                            isDownScroll = false
                        }
                    } else {
                        if (!isDownScroll) {
                            val animDown =
                                ObjectAnimator.ofFloat(
                                    scrollNews,
                                    "y",
                                    100f,
                                    scrollNews.top.toFloat()
                                )
                            animDown.duration = 200
                            animDown.start()
                            isDownScroll = true
                            isUpScroll = false
                        }
                    }
                }
            }
            return@setOnTouchListener !isUpScroll

        }

    }*/

    override fun setStatusBar() {
        titleBar.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
        titleBar.layoutParams.height = getStatusBarHeight(this.requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .transparentStatusBar()
            .init()
    }


    override val toolbarName: Int = R.string.detail

    private val imageGetter: Html.ImageGetter = Html.ImageGetter {
        Logger.i(it)
        val drawable = decodeImage(it)
        ImageLoader.with().load(it)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        return@ImageGetter drawable
    }

    private fun decodeImage(strBase: String): BitmapDrawable? {
        var drawable: BitmapDrawable? = null
        try {
            val encodeArray = Base64.decode(strBase, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(encodeArray, 0, encodeArray.size)
            drawable = BitmapDrawable(resources, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return drawable
    }


}