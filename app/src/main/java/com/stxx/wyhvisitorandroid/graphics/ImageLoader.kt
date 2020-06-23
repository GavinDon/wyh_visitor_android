package com.stxx.wyhvisitorandroid.graphics

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.squareup.picasso.*
import com.stxx.wyhvisitorandroid.transformer.GrayscaleTransformation
import java.io.File

/**
 * description:
 * Created by liNan on  2020/3/3 09:17
 */
object ImageLoader {

    fun with(): PicassoLoader = PicassoLoader


}

object PicassoLoader : ILoaderFrame {

    private val picasso = Picasso.get()
    private var requestCreator: RequestCreator? = null

    init {
        picasso.setIndicatorsEnabled(false)
        picasso.isLoggingEnabled = false
    }

    override fun load(uri: Uri?): ILoaderFrame {
        picasso.load(uri)
        return this
    }

    override fun load(file: File): ILoaderFrame {
        //内存加载、蓝色表示从磁盘加载、红色表示从网络加载
        /*
        *  MEMORY(Color.GREEN),
           DISK(Color.BLUE),
           NETWORK(Color.RED);*/
        requestCreator = picasso.load(file)
        return this
    }

    override fun load(resourceId: Int): ILoaderFrame {
        requestCreator = picasso.load(resourceId)
        return this
    }

    override fun load(path: String?): ILoaderFrame {
        requestCreator = picasso.load(path)
        return this
    }

    override fun transForm(transformation: Transformation): ILoaderFrame {
        requestCreator?.transform(transformation)
        return this
    }

    override fun placeHolder(placeholderResId: Int): ILoaderFrame {
        requestCreator?.placeholder(placeholderResId)

        return this
    }

    override fun error(errorResId: Int): ILoaderFrame {
        requestCreator?.error(errorResId)
        return this
    }

    override fun into(targetView: ImageView) {
        //不进行内存缓存 容易造成OOM 只使用disk缓存方式
//        requestCreator?.memoryPolicy(MemoryPolicy.NO_STORE)
        requestCreator?.into(targetView)
    }
}

abstract class GlideLoader() : ILoaderFrame {
    init {

    }
}


/**
 *图片加载
 */
interface ILoaderFrame {

    fun load(uri: Uri?): ILoaderFrame
    fun load(file: File): ILoaderFrame
    fun load(resourceId: Int): ILoaderFrame
    fun load(path: String?): ILoaderFrame
    fun transForm(transformation: Transformation): ILoaderFrame
    fun into(targetView: ImageView)
    fun placeHolder(placeholderResId: Int): ILoaderFrame
    fun error(errorResId: Int): ILoaderFrame
}