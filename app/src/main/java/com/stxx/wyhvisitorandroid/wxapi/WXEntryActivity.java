package com.stxx.wyhvisitorandroid.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.gavindon.mvvm_lib.base.MVVMBaseApplication;
import com.gavindon.mvvm_lib.utils.GsonUtil;
import com.gavindon.mvvm_lib.widgets.ToastUtil;
import com.google.gson.reflect.TypeToken;
import com.stxx.wyhvisitorandroid.bean.WXLoginResp;
import com.stxx.wyhvisitorandroid.bean.WxOpenIdInfo;
import com.stxx.wyhvisitorandroid.view.login.LoginActivity;
import com.stxx.wyhvisitorandroid.view.mine.UserInfoFragment;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

import static com.stxx.wyhvisitorandroid.ConstantsKt.wxSecret;
import static com.stxx.wyhvisitorandroid.ConstantsKt.wxappid;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private MyHandler handler;
    public static final String WXAUTHDATA = "wx_data";

    private static class MyHandler extends Handler {
        private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;

        public MyHandler(WXEntryActivity wxEntryActivity) {
            wxEntryActivityWeakReference = new WeakReference<>(wxEntryActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            int tag = msg.what;
            Bundle data = msg.getData();
            String result = data.getString("result");
            Type token = new TypeToken<WXLoginResp>() {
            }.getType();
            if (result != null) {
                WXLoginResp resp = GsonUtil.Companion.str2Obj(result, token);
                Intent intent = null;
                //get到activity可能为null导致异常
                Context act = wxEntryActivityWeakReference.get();
                if (act == null) {
                    act = MVVMBaseApplication.appContext;
                }
                //个人信息中绑定微信或者解绑微信
                if (tag == NetworkUtil.GET_INFO) {
                    intent = new Intent(act, UserInfoFragment.class);
                }
                if (tag == NetworkUtil.GET_TOKEN) {
                    intent = new Intent(act, LoginActivity.class);
                }
                intent.putExtra(WXAUTHDATA, resp);
                //Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag.
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                act.startActivity(intent);
                WxOpenIdInfo.INSTANCE.setWxLoginResp(resp);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, wxappid, false);
        handler = new MyHandler(this);
        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //singleTop模式启动复用栈顶的已有Activity,调用onNewIntent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        //0 同意授权 -4拒绝 -2取消
        if (resp.errCode == 0) {
            if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                SendAuth.Resp authResp = (SendAuth.Resp) resp;
                final String code = authResp.code;
                //个人中心绑定微信
                if (authResp.state.equals("wyh_wx_login_userinfo")) {
                    NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                                    "appid=%s&secret=%s&code=%s&grant_type=authorization_code", wxappid,
                            wxSecret, code), NetworkUtil.GET_INFO);
                } else {
                    NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                                    "appid=%s&secret=%s&code=%s&grant_type=authorization_code", wxappid,
                            wxSecret, code), NetworkUtil.GET_TOKEN);
                }

            }
        } else {
            ToastUtil toast = ToastUtil.Companion.getInstance();
            if (toast != null) {
                toast.show("登陆失败", 1);
            }
        }
        finish();
    }
}
