package com.stxx.wyhvisitorandroid.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.stxx.wyhvisitorandroid.R
import org.jetbrains.anko.layoutInflater

/**
 * description:个人中心列表
 * Created by liNan on  2020/2/10 15:12
 */
class VerticalLayout : LinearLayout {


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalLayout)
        val dataSource = typedArray.getTextArray(R.styleable.VerticalLayout_itemDataSource)
        removeAllViews()
        if (dataSource != null && dataSource.isNotEmpty()) {
            dataSource.forEachIndexed { i, s ->
                val v = context.layoutInflater.inflate(R.layout.item_mine, null, false)
                v.findViewById<TextView>(R.id.tvItem).text = s
                addView(v)
                v.setOnClickListener {
                    itemClickListener?.invoke(this, i)
                }
            }
        }
        typedArray.recycle()

    }


    fun addItemClickListener(l: (verticalLayout: VerticalLayout, position: Int) -> Unit) {
        itemClickListener = l
    }

    private var itemClickListener: ((VerticalLayout, position: Int) -> Unit)? = null

}