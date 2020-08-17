package com.stxx.wyhvisitorandroid.adapter

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.SparseIntArray
import android.view.View
import android.widget.AdapterViewFlipper
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.util.set
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.bgabanner.BGABanner
import com.alibaba.android.vlayout.LayoutHelper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dreamdeck.wyhapp.UnityPlayerActivity
import com.dreamdeck.wyhapp.UnityPlayerActivity2
import com.gavindon.mvvm_lib.net.*
import com.gavindon.mvvm_lib.net.BR
import com.mario.baseadapter.holder.VBaseHolderHelper
import com.orhanobut.logger.Logger
import com.squareup.picasso.Picasso
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.WebViewUrl.AI_BUDAO
import com.stxx.wyhvisitorandroid.WebViewUrl.AR_720
import com.stxx.wyhvisitorandroid.base.BaseDelegateVH
import com.stxx.wyhvisitorandroid.base.OnlyShowDelegateAdapter
import com.stxx.wyhvisitorandroid.base.VBaseAdapter
import com.stxx.wyhvisitorandroid.base.VBaseViewHolder
import com.stxx.wyhvisitorandroid.bean.*
import com.stxx.wyhvisitorandroid.enums.ScenicMApPointEnum
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.service.DownLoadAppService
import com.stxx.wyhvisitorandroid.transformer.RoundedCornersTransformation
import com.stxx.wyhvisitorandroid.transformer.ScaleInOutTransformer
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.constants.PageStyle
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import java.util.HashMap


/**
 * description:
 * Created by liNan on  2020/3/5 11:25
 */
