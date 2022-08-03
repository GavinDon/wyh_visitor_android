import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gavindon.mvvm_lib.utils.SpUtils
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.stxx.wyhvisitorandroid.*
import com.stxx.wyhvisitorandroid.view.splash.SplashActivity
import com.stxx.wyhvisitorandroid.view.webview.WebViewActivity
import kotlinx.android.synthetic.main.dialog_protocol.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.wrapContent


/**
 * description:隐私政策
 * Created by liNan on  2020/8/28 09:15
 */
class SmartTipDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.halfDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(phoneWidth - dip(80), wrapContent)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setOnKeyListener { dialog, keyCode, event ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.dialog_protocol, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val li = """
     <p> 在您使用畅游温榆APP前，请您认真阅读并
        了解 <a href=$FWXY>《软件服务协议》</a>和 <a href=$YSZC>《用户隐私政策》</a>。
        点击“同意”即表示您已阅读并同意全部条款。</p>
""".trimIndent()

        /*   val a = """
               <font color="#222222">在您使用交大云会议前，请您认真阅读并
           了解 <font color="#FF9800"><a href= "https://baidu.com/agreement.html">《用户协议》</a ></font>和<font color="#FF9800"><a href="https://baidu.com/privacy.html">《隐私政策》</a ></font>的全部内容，同意并接受全部条款后开始使用我们的产品和服务。我们会严格按照政策内容使用和保护您的个人信息，感谢您的信任。<br/><br/>若您同意以上用户协议和隐私协议保护政策，请点击“同意”并开始使用我们的产品和服务。</font>
           """.trimIndent()*/
        tvProtocContent.text = getClickableHtml(li)
        tvProtocContent.movementMethod = LinkMovementMethod.getInstance()
        btnConfirm.setOnClickListener {
            startActivity<SplashActivity>()
            this.activity?.finish()
            this.dismiss()
            SpUtils.put(AGREE_PROTOCOL, true)
            this.activity?.finish()
        }
        btnNoAgree.setOnClickListener {
            this.dismiss()
            SpUtils.clearName(TOKEN)
            SpUtils.clearName(LOGIN_NAME_SP)
            SpUtils.clearName(PASSWORD_SP)
            this.activity?.finish()
        }
    }

    private fun getClickableHtml(html: String?): CharSequence {
        val spannedHtml: Spanned = Html.fromHtml(html)
        val clickableHtmlBuilder = SpannableStringBuilder(spannedHtml)
        val spans = clickableHtmlBuilder.getSpans(0, spannedHtml.length, URLSpan::class.java)
        for (value in spans) {
            setLinkClickable(clickableHtmlBuilder, value)
        }

        return clickableHtmlBuilder
    }


    /**
     * 捕获<a>标签点击事件
     */
    private fun setLinkClickable(clickableHtmlBuilder: SpannableStringBuilder, urlSpan: URLSpan?) {
        val start = clickableHtmlBuilder.getSpanStart(urlSpan)
        val end = clickableHtmlBuilder.getSpanEnd(urlSpan)
        val flags = clickableHtmlBuilder.getSpanEnd(urlSpan)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val url = urlSpan?.url
                //能够点击的关键在于removeSpan(urlSpan)
                if (url?.contains("softwareService.html") == true) {
                    startActivity<WebViewActivity>(Pair(WEB_VIEW_URL, FWXY))
                } else if (url?.contains("userPrivacy.html") == true) {
                    startActivity<WebViewActivity>(Pair(WEB_VIEW_URL, YSZC))
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                //设置颜色
                ds.color = Color.parseColor("#FF9800")
                //设置是否要下划线
                ds.isUnderlineText = false
            }

        }
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags)
        // its too important
        clickableHtmlBuilder.removeSpan(urlSpan)

    }


}