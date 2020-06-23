/*
package com.stxx.wyhvisitorandroid.view.asr;

import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import com.baidu.speech.asr.SpeechConstant;
import com.baidu.voicerecognition.android.ui.BaiduASRDialog;
import com.carlos.voiceline.mylibrary.VoiceLineView;
import com.orhanobut.logger.Logger;
import com.stxx.wyhvisitorandroid.R;

import java.util.ResourceBundle;


*/
/**
 * description:
 * Created by liNan on 2020/4/13 20:31
 *//*

public class CustomBaiduAsrDigitalDialog extends BaiduASRDialog {


    private ConstraintLayout cslTipView;

    private TextView tvResult;

    private ImageView ivAsrClose;
    private VoiceLineView voiceLineView;

    private CharSequence mErrorRes = "";
    private static final String KEY_TIPS_ERROR_NETWORK = "tips.error.network";
    private static final String KEY_TIPS_ERROR_SILENT = "tips.error.silent";
    private ResourceBundle mLableRes;
    private static final String KEY_TIPS_ERROR_NETWORK_UNUSABLE = "tips.error.network_unusable";
    private static final int ERROR_NETWORK_UNUSABLE = 0x90000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mContentRoot = View.inflate(this,
                getResources().getIdentifier("dialog_bd_asr", "layout", getPackageName()), null);

        if (mContentRoot != null) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(new View(this));
            ViewGroup.LayoutParams param = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addContentView(mContentRoot, param);
            cslTipView = mContentRoot.findViewById(R.id.cslTip);
            tvResult = mContentRoot.findViewById(R.id.tvAsrResult);
            voiceLineView = mContentRoot.findViewById(R.id.voicLine);
            ivAsrClose = mContentRoot.findViewById(R.id.ivAsrClose);
            ivAsrClose.setOnClickListener(view -> {
                myRecognizer.cancel();
            });
        }
        startRecognition();
    }

    @Override
    protected void onRecognitionStart() {
        Logger.i("onRecognitionStart");

    }

    @Override
    protected void onPrepared() {
        Logger.i("onPrepared");

    }

    @Override
    protected void onBeginningOfSpeech() {
        Logger.i("onBeginningOfSpeech");

    }

    @Override
    protected void onVolumeChanged(float volume) {
        voiceLineView.setVolume((int) volume);
    }

    @Override
    protected void onEndOfSpeech() {
        Logger.i("onEndOfSpeech");
        finish();
    }


    */
/**
     * 获取国际化字符串
     *
     * @param key
     * @return 资源不存在返回Null
     *//*

    protected String getString(String key) {
        String label = null;
        if (mLableRes != null) {
            try {
                label = mLableRes.getString(key);
            } catch (Exception e) {
            }
        }
        return label;
    }

    @Override
    protected void onFinish(int errorType, int errorCode) {
        if (errorType != 0) {
            switch (errorType) {
                case SpeechRecognizer.ERROR_NO_MATCH:
                    mErrorRes = "没有匹配的识别结果";
                    break;
                case SpeechRecognizer.ERROR_AUDIO:
                    mErrorRes = "启动录音失败";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    mErrorRes = getString(KEY_TIPS_ERROR_SILENT);
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    SpannableString spanString = new SpannableString("网络超时，再试一次");
                    URLSpan span = new URLSpan("#") {
                        @Override
                        public void onClick(View widget) {
                            startRecognition();
                        }
                    };
                    spanString.setSpan(span, 5, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mErrorRes = spanString;
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    if (errorCode == ERROR_NETWORK_UNUSABLE) {
                        mErrorRes = getString(KEY_TIPS_ERROR_NETWORK_UNUSABLE);
                    } else {
                        mErrorRes = getString(KEY_TIPS_ERROR_NETWORK);
                    }
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    mErrorRes = "客户端错误";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    mErrorRes = "权限不足，请检查设置";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    mErrorRes = "引擎忙";
                    break;
            }
            tvResult.setText(mErrorRes);

        }

    }

    @Override
    protected void onPartialResults(String[] results) {
        if (results != null && results.length > 0) {
            Logger.i(results[0]);
            showResult(results[0]);
        }
    }


    private void showResult(String result) {
        cslTipView.setVisibility(View.GONE);
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText(result);
    }
}
*/
