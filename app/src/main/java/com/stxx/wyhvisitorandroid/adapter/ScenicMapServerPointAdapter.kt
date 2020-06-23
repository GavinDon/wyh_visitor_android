package com.stxx.wyhvisitorandroid.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.luck.picture.lib.config.PictureMimeType
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.bean.ParkPointResp
import com.stxx.wyhvisitorandroid.bean.ServerPointResp
import com.stxx.wyhvisitorandroid.enums.ToiletTypeEnum
import com.stxx.wyhvisitorandroid.graphics.ImageLoader

/**
 * description:电子地图服务点
 * Created by liNan on 2020/4/1 17:02

 */
class ScenicMapServerPointAdapter(layoutResId: Int, data: MutableList<ServerPointResp>?) :
    BaseQuickAdapter<ServerPointResp, BaseViewHolder>(layoutResId, data) {


    private var navListener: ((position: ServerPointResp) -> Unit)? = null

    var isPark = false
    var isToilet = false

    fun onItemClick(onNavClickListener: (position: ServerPointResp) -> Unit) {
        this.navListener = onNavClickListener
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: ServerPointResp) {
        when {
            isPark -> {
                holder.setText(R.id.adaTvServerPoint, item.name)
                    .setText(R.id.adaTvScenicTotal, "总车位:${item.sum}")
                    .setText(R.id.adaTvScenicRetail, "空闲车位:${item.residue}")
                    .setGone(R.id.adaTvServerPointIntroduce, true)
                    .setGone(R.id.adaLlToilet, true)
                    .setGone(R.id.adaTvScenicTotal, false)
                    .setGone(R.id.adaTvScenicRetail, false)

                Glide.with(context)
                    .load(item.imgurl)
                    .centerCrop()
                    .placeholder(R.color.color999)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.getView(R.id.adaIvServerPoint))
            }
            isToilet -> {
                val manRetailNumber = holder.getView<TextView>(R.id.infoWindowRetailToilet)
                val womanRetailNumber = holder.getView<TextView>(R.id.infoWindowWomanRetailToilet)
                val brRetailNumber = holder.getView<TextView>(R.id.infoWindowBrRetailToilet)
                val cslMan = holder.getView<ConstraintLayout>(R.id.cslMan)
                val cslWoman = holder.getView<ConstraintLayout>(R.id.cslWoman)
                val cslBarrierFree = holder.getView<ConstraintLayout>(R.id.cslBarrierFree)

                holder.setText(R.id.adaTvServerPoint, item.name)
                    .setText(R.id.adaTvScenicTotal, "总位数:${item.sum}")
                    .setGone(R.id.adaTvServerPointIntroduce, true)
                    .setGone(R.id.adaLlToilet, false)
                    .setGone(R.id.adaTvScenicTotal, true)
                    .setGone(R.id.adaTvScenicRetail, true)

                if (item.manInfo != null) {
                    manRetailNumber.text =
                        "剩余数:${item.manInfo.sum - item.manInfo.occupation}"
                } else {
                    cslMan.visibility = View.GONE
                }
                if (item.woMenInfo != null) {
                    cslWoman.visibility = View.VISIBLE
                    womanRetailNumber.text =
                        "剩余数:${item.woMenInfo.sum - item.woMenInfo.occupation}"
                } else {
                    cslWoman.visibility = View.GONE
                }
                if (item.thirdInfo != null) {
                    brRetailNumber.text =
                        "剩余数:${item.thirdInfo.sum - item.thirdInfo.occupation}"
                } else {
                    cslBarrierFree.visibility = View.GONE
                }

                Glide.with(context)
                    .load(R.mipmap.restroom)
                    .centerCrop()
                    .placeholder(R.color.color999)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.getView(R.id.adaIvServerPoint))
            }
            else -> {
                holder.setText(R.id.adaTvServerPoint, item.name)
                    .setText(R.id.adaTvServerPointIntroduce, item.introduction)
                    .setGone(R.id.adaTvServerPointIntroduce, false)
                    .setGone(R.id.adaTvScenicTotal, true)
                    .setGone(R.id.adaTvScenicRetail, true)
                    .setGone(R.id.adaLlToilet, true)
                Glide.with(context)
                    .load(item.imgurl)
                    .centerCrop()
                    .placeholder(R.color.color999)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.getView(R.id.adaIvServerPoint))
            }
        }
        val navView = holder.getView<ImageView>(R.id.adaIvNav)
        navView.setOnClickListener {
            navListener?.invoke(item)
        }

    }
}