class BannerAdapter(
    mLayoutId: Int
) : VBaseAdapter<Any>(mLayoutId) {


    override fun convert(holder: VBaseViewHolder<Any>, item: Any?) {
        //背景
        val bannerView = holder.getView<BGABanner>(R.id.homeTopBanner)
        bannerView.setData(listOf(R.mipmap.banner2, R.mipmap.banner1), null)
        bannerView.setAdapter { _, itemView, model, _ ->
            ImageLoader.with().load(model as Int)
                .into(itemView as ImageView)
        }
        //空气指数
        val airIndex = holder.getView<TextView>(R.id.tvAir)
        //湿度
        val hum = holder.getView<TextView>(R.id.tvHumidity)
        //在园人数
        val nowPeople = holder.getView<TextView>(R.id.tvVisitorNum)
        //游园指数
        val visitIndex = holder.getView<TextView>(R.id.tvVisitorIndex)
        //温度
        val homeTVTempera = holder.getView<TextView>(R.id.homeTVTempera)
        //多云转睛
        val weather = holder.getView<TextView>(R.id.homeTvWeather)

        /*  val weatherNow =
              Fuel.get(ApiService.WEATHER_NOW).rxResponseObject(gsonDeserializer<BR<WeatherResp>>())
                  .toObservable()
          val lifestyle = Fuel.get(ApiService.WEATHER_LIFESTYLE)
              .rxResponseObject(gsonDeserializer<BR<List<WeatherLifestyle>>>())
              .toObservable()

          val nowPe = Fuel.get(ApiService.REAL_TIME_NUM_TOTAL)
              .rxResponseObject(gsonDeserializer<BR<RealPeopleNum>>()).toObservable()*/

        /*    val a = Observable.mergeDelayError(
                weatherNow,
                lifestyle,
                nowPe
            ).compose(RxScheduler.applyScheduler())
                .subscribe({
                    if (null != it) {
                        val data = it.data
                        when (data) {
                            is WeatherResp -> {
                                weather.text = data.cond_txt
                                if (data.tmp.isNotEmpty()) {
                                    homeTVTempera.text = "${data.tmp}°"
                                }
                                if (!data.hum.isNullOrEmpty()) {
                                    hum.text = "${data.hum}%"
                                }
                            }
                            is RealPeopleNum -> {
                                nowPeople.text = data.history_num_total.toString()
                            }
                            else -> {
                                val lifestyle = data as List<WeatherLifestyle>
                                airIndex.text = "舒适"
                                visitIndex.text = "适宜"
    //                            visitIndex.text = lifestyle[6].brf
                            }
                        }
                    } else {
                    }
                }, {
                })*/


        /////////////////////////

        if (singleData != null) {
            for (itemData in singleData!!) {
                if (itemData is SuccessSource<*>) {
                    val brData = itemData.body as BR<*>
                    val data = brData.data
                    if (brData.code == 0) {
                        if (data is List<*>) {
                            if (!data.isNullOrEmpty())
                                when {
                                    data[0] is BannerResp -> {
                                        //设置banner 数据
//                                        bannerView.setData(data, null)
                                    }
                                    data[0] is WeatherLifestyle -> {
                                        //设置生活指数
                                        for (weatherItem in data) {
                                            val weatherData = (weatherItem as WeatherLifestyle)
                                            //旅游指数
                                            if (weatherData.type == "trav") {
                                                visitIndex.text = weatherData.brf
                                            } else if (weatherData.type == "air") {
                                                airIndex.text = weatherData.brf
                                            }
                                            visitIndex.text = "较适宜"
                                        }
                                    }
                                }
                        } else {
                            when (data) {
                                is WeatherResp -> {
                                    //天气数据
                                    weather.text = data.cond_txt
                                    if (data.tmp.isNotEmpty()) {
                                        homeTVTempera.text = "${data.tmp}°"
                                    }
                                    if (!data.hum.isNullOrEmpty()) {
                                        hum.text = "${data.hum}%"
                                    }
                                }
                                is RealPeopleNum -> {
                                    //入园人数
                                    nowPeople.text = data.real_time_num_total.toString()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class NoticeAdapter(
    mLayoutId: Int
) : VBaseAdapter<NoticeResp>(mLayoutId) {
    override fun convert(holder: VBaseViewHolder<NoticeResp>, item: NoticeResp?) {
        val marqueeView = holder.getView<AdapterViewFlipper>(R.id.marquee_view)
        val marqueeZuix = holder.getView<TextView>(R.id.marquee_zuix)
        val marqueeZixun = holder.getView<TextView>(R.id.marquee_zix)
        marqueeZuix.typeface =
            Typeface.createFromAsset(holder.itemView.context.assets, "fonts/new_notice.ttf")
        marqueeZixun.typeface =
            Typeface.createFromAsset(holder.itemView.context.assets, "fonts/new_notice.ttf")
        if (this.singleData.isNullOrEmpty()) {
//            marqueeView.adapter = MarqueeViewAdapter(mutableListOf(NoticeResp("暂无公告...")))
        } else {
            marqueeView.adapter = MarqueeViewAdapter(this.singleData!!.toMutableList())
        }
    }
}

/**
 * 热门推荐
 */
class HotRecommendAdapter(
    mLayoutId: Int
) : VBaseAdapter<HotRecommendResp>(mLayoutId) {

    override fun convert(holder: VBaseViewHolder<HotRecommendResp>, item: HotRecommendResp?) {
        val context = holder.itemView.context
        //viewPager2使用
        val bannerViewPager =
            holder.getView<BannerViewPager<HotRecommendResp, BannerViewHolder>>(R.id.banner_view)
        with(bannerViewPager) {
            setIndicatorVisibility(View.GONE)
            adapter = BannerAdpater()
            setAutoPlay(false)
            setPageStyle(PageStyle.MULTI_PAGE_SCALE)
            setPageMargin(context.dip(10))
            setRevealWidth(context.dip(30))
            addPageTransformer(ScaleInOutTransformer())
            create(singleData)
        }
        val tvHotRecommendMore = holder.getView<TextView>(R.id.tvHotRecommendMore)
        tvHotRecommendMore.setOnClickListener {
            it.findNavController().navigate(R.id.fragment_hot_recommend_more, null, navOption)
        }

        /* val rv = holder.getView<RecyclerView>(R.id.rvHotRecommend)
         rv.apply {
             setRecycledViewPool(recycledViewPool)
             recycledViewPool.setMaxRecycledViews(0, 10)
 //            adapter = HotHorizontalAdapter(R.layout.card_hot_recommend, singleData?.toMutableList())
             rv.smoothScrollToPosition(1)
         }*/
    }

    inner class BannerAdpater : BaseBannerAdapter<HotRecommendResp, BannerViewHolder>() {
        override fun getLayoutId(viewType: Int) = R.layout.card_hot_recommend

        override fun createViewHolder(itemView: View, viewType: Int): BannerViewHolder {

            return BannerViewHolder(itemView)
        }

        override fun onBind(
            holder: BannerViewHolder,
            item: HotRecommendResp,
            position: Int,
            pageSize: Int
        ) {
            holder.itemView.setOnClickListener {
                it.findNavController()
                    .navigate(
                        R.id.fragment_scenic_news_detail,
                        bundleOf("detail" to item),
                        navOption
                    )
            }
            holder.bindData(item, position, pageSize)
        }
    }

    inner class BannerViewHolder(itemView: View) :
        com.zhpan.bannerview.BaseViewHolder<HotRecommendResp>(itemView) {
        override fun bindData(data: HotRecommendResp, position: Int, pageSize: Int) {
            itemView.findViewById<TextView>(R.id.tvScenicSummary).text =
                HtmlCompat.fromHtml(data.content ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT)

            itemView.findViewById<TextView>(R.id.tvScenicName).text = data.title
            val iv = itemView.findViewById<ImageView>(R.id.ivScenicPic)
            ImageLoader.with().load(data.imgurl).transForm(
                RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.TOP)
            ).into(iv)
        }
    }
}


/**
 * 首页网格布局
 */
class GridAdapter(layoutId: Int, layoutHelper: LayoutHelper) :
    com.mario.baseadapter.VBaseAdapter<GridBean>(layoutId, layoutHelper) {
    override fun convert(helper: VBaseHolderHelper, t: GridBean, position: Int) {
        helper.setText(R.id.tvStatement, helper.convertView.context.getString(t.strId))
        helper.setImageResource(R.id.ivGrid, t.imgId)
        helper.setItemChildClickListener(R.id.llHomeGrid)
        helper.addOnItemChildClickListener { view, _ ->
            when (t.strId) {
                R.string.grid_visit -> {
                    //游客服务
                    view.findNavController().navigate(R.id.action_visitor_server, null, navOption)
                }
                R.string.grid_smart_car -> {
                    //智慧停车
                    view.findNavController().navigate(
                        R.id.fragment_scenic, bundleOf(
                            BUNDLE_SELECT_TAB to ScenicMApPointEnum.PARK.ordinal,
                            BUNDLE_IS_ROBOT to true
                        ),
                        navOption
                    )
                }
                R.string.grid_ar_science -> {
                    view.context.startActivity<UnityPlayerActivity2>()
                    //ar科普
                    /*      if (checkInstallAr != null) {
                              view.context.startActivity(Intent().run {
                                  setClassName(
                                      "com.stxx.wyh_unity_ar",
                                      "com.stxx.wyh_unity_ar.UnityPlayerActivity"
                                  )
                              })
                          } else {
                              val intent = Intent(view.context, DownLoadAppService::class.java)
                              view.context.startService(intent)
                              DownLoadAppService.REQUEST_CANCEL
                          }*/
                }
                R.string.grid_book -> {
                    val token = judgeLogin()
                    if (token.isNotEmpty()) {
                        view.findNavController().navigate(
                            R.id.fragment_webview, bundleOf(
                                "url" to "${WebViewUrl.BOOK}&token=$token",
                                "title" to R.string.grid_smart_book
                            ), navOption
                        )
                    } else {
                        view.findNavController().navigate(R.id.login_activity, null, navOption)
                    }
                }
                R.string.grid_scenic_wiki -> {
                    //景区百科
                    view.findNavController()
                        .navigate(R.id.action_vegetation_wiki, null, navOption)
                }
                R.string.str_ar -> {
                    //720虚拟游园
                    view.findNavController()
                        .navigate(R.id.fragment_ar_more, null, navOption)
                }
                R.string.visitor_ai_budao -> {
                    view.findNavController().navigate(
                        R.id.fragment_webview_notitle,
                        bundleOf(
                            "url" to AI_BUDAO,
                            "title" to R.string.visitor_ai_budao
                        )
                        , navOption
                    )
                }
                R.string.grid_plant_wiki -> {
                    view.findNavController().navigate(R.id.fragment_plant_wiki, null, navOption)
                }
            }
        }

    }

    private fun navigatorWebView(view: View, bundle: Bundle) {
        view.findNavController().navigate(
            R.id.fragment_webview,
            bundle
            , navOption
        )
    }
}

class NewsAdapters(
    layoutId: Int, layoutHelper: LayoutHelper
) :
    com.mario.baseadapter.VBaseAdapter<ScenicNewsResp>(layoutId, layoutHelper) {

    override fun convert(helper: VBaseHolderHelper, t: ScenicNewsResp?, position: Int) {
        helper.setText(R.id.tvNewsTitle, t?.title)
            .setText(R.id.tvNewsContent, t?.des)

        val iv = helper.getImageView(R.id.ivNewsPic)

        Picasso.get().load(t?.imgurl).resizeDimen(R.dimen.dp_92, R.dimen.dp_82)
            .transform(RoundedCornersTransformation(20, 0)).into(iv)

        helper.convertView?.setOnClickListener {
            val extra = FragmentNavigatorExtras(iv to ViewCompat.getTransitionName(iv)!!)
            it.findNavController()
                .navigate(R.id.action_news_detail, bundleOf("detail" to t), navOption, extra)
        }
    }
}

/**
 * 线路推荐
 */
class LineRecommendAdapter(
    layoutId: Int, layoutHelper: LayoutHelper
) :
    com.mario.baseadapter.VBaseAdapter<LineRecommendResp>(layoutId, layoutHelper) {

    override fun convert(helper: VBaseHolderHelper, t: LineRecommendResp?, position: Int) {
        helper.setText(R.id.tvNewsTitle, t?.title)
            .setText(
                R.id.tvNewsContent,
                HtmlCompat.fromHtml(t?.content ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT)
            )

        val iv = helper.getImageView(R.id.ivNewsPic)

        Picasso.get().load(t?.imgurl).resizeDimen(R.dimen.dp_92, R.dimen.dp_82)
            .transform(RoundedCornersTransformation(20, 0)).into(iv)



        helper.convertView?.setOnClickListener {
            val extra = FragmentNavigatorExtras(iv to ViewCompat.getTransitionName(iv)!!)
            it.findNavController()
                .navigate(R.id.action_news_detail, bundleOf("detail" to t), navOption, extra)
        }
    }
}

class Ar720Adapter(layoutId: Int) : VBaseAdapter<Ar720Resp>(layoutId) {

    override fun convert(holder: VBaseViewHolder<Ar720Resp>, item: Ar720Resp?) {
        val rv = holder.getView<RecyclerView>(R.id.rvArRecommend)
        val more = holder.getView<TextView>(R.id.arTvMore)
        rv.adapter = AR720RowAdapter(R.layout.card_ar_visit, singleData as MutableList<Ar720Resp>?)
        (rv.adapter as AR720RowAdapter).setOnItemClickListener { _, view, position ->
            //获取720 场景id
            val scenicId = singleData?.get(position)?.pid ?: 1
            val name = singleData?.get(position)?.name
            view.findNavController()
                .navigate(
                    R.id.fragment_webview,
                    bundleOf(
                        Pair(WEB_VIEW_URL, "${AR_720}${scenicId}"),
                        Pair(WEB_VIEW_TITLE, name)
                    ),
                    navOption
                )
        }
        more.setOnClickListener {
            it.findNavController().navigate(R.id.fragment_ar_more, null, navOption)
        }
    }
}

class AR720RowAdapter(layoutResId: Int, data: MutableList<Ar720Resp>?) :
    BaseQuickAdapter<Ar720Resp, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: Ar720Resp) {
        holder.setText(R.id.adaArTVName, item.name)
        val imageView = holder.getView<ImageView>(R.id.adaArIv)
        ImageLoader.with().load(item.imgurl).into(imageView)


    }

}


/**
 * 列表标题
 * @param isLine 是否是线路推荐点击更多事件 true为线路推荐
 */
class TitleAdapter(
    @StringRes val titleRes: Int,
    @StringRes val titleResSub: Int,
    val isLine: Boolean = false
) :
    OnlyShowDelegateAdapter(
        R.layout.vlayout_title
    ) {
    override fun onBindViewHolder(holder: BaseDelegateVH, position: Int) {
        holder.setText(R.id.vlayoutTitle, titleRes)
            .setText(R.id.vlayoutTitleSub, titleResSub)
        holder.getView<TextView>(R.id.tvMore)
            ?.setOnClickListener {
                if (isLine) {
                    it.findNavController()
                        .navigate(R.id.fragment_line_recommend, null, navOption)
                } else {
                    it.findNavController().navigate(
                        R.id.fragment_news_all,
                        null, navOption
                    )
                }
            }

    }
}