package com.stxx.wyhvisitorandroid.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.stxx.wyhvisitorandroid.R
import org.jetbrains.anko.*


/**
 * description:可折叠textView
 * Created by liNan on 2020/3/11 9:05

 */
class TextViewFoldingLayout : FrameLayout {

    //显示内容textView
    private lateinit var contentTextView: TextView

    //收起/展开textView
    private lateinit var expandTextView: TextView

    //可显示几行内容(多余的则隐藏起来)
    private var shortLineCount: Int = 3
    private var shortHeight = 0

    //文字完全展开需要几行
    private var longLineCount: Int = 1
    private var longHeight = 0

    private var isOpen: Boolean = false

    private lateinit var contentTextViewLP: LinearLayout.LayoutParams

    companion object {
        private const val OPEN = "全文"
        private const val CLOSE = "收起"
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initTextView()
    }


    private fun initTextView(): LinearLayout {

        return verticalLayout {
            contentTextView = textView {
                textColor = ContextCompat.getColor(context, R.color.fontGrey)
                textSize = 16f
                tag = "contentTextView"
            }.lparams(matchParent, wrapContent) { }
            expandTextView = textView {
                textSize = 16f
                textColor = ContextCompat.getColor(context, R.color.fontGrey3)
                setOnClickListener {
                    toggle()
                }
            }.lparams {
                gravity = GravityCompat.END
                horizontalPadding = dip(20)
                verticalPadding = dip(10)
            }
        }


    }


    fun setContentText(strContent: String) {

        contentTextView.text = strContent

        /*    if (longHeight <= 0) {
                contentTextView.post {
                    //完全显示需要几行显示
                    longLineCount = contentTextView.lineCount
                    //完全显示的高度
                    longHeight = contentTextView.measuredHeight
                    measureHeight()
                }
            } else {
                measureHeight()
            }*/


        /*   contentTextView.post {
               //完全显示需要几行显示
               longLineCount = contentTextView.lineCount
               //完全显示的高度
               longHeight = contentTextView.measuredHeight
               if (longHeight > 0) {
                   //收缩起来的高度
                   shortHeight = longHeight / longLineCount * shortLineCount

                   if (longLineCount > 0) {
                       //当文本内容的高度小于设置的最大显示行, 则不显示展开/收起按钮
                       if (longLineCount <= shortLineCount) {
                           expandTextView.visibility = View.GONE
                           expandTextView.text = CLOSE
                       } else {
                           expandTextView.visibility = View.VISIBLE
                           expandTextView.text = OPEN
                           contentTextViewLP =
                               contentTextView.layoutParams as LinearLayout.LayoutParams
                           //可显示的高度
                           contentTextViewLP.height = shortHeight

                           contentTextView.apply {
                               maxLines = shortLineCount
                               layoutParams = contentTextViewLP
                               ellipsize = TextUtils.TruncateAt.END
                           }
                       }
                   }
               }
           }*/
        /*    viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                     //在recycleView中总是返回false
                    if (viewTreeObserver.isAlive) {
                        //完全显示需要几行显示
                        longLineCount = contentTextView.lineCount
                        //完全显示的高度
                        longHeight = contentTextView.height
                        //收缩起来的高度
                        shortHeight = longHeight / longLineCount * shortLineCount

                        if (longLineCount > 0) {
                            *//*当文本内容的高度小于设置的最大显示行,则不显示展开/收起按钮*//*
                        if (longLineCount <= shortLineCount) {
                            expandTextView.visibility = View.GONE
                            expandTextView.text = CLOSE
                        } else {
                            expandTextView.visibility = View.VISIBLE
                            expandTextView.text = OPEN
                            contentTextViewLP =
                                contentTextView.layoutParams as LinearLayout.LayoutParams
                            //可显示的高度
                            contentTextViewLP.height = shortHeight

                            contentTextView.apply {
                                maxLines = shortLineCount
                                layoutParams = contentTextViewLP
                                ellipsize = TextUtils.TruncateAt.END
                            }
                        }
                        //如果已经测量出行数大于0则取消观察
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            }
        })*/
    }


    fun measureHeight(h: Int, l: Int) {

        longHeight = h
        longLineCount = l
        if (longHeight > 0) {
            //收缩起来的高度
            shortHeight = longHeight / longLineCount * shortLineCount
            //当文本内容的高度小于设置的最大显示行, 则不显示展开/收起按钮
            if (longLineCount <= shortLineCount) {
                expandTextView.visibility = View.GONE
                expandTextView.text = CLOSE
            } else {
                expandTextView.visibility = View.VISIBLE
                expandTextView.text = OPEN
                contentTextViewLP =
                    contentTextView.layoutParams as LinearLayout.LayoutParams
                //可显示的高度
                contentTextViewLP.height = shortHeight

                contentTextView.apply {
                    maxLines = shortLineCount
                    layoutParams = contentTextViewLP
                    ellipsize = TextUtils.TruncateAt.END
                }
            }
        }
    }

    private fun toggle() {
        val animator: ValueAnimator = if (isOpen) {
            //如果是展开的情况,设置maxLine使ellipsize生效
            contentTextView.maxLines = shortLineCount
            expandTextView.text = OPEN
            ValueAnimator.ofInt(longHeight, shortHeight)
        } else {
            //如果是收缩的情况,设置最大行数完全展开数据
            contentTextView.maxLines = longLineCount
            expandTextView.text = CLOSE
            ValueAnimator.ofInt(shortHeight, longHeight)
        }
        isOpen = !isOpen
        animator.duration = 150
        animator.start()

        animator.addUpdateListener {
            val v = it.animatedValue as Int
            contentTextViewLP.height = v
            contentTextView.layoutParams = contentTextViewLP
        }

        /*    animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    expandTextView.text = if (isOpen) CLOSE else OPEN
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })*/


    }


}