/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.stxx.wyhvisitorandroid.view.ar;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.mapapi.UIMsg;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener;
import com.baidu.mapapi.walknavi.adapter.IWTTSPlayer;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;
import com.baidu.mapapi.walknavi.model.WalkNaviDisplayOption;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;
import com.baidu.platform.comapi.walknavi.widget.ArCameraView;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.orhanobut.logger.Logger;
import com.stxx.wyhvisitorandroid.view.asr.Auth;
import com.stxx.wyhvisitorandroid.view.navigation.WalkNavigationFragment;

/**
 * @author 53089
 */
public class WNaviGuideActivity extends Activity {

    private final static String TAG = WNaviGuideActivity.class.getSimpleName();

    private SpeechSynthesizer mSpeechSynthesizer;

    private WalkNavigateHelper mNaviHelper = WalkNavUtil.INSTANCE.getWalkNavigateHelper();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

    private void initSpeech() {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        Auth auth = Auth.getInstance(this);
        mSpeechSynthesizer.setApiKey(auth.getAppKey(), auth.getSecretKey());
        mSpeechSynthesizer.setAppId(auth.getAppId());
        mSpeechSynthesizer.setContext(this);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        mSpeechSynthesizer.initTts(TtsMode.ONLINE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            WalkNaviDisplayOption option = new WalkNaviDisplayOption();
            option.runInFragment(true);
            Bitmap bitmap = option.getImageToNormal();
            int layout = option.getBottomSettingLayout();
            mNaviHelper.setWalkNaviDisplayOption(option);
            View view = mNaviHelper.onCreate(WNaviGuideActivity.this);
            if (view != null) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                lp.height = 1200;
                view.setLayoutParams(lp);
                FrameLayout frl = new FrameLayout(this);
//                MapView bdMapView = new MapView(this);
                frl.addView(view);
                view.findViewById(layout).setVisibility(View.GONE);
//                frl.addView(bdMapView, ViewGroup.LayoutParams.MATCH_PARENT, 700);
                setContentView(frl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initSpeech();
        mNaviHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
            @Override
            public void onWalkNaviModeChange(int mode, WalkNaviModeSwitchListener listener) {
                Log.d(TAG, "onWalkNaviModeChange : " + mode);
                mNaviHelper.switchWalkNaviMode(WNaviGuideActivity.this, mode, listener);
            }

            @Override
            public void onNaviExit() {
                Log.d(TAG, "onNaviExit");
            }
        });

        mNaviHelper.setTTsPlayer((s, b) -> {
            mSpeechSynthesizer.speak(s);
            return 0;
        });

        boolean startResult = mNaviHelper.startWalkNavi(WNaviGuideActivity.this);
        Log.e(TAG, "startWalkNavi result : " + startResult);

        mNaviHelper.setRouteGuidanceListener(this, new IWRouteGuidanceListener() {
            @Override
            public void onRouteGuideIconUpdate(Drawable icon) {

            }

            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {
                Log.d(TAG, "onRouteGuideKind: " + routeGuideKind);
            }

            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {
                Log.d(TAG, "onRoadGuideTextUpdate   charSequence=: " + charSequence + "   charSequence1 = : " +
                        charSequence1);

            }

            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {
                Log.d(TAG, "onRemainDistanceUpdate: charSequence = :" + charSequence);

            }

            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {
                Log.d(TAG, "onRemainTimeUpdate: charSequence = :" + charSequence);

            }

            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {
                Log.d(TAG, "onGpsStatusChange: charSequence = :" + charSequence);

            }

            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {
                Log.d(TAG, "onRouteFarAway: charSequence = :" + charSequence);

            }

            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {
                Log.d(TAG, "onRoutePlanYawing: charSequence = :" + charSequence);

            }

            @Override
            public void onReRouteComplete() {

            }

            @Override
            public void onArriveDest() {

            }

            @Override
            public void onIndoorEnd(Message msg) {

            }

            @Override
            public void onFinalEnd(Message msg) {

            }

            @Override
            public void onVibrate() {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ArCameraView.WALK_AR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(WNaviGuideActivity.this, "没有相机权限,请打开后重试", Toast.LENGTH_SHORT).show();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaviHelper.startCameraAndSetMapView(WNaviGuideActivity.this);
            }
        }
    }
}
