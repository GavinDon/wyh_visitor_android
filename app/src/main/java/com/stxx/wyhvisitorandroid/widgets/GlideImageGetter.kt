package com.stxx.wyhvisitorandroid.widgets

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.text.Html
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.orhanobut.logger.Logger
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.stxx.wyhvisitorandroid.graphics.HtmlTagHandler
import com.stxx.wyhvisitorandroid.graphics.ImageLoader
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.concurrent.thread

/**
 * description:
 * Created by liNan on  2020/4/12 19:20
 */
class GlideImageGetter(
    private val textView: TextView,
    private val context: Context,
    private val handler: Handler
) :
    Html.ImageGetter {

    /*   override fun getDrawable(p0: String?): Drawable {
           var drawable: BitmapDrawable? = null
           Logger.i(p0.toString())
           Glide.with(context).asBitmap().load(p0)
               .error(R.mipmap.grid_line_guide)
               .into(object : ImageViewTarget<Bitmap>(NetWorkDrawable(context)) {
                   override fun setResource(resource: Bitmap?) {
                       Logger.i(drawable!!.intrinsicWidth.toString())
                       drawable = BitmapDrawable(context.resources, resource)
                       drawable!!.setBounds(
                           0,
                           0,
                           drawable!!.intrinsicWidth,
                           drawable!!.intrinsicHeight
                       )
                       textView.invalidate()
                       textView.text = textView.text
                   }
               })
           return drawable!!

       }*/
    override fun getDrawable(p0: String?): Drawable? {
        var drawable: Drawable? = null
        thread {
            try {
                drawable = Glide.with(context).load(p0).submit().get()
                if (drawable != null) {
                    val w = drawable!!.minimumWidth
                    val h = drawable!!.minimumHeight
                    drawable!!.setBounds(0, 0, w, h)
                    textView.invalidate()
                    textView.text = textView.text
                    val message = Message()
                    message.what = 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return drawable
    }
}

class HtmlUtil {

    companion object {
        const val what = 1
    }

    fun show(context: Context?, content: String, handler: Handler) {
        if (context != null) {
            val message = Message.obtain()
            thread {
                val imageGetter = Html.ImageGetter {
                    var drawable: Drawable? = null
                    try {
                        drawable = Glide.with(context).load(it).submit().get()
                        if (drawable != null) {
                            val w = drawable.minimumWidth
                            val matrix = Matrix()
                            val scale = BigDecimal(phoneWidth).div(BigDecimal(w)).toFloat()
                            Logger.i("scale=${scale}")
                            matrix.postScale(scale, scale)
                            val bitmap = drawable.toBitmap()
                            val matrixBitmap = Bitmap.createBitmap(
                                bitmap,
                                0,
                                0,
                                bitmap.width,
                                bitmap.height,
                                matrix,
                                true
                            )
                            drawable = BitmapDrawable(context.resources, matrixBitmap)
                            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)

                        } else {
                            return@ImageGetter null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return@ImageGetter drawable
                }

                val spannable =
                    HtmlCompat.fromHtml(
                        content,
                        HtmlCompat.FROM_HTML_MODE_COMPACT,
                        imageGetter,
                        HtmlTagHandler()
                    )
                message.what = what
                message.obj = spannable
                handler.sendMessage(message)
            }
        }
    }

}



