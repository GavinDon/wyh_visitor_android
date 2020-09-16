package com.stxx.wyhvisitorandroid.view.mine

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.Resource
import com.gavindon.mvvm_lib.net.SuccessSource
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.getStatusBarHeight
import com.gavindon.mvvm_lib.utils.rxRequestPermission
import com.gavindon.mvvm_lib.widgets.showToast
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.stxx.wyhvisitorandroid.LOGIN_NAME_SP
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.BaseActivity
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.UserInfoResp
import com.stxx.wyhvisitorandroid.bean.WXLoginResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.graphics.REQUEST_CODE_CHOOSE
import com.stxx.wyhvisitorandroid.graphics.chooseSinglePicture
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import com.stxx.wyhvisitorandroid.transformer.PicassoCircleImage
import com.stxx.wyhvisitorandroid.view.helpers.WeChatRegister
import com.stxx.wyhvisitorandroid.wxapi.WXEntryActivity.WXAUTHDATA
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.fragment_user_info.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File

/**
 * description:用户信息
 * Created by liNan on 2020/3/2 16:52

 */
class UserInfoFragment : BaseActivity() {
    //        override val toolbarName: Int = R.string.user_info
    override val layoutId: Int = R.layout.fragment_user_info
    private val mineVm: MineVm = MineView.mineVm

    private var resourceUseInfo: Resource<BR<UserInfoResp>>? = null
    private var userInfoData: UserInfoResp? = null
    private var nickName: String? = ""

    override fun onInit(savedInstanceState: Bundle?) {
        resourceUseInfo = mineVm.getUserInfo().value

        //初始化数据
        if (resourceUseInfo is SuccessSource) {
            //用户信息
            userInfoData = (resourceUseInfo as SuccessSource).body.data
            nickName = userInfoData?.true_name
            //如果用户信息表中没有手机号码 则认为是微信登陆且没有绑定手机号
            if (userInfoData?.phone.isNullOrEmpty()) {
                rlBindAccount.visibility = View.GONE
            } else {
                //绑定微信
                tvBindAccount.text = if (userInfoData?.wxid.isNullOrEmpty()) "绑定微信" else "解绑微信"
            }

            //昵称
            tvUserName?.text = if (nickName.isNullOrEmpty()) userInfoData?.name else nickName
            //用户头像
            ImageLoader.with().load(userInfoData?.icon).error(R.mipmap.default_icon)
                .placeHolder(R.mipmap.default_icon).transForm(PicassoCircleImage())
                .into(ivUserIcon)
        }
        //修改用户头像
        ivUserIcon.setOnClickListener {
            this.rxRequestPermission(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) {
                chooseSinglePicture()
            }
        }

        //修改用户名
        rlUserName.setOnClickListener {
            val fullDialogFragment = FixUserInfoFragment()
            fullDialogFragment.addOnDismissListener {
                mineVm.getUserInfo().observe(this, Observer {
                    handlerResponseData(it, { resp ->
                        tvUserName.text =
                            if (resp.data.true_name.isNullOrEmpty()) resp.data.name else resp.data.true_name
                    }, {})
                })

            }
            fullDialogFragment.show(supportFragmentManager, "full")
        }
        rlBindAccount.setOnClickListener {
            if (userInfoData?.wxid.isNullOrEmpty()) {
                //绑定微信
                wakeWxApp()
            } else {
                //解绑微信
                mineVm.wxBindOrUnBind(null).observe(this, Observer {
                    handlerResponseData(it, {
                        //liveData中微信id置为空
                        userInfoData?.wxid = ""
                        mineVm.getUserInfo().postValue(resourceUseInfo)
                        tvBindAccount.text = "绑定微信"
                        SpUtils.clearName(LOGIN_NAME_SP)
                        showToast("解绑成功")
                    }, {})
                })
            }

        }
    }


    private fun wakeWxApp() {
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "wyh_wx_login_userinfo"
        WeChatRegister.wxApi?.sendReq(req)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val wxResp = intent?.getSerializableExtra(WXAUTHDATA)
        if (wxResp is WXLoginResp) {
            val openId = wxResp.openid
            //绑定微信
            mineVm.wxBindOrUnBind(openId).observe(this, Observer {
                handlerResponseData(it, {
                    //设置wxId不为空即可
                    userInfoData?.wxid = "1234"
                    mineVm.getUserInfo().postValue(resourceUseInfo)
                    tvBindAccount.text = "解绑微信"
                    showToast("绑定成功")
                }, {})
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val select = PictureSelector.obtainMultipleResult(data)
            if (select.size > 0 && select[0].isCut) {
                val iconPath = select[0].cutPath
                mineVm.uploadIcon(File(iconPath)).observe(this, Observer {
                    handlerResponseData(it, {
                        ImageLoader.with().load("file://$iconPath")
                            .transForm(PicassoCircleImage())
                            .into(ivUserIcon)
                        userInfoData?.icon = "file://$iconPath"
                        mineVm.getUserInfo().postValue(resourceUseInfo)
                    }, {})
                })
            }

        }
    }

    override fun setStatusBar() {
        super.setStatusBar()
        titleBar?.layoutParams?.height = getStatusBarHeight(this)
        titleBar?.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTabSelect))
        app_tv_Title?.setText(R.string.user_info)
        frame_layout_title?.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTabSelect))
        toolbar_back?.setOnClickListener { this.finish() }
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(true)
            .init()
    }

    override fun permissionForResult() {
    }
}