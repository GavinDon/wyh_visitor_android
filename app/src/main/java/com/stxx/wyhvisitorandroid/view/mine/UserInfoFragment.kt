package com.stxx.wyhvisitorandroid.view.mine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.gavindon.mvvm_lib.net.BR
import com.gavindon.mvvm_lib.net.Resource
import com.gavindon.mvvm_lib.net.SuccessSource
import com.luck.picture.lib.PictureSelector
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.base.ToolbarFragment
import com.stxx.wyhvisitorandroid.bean.UserInfoResp
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import com.stxx.wyhvisitorandroid.graphics.REQUEST_CODE_CHOOSE
import com.stxx.wyhvisitorandroid.graphics.chooseSinglePicture
import com.stxx.wyhvisitorandroid.mplusvm.MineVm
import com.stxx.wyhvisitorandroid.transformer.PicassoCircleImage
import kotlinx.android.synthetic.main.fragment_user_info.*
import java.io.File

/**
 * description:用户信息
 * Created by liNan on 2020/3/2 16:52

 */
class UserInfoFragment : ToolbarFragment() {
    override val toolbarName: Int = R.string.user_info
    override val layoutId: Int = R.layout.fragment_user_info
    private lateinit var mineVm: MineVm

    private var resourceUseInfo: Resource<BR<UserInfoResp>>? = null
    private var userInfoData: UserInfoResp? = null
    private var nickName: String? = ""

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        mineVm = getViewModel()
        resourceUseInfo = mineVm.getUserInfo().value

        //初始化数据
        if (resourceUseInfo is SuccessSource) {
            userInfoData = (resourceUseInfo as SuccessSource).body.data
            nickName = userInfoData?.true_name
            tvUserName?.text =
                if (userInfoData?.true_name.isNullOrEmpty()) userInfoData?.name else nickName
            ImageLoader.with().load(userInfoData?.icon).error(R.mipmap.default_icon)
                .placeHolder(R.mipmap.default_icon).transForm(PicassoCircleImage())
                .into(ivUserIcon)
        }


        //修改用户头像
        ivUserIcon.setOnClickListener {
            requestPermission(
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
            fullDialogFragment.show(childFragmentManager, "full")
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
}