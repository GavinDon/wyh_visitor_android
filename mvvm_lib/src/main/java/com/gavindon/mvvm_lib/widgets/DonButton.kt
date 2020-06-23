package com.gavindon.mvvm_lib.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import com.gavindon.mvvm_lib.R
import com.gavindon.mvvm_lib.utils.dp2px

/**
 * description:
 * Created by liNan on  2020/2/6 11:25
 */
class DonButton : AppCompatButton {


    //colorStates列表
    private val states = Array(6) {
        intArrayOf()
    }

    private var pressBackgroundColor = 0
    private var unableBackgroundColor = 0
    private var normalBackgroundColor = 0
    private val backgroundColors = IntArray(6)

    private var textNormalColor = 0
    private var textPressColor = 0

    private val textColors = IntArray(6)

    private var circleRadius = 0


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DonButton)

        normalBackgroundColor =
            typedArray.getColor(R.styleable.DonButton_normalBackgroundColor, Color.GREEN)

        pressBackgroundColor =
            typedArray.getColor(R.styleable.DonButton_pressBackgroundColor, Color.LTGRAY)

        unableBackgroundColor =
            typedArray.getColor(R.styleable.DonButton_unableBackgroundColor, Color.BLUE)

        /*字体color*/
        textNormalColor = typedArray.getColor(R.styleable.DonButton_normalTextColor, Color.BLACK)
        textPressColor = typedArray.getColor(R.styleable.DonButton_pressTextColor, Color.RED)

        circleRadius = typedArray.getDimension(R.styleable.DonButton_circleRadius, 0f).toInt()

        typedArray.recycle()
        setup()
    }

    private fun setup() {
        initState()
        gravity = Gravity.CENTER
        isClickable = true
        isFocusable = true
        background = getStateListDrawable()
        setTextColor(ColorStateList(states, textColors))
//        translationZ = 10f
    }

    /**
     * 背景drawable,文本颜色
     */
    private fun initState() {
        states[0] = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
        states[1] = intArrayOf(android.R.attr.state_enabled)
        states[2] = intArrayOf()


        backgroundColors[0] = pressBackgroundColor
        backgroundColors[1] = normalBackgroundColor
        backgroundColors[2] = unableBackgroundColor

        textColors[0] = textPressColor
        textColors[1] = textNormalColor
        textColors[2] = textNormalColor

    }


    private fun getStateListDrawable(): StateListDrawable {
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(states[0], pressBackgroundColor.toDrawable)
        stateListDrawable.addState(states[1], normalBackgroundColor.toDrawable)
        stateListDrawable.addState(states[2], unableBackgroundColor.toDrawable)
        return stateListDrawable
    }


    private val (Int).toDrawable: GradientDrawable
        get() {
            val gradientDrawable = GradientDrawable()
            //ColorStateList 中定义的默认 Item 一定要放在最下面
            gradientDrawable.setColor(this)
            gradientDrawable.cornerRadius = circleRadius.dp2px(this@DonButton.context)
            return gradientDrawable
        }

}