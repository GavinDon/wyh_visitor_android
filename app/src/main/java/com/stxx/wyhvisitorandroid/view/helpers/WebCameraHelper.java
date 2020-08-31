package com.stxx.wyhvisitorandroid.view.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.tencent.smtt.sdk.ValueCallback;

import java.io.File;

/**
 * description:
 * Created by liNan on  2020/8/30 08:38
 */
public class WebCameraHelper {
    private static class SingletonHolder {
        static final WebCameraHelper INSTANCE = new WebCameraHelper();
    }

    public static WebCameraHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 图片选择回调
     */
    public ValueCallback<Uri[]> mUploadCallbackAboveL;

    private Uri fileUri;
    private static final int TYPE_REQUEST_PERMISSION = 3;
    public static final int TYPE_CAMERA = 23;
    private File faceImage;
    private Activity activity;

    /**
     * 包含拍照和相册选择
     */
    public void showOptions(final Activity act) {
        this.activity = act;
        if (ContextCompat.checkSelfPermission(act,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat
                    .requestPermissions(
                            act,
                            new String[]{Manifest.permission.CAMERA},
                            TYPE_REQUEST_PERMISSION);
        } else {
            toCamera(act);
        }
    }

    public void cameraCancel() {
        try {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        } catch (Exception e) {

        }
    }

    /**
     * 请求拍照
     */
    private void toCamera(Activity act) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android的相机
        // 创建一个文件保存图片
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(act, "com.stxx.wyhvisitorandroid.FileProvider", createImageFile(act));
        } else {
            fileUri = Uri.fromFile(createImageFile(act));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        intent.putExtra("autofocus", true);
        act.startActivityForResult(intent, TYPE_CAMERA);

    }


    private File createImageFile(Activity act) {
        //创建打卡时的临时拍照‘文件对象’
        faceImage = new File(act.getExternalCacheDir(), "budao_face.jpg");
        try {
            if (faceImage.exists()) {
                faceImage.delete();
            } else {
                faceImage.createNewFile();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return faceImage;

    }

    /**
     * startActivityForResult之后要做的处理
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (faceImage != null) {
            try {
                Bitmap bitmap = PhotoUtils.getBitmapFormUri(activity, fileUri);
                if (bitmap != null) {
                    PhotoUtils.compressImage(bitmap);
                }
            } catch (Exception e) {

            }
        }
        if (requestCode == TYPE_CAMERA) {
            if (null == mUploadCallbackAboveL) return;
            if (resultCode != -1) {
                mUploadCallbackAboveL.onReceiveValue(null);
                mUploadCallbackAboveL = null;
            } else {
                Uri[] uris = new Uri[]{fileUri};
                mUploadCallbackAboveL.onReceiveValue(uris);
                mUploadCallbackAboveL = null;
            }

        }
    }

}
