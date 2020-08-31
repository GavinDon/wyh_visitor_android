package com.stxx.wyhvisitorandroid.view.helpers

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.gavindon.mvvm_lib.utils.rxRequestPermission
import com.stxx.wyhvisitorandroid.fileProviderAuth
import com.tencent.smtt.sdk.ValueCallback
import java.io.File

/**
 * description:webView拍照辅助类
 * Created by liNan on  2020/8/31 14:47
 */
object WebViewCameraHelper {

    private lateinit var act: Activity
    private var faceImage: File? = null
    var mUploadCallbackAboveL: ValueCallback<Array<Uri>>? = null
    private var fileUri: Uri? = null
    const val TYPE_CAMERA = 404


    fun showOptions(act: Activity) {
        this.act = act
        (act as FragmentActivity).rxRequestPermission(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onGrantedAction = {
                openCamera()
            }, onDeniedAction = {
                cancelChoose()
            }
        )
    }

    /**
     * 调用相机
     */
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 调用android的相机
        faceImage = createImageFile(act)
        //如果文件未创建
        if (faceImage == null) return
        // 创建一个文件保存图片
        fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(act, act.fileProviderAuth, faceImage!!)
        } else {
            Uri.fromFile(faceImage)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.putExtra("autofocus", true)
        act.startActivityForResult(intent, TYPE_CAMERA)
    }

    private fun createImageFile(act: Activity): File? {
        //创建打卡时的临时拍照‘文件对象’
        val faceImage = File(act.externalCacheDir, "budao_face.jpg")
        try {
            if (faceImage.exists()) {
                faceImage.delete()
            } else {
                faceImage.createNewFile()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return faceImage
    }

    /**
     * startActivityForResult之后要做的处理
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (faceImage != null) {
            try {
                val bitmap = PhotoUtils.getBitmapFormUri(act, fileUri)
                if (bitmap != null) PhotoUtils.compressImage(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (null == mUploadCallbackAboveL) return
        if (resultCode != RESULT_OK) {
            cancelChoose()
        } else {
            val uris = arrayOf(fileUri!!)
            mUploadCallbackAboveL?.onReceiveValue(uris)
            mUploadCallbackAboveL = null
        }
    }

    fun cancelChoose() {
        mUploadCallbackAboveL?.onReceiveValue(null)
        mUploadCallbackAboveL = null
    }

}