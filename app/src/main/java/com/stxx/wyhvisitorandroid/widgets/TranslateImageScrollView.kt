package com.stxx.wyhvisitorandroid.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

/**
 * description:景点详情滑动详情移动到顶部
 * Created by liNan on  2020/2/7 13:30
 */
class TranslateImageScrollView : ScrollView {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
    }


    private var listener: ((y: Boolean) -> Unit)? = null

    fun addOnScrollChanged(listener: (y: Boolean) -> Unit) {
        this.listener = listener
    }

    var posY = 0f
    var curY = 0f

    override fun performClick(): Boolean {
        return super.performClick()
    }

    /*override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                posY = event.y
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                curY = event.y
            }
            MotionEvent.ACTION_UP -> {
                if (curY - posY > 0) {
                    listener?.invoke(false)
                } else {
                    listener?.invoke(true)
                }
            }
        }
        return false
    }*/
}