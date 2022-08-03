import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gavindon.mvvm_lib.utils.phoneWidth
import com.gavindon.mvvm_lib.utils.rxRequestPermission
import com.stxx.wyhvisitorandroid.R
import kotlinx.android.synthetic.main.dialog_permission.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.wrapContent


/**
 * description:申请权限说明
 * Created by liNan on  2020/8/28 09:15
 */
class PermissionTipDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.halfDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(phoneWidth - dip(60), wrapContent)
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
        return layoutInflater.inflate(R.layout.dialog_permission, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnConfirm.setOnClickListener {
            this.dismiss()
            this.rxRequestPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.RECORD_AUDIO
            ) {}
        }

        btnNoAgree.setOnClickListener {
            this.dismiss()
        }

    }


}