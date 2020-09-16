package com.stxx.wyhvisitorandroid.view.mine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gavindon.mvvm_lib.net.ErrorSource
import com.gavindon.mvvm_lib.net.HttpManager
import com.gavindon.mvvm_lib.net.NotZeroSource
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.widgets.TipDialog
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.base.BaseFragment
import com.stxx.wyhvisitorandroid.bean.UserInfoResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.graphics.REQUEST_CODE_CHOOSE
import com.stxx.wyhvisitorandroid.graphics.chooseSinglePicture
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import com.stxx.wyhvisitorandroid.transformer.PicassoCircleImage
import com.stxx.wyhvisitorandroid.view.splash.WxLoginBindPhoneActivity
import kotlinx.android.synthetic.main.fragment_mine.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.io.File

/**
 * description:个人中心
 * Created by liNan on 2020/1/14 15:54

 */
class MineFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_mine

    private lateinit var mineVm: MineVm
    private var userInfoResp: UserInfoResp? = null
    private val gridArray = mutableListOf<Pair<Int, String>>()
    private lateinit var gridAdapter: GridAdapter

    //人脸识别跳转过来
    private val isFaceCome by lazy { arguments?.getBoolean(FACE_IDENTIFY) }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        if (gridArray.isNullOrEmpty()) {
            gridArray.add(R.mipmap.ic_message to getString(R.string.str_my_message))
            gridArray.add(R.mipmap.ic_car_info to getString(R.string.str_car_info))
            gridArray.add(R.mipmap.ic_scenic_server to getString(R.string.str_scenic_server))
        }
        gridAdapter = GridAdapter(R.layout.item_mine_grid, gridArray)
        rvMineGrid.adapter = gridAdapter
        itemClickListener()
        gridClickListener()

        ivMessage.setOnClickListener {
            findNavController().navigate(
                R.id.fragment_push_message, null,
                navOption
            )
        }
    }

    override fun onResume() {
        super.onResume()
        initUserInfo()
    }

    private fun initUserInfo() {
        mineVm = getViewModel()
        //保存成单例
        MineView.mineVm = mineVm
        mineVm.fetchUserInfo().observeForever {
            when (it) {
                is SuccessSource -> {
                    userInfoResp = it.body.data
                    val loginView =
                        (view ?: MineView.mineView)?.findViewById<TextView>(R.id.tvUserName)
                    loginView?.text =
                        if (userInfoResp?.true_name.isNullOrEmpty()) userInfoResp?.name else userInfoResp?.true_name
                    val iconIv =
                        (view ?: MineView.mineView)?.findViewById<ImageView>(R.id.ivUserIcon)
                    ImageLoader.with().load(userInfoResp?.icon)
                        .placeHolder(R.mipmap.default_icon)
                        .error(R.mipmap.default_icon)
                        .transForm(PicassoCircleImage())
                        .into(iconIv!!)
                    iconIv.setOnClickListener {
                        findNavController().navigate(R.id.action_user_info, null, navOption)
                    }
                    loginView?.setOnClickListener {
                        findNavController().navigate(R.id.action_user_info, null, navOption)
                    }
                    val ll = (view ?: MineView.mineView)?.findViewById<LinearLayout>(R.id.llMine)
                    ll?.get(ll.childCount - 1)?.visibility = View.VISIBLE
                    mineVm.isLoginFinish.value = false
                }
                is NotZeroSource -> {
                    unLogin()
                }
                is ErrorSource -> {
                    unLogin()
                }
            }
        }
    }

    private fun unLogin() {
        //未登陆时
        val loginView = (view ?: MineView.mineView)?.findViewById<TextView>(R.id.tvUserName)
        loginView?.text = "注册/登录"
        val iconIv = (view ?: MineView.mineView)?.findViewById<ImageView>(R.id.ivUserIcon)
        ImageLoader.with().load(R.mipmap.default_icon)
            .transForm(PicassoCircleImage())
            .into(iconIv!!)
        loginView?.setOnClickListener {
            findNavController().navigate(R.id.login_activity, null, navOption)
        }
        val ll = (view ?: MineView.mineView)?.findViewById<LinearLayout>(R.id.llMine)
        ll?.get(ll.childCount - 1)?.visibility = View.GONE
    }


    /**
     * 垂直列表点击事件
     */
    private fun itemClickListener() {
        llMine.addItemClickListener { verticalLayout, position ->
            when (position) {
                0 -> {
                    val token = judgeLogin()
                    if (token.isNotEmpty()) {
                        findNavController().navigate(R.id.action_browse_record, null, navOption)
                    } else {
                        findNavController().navigate(
                            R.id.login_activity,
                            null,
                            navOption
                        )
                    }
                }
                1 -> {
                    findNavController().navigate(R.id.fragment_complaint, null, navOption)
                }
                2 -> {
                    //调用人脸识别接口
                    if (BuildConfig.DEBUG) {
                        faceDistinguish()
                    } else {
                        faceDistinguish()
                    }
                }
                3 -> {
                    findNavController().navigate(R.id.fragment_setting, null, navOption)
                }
                4 -> {
                    TipDialog().builds {
                        message = "确认退出登录?"
                        confirm {
                            SpUtils.clearName(TOKEN)
                            //显示退出登录按钮
                            verticalLayout[verticalLayout.childCount - 1].visibility = View.VISIBLE
                            mineVm.isLoginFinish.value = true
                            HttpManager.instance.removeHeader()
                            findNavController().navigate(
                                R.id.login_activity,
                                null,
                                navOptions { })
                        }
                    }.show(this.childFragmentManager, "logout")

                }
            }
        }
    }

    /**
     * grid点击事件
     */
    private fun gridClickListener() {
        gridAdapter.setOnItemClickListener { _, _, position ->

            when (position) {

                0 -> {
                    findNavController().navigate(R.id.fragment_push_message, null, navOption)
                }
                1 -> {
                    val token = judgeLogin()
                    if (token.isNotEmpty()) {
                        findNavController().navigate(
                            R.id.fragment_webview,
                            bundleOf(
                                WEB_VIEW_URL to "${WebViewUrl.CAR_INFO}?token=$token",
                                WEB_VIEW_TITLE to R.string.str_car_info
                            ),
                            navOption
                        )
                    } else {
                        toast("请先登录")
                        findNavController().navigate(R.id.login_activity, null, navOption)
                    }
                }
                2 -> {
                    findNavController().navigate(R.id.fragment_scenic_server, null, navOption)
                }

            }
        }
    }

    /**
     * 人脸认证接口调用
     */
    private fun faceDistinguish() {
        val token = judgeLogin()
        val phone = SpUtils.get(LOGIN_NAME_SP, "")
        if (token.isNotEmpty()) {
            if (userInfoResp != null && userInfoResp?.education != "1") {
                if (phone.isEmpty()) {
                    //如果没有电话则进行手机认证
                    startActivityForResult(
                        Intent(this.context, WxLoginBindPhoneActivity::class.java),
                        BIND_PHONE_RESULT
                    )
                } else {
                    //调用相册
                    requestPermission2(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) {
                        chooseSinglePicture(false)
                    }
                }
            } else {
                toast("您已经完成认证~")
            }
        } else {
            toast("请先登录")

            findNavController().navigate(R.id.login_activity, null, navOption)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val select = PictureSelector.obtainMultipleResult(data)
            if (select.size > 0) {
                val iconPath = select[0].compressPath
                if (iconPath.isNullOrEmpty()) {
                    toast("图片出错,就重新选择")
                    return
                }
                //先进行人脸认证。再进行本地更新用户信息并上传服务器
                showFaceAuthLoading(true)
                mineVm.getFaceIdentify(File(iconPath)).observe(this, Observer {
                    handlerResponseData(it, { faceResp ->
                        if (faceResp.code == 0) {
                            //如果返回body为true代表上传成功同步更新用户信息
                            mineVm.postFaceInfo().observe(this, Observer { updateResp ->
                                if (updateResp is SuccessSource && updateResp.body) {
                                    val resourceValue = mineVm.getUserInfo().value
                                    if (resourceValue is SuccessSource) {
                                        resourceValue.body.data.education = "1"
                                        mineVm.getUserInfo().postValue(resourceValue)
                                    }
                                }
                            })
                            goBudao()
                        } else {
                            this.context?.showToast("${faceResp.message}请重新认证！")
                        }
                        showFaceAuthLoading(false)
                    }, {
                        showFaceAuthLoading(false)
                    })
                    showFaceAuthLoading(false)
                })
            }

        } else if (resultCode == BIND_PHONE_RESULT) {
            if (data != null) {
                val isBind = data.getBooleanExtra("isBind", false)
                if (isBind) {
                    goBudao()
                }
            }
        }
    }

    private fun goBudao() {
        val phone = SpUtils.get(LOGIN_NAME_SP, "")
        if (phone.isNotEmpty()) {
            //跳转到步道
            findNavController().navigate(
                R.id.fragment_webview_notitle,
                bundleOf(
                    "url" to "${WebViewUrl.AI_BUDAO}${phone}",
                    "title" to R.string.visitor_ai_budao
                )
                , navOption
            )
        } else {

        }

    }

    private fun showFaceAuthLoading(show: Boolean) {
        if (show) {
            flyFaceLoading?.visibility = View.VISIBLE
            faceLottieView?.playAnimation()
        } else {
            flyFaceLoading?.visibility = View.GONE
            faceLottieView?.clearAnimation()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        MineView.mineView = view
    }


    override fun setStatusBar() {
        mineStatusBar.layoutParams.height = getStatusBarHeight(requireContext())
        ImmersionBar.with(this)
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .init()
    }

    /**
     * 网格适配器
     */
    inner class GridAdapter(
        layoutResId: Int,
        gridArray: MutableList<Pair<Int, String>>
    ) :
        BaseQuickAdapter<Pair<Int, String>, BaseViewHolder>(layoutResId, gridArray) {
        override fun convert(holder: BaseViewHolder, item: Pair<Int, String>) {
            holder.setText(R.id.tvStatement, item.second)
                .setImageResource(R.id.ivGrid, item.first)
        }

    }
}

/**
 * 保存当前view在修改个人信息时可以进行同步更新
 */
object MineView {
    var mineView: View? = null
    lateinit var mineVm: MineVm
}