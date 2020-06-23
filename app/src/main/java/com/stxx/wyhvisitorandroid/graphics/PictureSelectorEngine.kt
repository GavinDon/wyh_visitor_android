package com.stxx.wyhvisitorandroid.graphics

import android.content.Context
import android.widget.ImageView
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import com.squareup.picasso.Picasso

/**
 * description:
 * Created by liNan on 2020/4/1 11:03

 */
object PictureSelectorEngine : ImageEngine {

    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        Picasso.get() //
            .load(url) //
            .resize(300, 300) //必须设置了，没设置这个还是预览不显示，自己调整试下
            .centerCrop()
            .tag(context) //
            .into(imageView)
    }

    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView?,
        callback: OnImageCompleteCallback?
    ) {
        Picasso.get() //
            .load(url) //
            .resize(300, 300) //必须设置了，没设置这个还是预览不显示，自己调整试下
            .centerCrop()
            .tag(context) //
            .into(imageView)
    }

    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView?
    ) {
        Picasso.get() //
            .load(url) //
            .resize(300, 300) //必须设置了，没设置这个还是预览不显示，自己调整试下
            .centerCrop()
            .tag(context) //
            .into(imageView)
    }

    override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
        Picasso.get().load(url)
            .into(imageView)

    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        Picasso.get() //
            .load(url) //
            .resize(300, 300) //必须设置了，没设置这个还是预览不显示，自己调整试下
            .centerCrop()
            .tag(context) //
            .into(imageView)
    }

    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        Picasso.get().load(url)
            .into(imageView)
    }
}