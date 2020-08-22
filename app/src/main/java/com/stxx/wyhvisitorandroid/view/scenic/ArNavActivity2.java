package com.stxx.wyhvisitorandroid.view.scenic;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.quyuanfactory.artmap.ArtMap;
import com.quyuanfactory.artmap.ArtMapARView;
import com.quyuanfactory.artmap.ArtMapView;
import com.quyuanfactory.artmap.ArtMapMark;
import com.quyuanfactory.artmap.ArtMapPoi;
import com.stxx.wyhvisitorandroid.R;

import java.util.ArrayList;
import java.util.List;

public class ArNavActivity2 extends AppCompatActivity {
    private static final String TAG = ArNavActivity2.class.getSimpleName();

    private Context mContext;
    private ArtMapView mMapView;
    private ArtMapARView mARView;
    private ImageButton butLocation;
    private boolean hasLocation = false;

    private LinearLayout mapCtrl;
    private LinearLayout botCtrl;

    //  private RelativeLayout  navCtrl;

    private int MGAR = 0;
    private int MGBOT = 0;
    private ImageButton butHome;
    private AlertDialog dlgTip;

    private Drawable[] mIconWeiZhi = {null, null};

    private boolean hasCamera = false;  //Camera Permission
    private LinearLayout butNavClose = null;
    private LinearLayout butNavMode = null;
    private RelativeLayout relNavto = null;

    private TextView txtNavtoMode = null;
    private int routeLookMode = 0; //0 -- overlook, 1 -- detail look

    private TextView txtNavtoTitle = null;

    private ArtMapMark startMark = null;
    private ArtMapMark endMark = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        ArtMap.SetCallBack(new ArtMap.CallBack() {
            @Override
            public void mapLoaded(boolean ret) {
                //Search Route
                Intent intent = getIntent();
                startMark = null;
                endMark = null;
                if (intent.hasExtra("start")) {
                    startMark = (ArtMapMark) intent.getSerializableExtra("start");
                }
                endMark = (ArtMapMark) intent.getSerializableExtra("stop");

                //open location
                butLocation.performClick();
            }

            @Override
            public void Routed(boolean ret) {
                if (!ret) {
                    Toast.makeText(getApplicationContext(), "抱歉，未找到路线", Toast.LENGTH_SHORT).show();
                    return;
                }
                EnterNav();
            }

            @Override
            public void Navigated(float relDistance, float relTime, String tip) {
                int dk = (int) Math.floor(relDistance * 0.001);
                int dm = (int) Math.floor(relDistance - dk * 1000.0);
                int th = (int) Math.floor(relTime / 3600.0);
                int tm = (int) Math.floor((relTime - th * 3600.0) / 60.0);
                int ts = (int) Math.floor(relTime - th * 3600.0 - tm * 60.0);
                String str = "全程剩余";
                if (dk > 0) {
                    if (dm > 0) {
                        str += "" + String.format("%.3f", dk + dm * 0.001f) + "公里";
                    } else {
                        str += "" + dk + "公里";
                    }
                } else if (dm > 0) {
                    str += "" + dm + "米";
                }

                str += " ";
                if (th > 0) {
                    str += "" + th + "小时";
                }
                if (tm > 0) {
                    str += "" + tm + "分";
                }
                if (ts > 0) {
                    str += "" + ts + "秒";
                }
                Log.d(TAG, "Navigated: " + " " + str + " tts: " + tip);

                txtNavtoTitle.setText(str);
            }

            @Override
            public void arLoaded(boolean ret) {
                Log.d(TAG, "arLoaded: " + ret);
                //概览路线
                routeLookMode = 0;
                ArtMap.OverlookRoute();
            }

            @Override
            public void arClicked(String name) {
                Log.d(TAG, "arClicked: " + name);
            }
        });

        init();
    }

    private void init() {
        setContentView(R.layout.activity_ar_nav);

        mIconWeiZhi[0] = getResources().getDrawable(R.drawable.weizhi);
        mIconWeiZhi[1] = getResources().getDrawable(R.drawable.weizhi_on);

        mapCtrl = findViewById(R.id.mapCtrl);
        botCtrl = findViewById(R.id.botCtrl);

        butHome = findViewById(R.id.butHome);

        mMapView = findViewById(R.id.artmapview);
        butLocation = findViewById(R.id.butWeiZi);

        mARView = findViewById(R.id.artmaparview);
        butNavClose = findViewById(R.id.lineNavtoClose);
        butNavMode = findViewById(R.id.lineNavtoMode);
        relNavto = findViewById(R.id.relNavto);

        txtNavtoMode = findViewById(R.id.txtNavtoMode);

        txtNavtoTitle = findViewById(R.id.txtNavtoTitle);

        butLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasLocation) {
                    doCheckPermission(new CallBackPermission() {
                        @Override
                        public void onResult(boolean ret) {
                            if (ret) {
                                hasLocation = true;
                                ArtMap.EnableLocation(true);
                            }
                            boolean _on = ArtMap.isLocated();
                            butLocation.setImageDrawable(_on ? mIconWeiZhi[1] : mIconWeiZhi[0]);
                            doEnter();
                        }
                    });
                    return;
                }
                boolean on = ArtMap.isLocated();
                ArtMap.EnableLocation(true);
                if (!on) {
                    boolean _on = ArtMap.isLocated();
                    butLocation.setImageDrawable(_on ? mIconWeiZhi[1] : mIconWeiZhi[0]);
                }
                doEnter();
            }//end click
        });

        butLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean on = ArtMap.isLocated();
                if (on) {//do turn off location
                    ArtMap.EnableLocation(false);
                    Toast.makeText(getApplicationContext(), "定位已关闭！", Toast.LENGTH_SHORT).show();
                    boolean _on = ArtMap.isLocated();
                    butLocation.setImageDrawable(_on ? mIconWeiZhi[1] : mIconWeiZhi[0]);
                    return true;
                }
                return false;
            }
        });

        butHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtMap.GoHome();
            }
        });

        butNavClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgTip = commonDialog("ArtMap", "              退出导航", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DialogInterface.BUTTON_POSITIVE == which) {
                            doEixt();
                            finish();
                        }
                        dlgTip.dismiss();
                        dlgTip = null;
                    }
                });
                dlgTip.show();
            }
        });

        butNavMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ArtMap.isRouted()) {
                    return;
                }
                routeLookMode = 1 - routeLookMode;
                if (0 == routeLookMode) {
                    ArtMap.OverlookRoute();
                    txtNavtoMode.setText("继续当前导航");
                } else {
                    ArtMap.EnableLocation(true);
                    txtNavtoMode.setText("查看全览");
                }
            }
        });

        resetHeight();
    }

    private void doEnter() {
        if (!ArtMap.isLocated()) {
            return;
        }

        if (!hasCamera) {
            hasCamera = checkAndGotPermissionCamera(new CallBackPermission() {
                @Override
                public void onResult(boolean ret) {
                    hasCamera = ret;
                    if (hasCamera) {
                        doEnter();
                    }
                }
            });
            if (!hasCamera) {
                return;
            }
        }

        ArtMap.SearchRoute(startMark, endMark);
    }

    private void doEixt() {
        ExitNav();
        ArtMap.CancelRoute();
        ArtMap.EnableLocation(false);
        ArtMap.Exit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doEixt();
    }

    private int getContextH(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    private AlertDialog commonDialog(String title, String msg, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton("取消", onClickListener);
        builder.setPositiveButton("确定", onClickListener);
        return builder.create();
    }

    private void resetHeight() {
        MGBOT = ((RelativeLayout.LayoutParams) botCtrl.getLayoutParams()).height;
        int mapH = (int) (Math.abs(MGBOT) * 1.8);
        int H = getContextH(getApplicationContext());
        MGAR = H - mapH;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mARView.getLayoutParams();
        layoutParams.height = MGAR;
        mARView.setLayoutParams(layoutParams);
    }

    private void EnterNav() {
        if (!hasCamera) {
            hasCamera = checkAndGotPermissionCamera(new CallBackPermission() {
                @Override
                public void onResult(boolean ret) {
                    hasCamera = ret;
                    if (hasCamera) {
                        EnterNav();
                    }
                }
            });
            if (!hasCamera) {
                return;
            }
        }

        //Open AR
        ArtMap.StartAR();
    }

    private void ExitNav() {
        //Gone AR
        ArtMap.StopAR();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        if (null != mMapView) mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        if (null != mMapView) mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        doEixt();

        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        if (null != mMapView) mMapView.onDestroy();

        mMapView = null;
        mARView = null;
    }

    //////////////////////////////////////////////////////////////////////////
    //NORMAL
    private static interface CallBackPermission {
        public void onResult(boolean ret);
    }

    //CHECK LOCATION PERMISSION
    private static final int LOCATION_PERMISSION_REQ_CODE = 2;
    private CallBackPermission cbPermission;

    private boolean checkPermission() {
        ArrayList<String> permissionsList = new ArrayList<>();
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//7.0
            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                }
            }

            return permissionsList.isEmpty();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), perm)) {
                    permissionsList.add(perm);
                }
            }
            return permissionsList.isEmpty();
        }

        boolean ret = true;
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //Get a location provider, GPS, or NetWork
        List<String> providers = locationManager.getProviders(true);
        Log.d(TAG, "Location: " + providers.toString());
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {// NetWork Location
            Log.d(TAG, "Got NetWork Location");
            ret = true;
        }
        if (providers.contains(LocationManager.GPS_PROVIDER)) {//GPS Location
            Log.d(TAG, "Got GPS Location");
            ret = true;
        }
        Log.d(TAG, "No locator is available, them check");
        return ret;
    }

    private void doCheckPermission(CallBackPermission cb) {
        cbPermission = cb;
        boolean ret = checkPermission();
        if (ret) {
            if (null != cbPermission) {
                cbPermission.onResult(true);
            }
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 7.0 以上引导
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivityForResult(intent, LOCATION_PERMISSION_REQ_CODE);
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQ_CODE);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 6.0 动态申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQ_CODE);
        }
    }

    //CHECK CAMERA
    private static final int CAMERA_PERMISSION_REQ_CODE = 3;
    private CallBackPermission cbPermissionCamera = null;

    private boolean checkAndGotPermissionCamera(CallBackPermission cb) {
        cbPermissionCamera = cb;

        ArrayList<String> permissionsList = new ArrayList<>();
        String[] permissions = {
                Manifest.permission.CAMERA
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//7.0
            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                }
            }
            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), CAMERA_PERMISSION_REQ_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//6.0
            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getApplicationContext(), perm)) {
                    permissionsList.add(perm);
                }
            }
            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                ActivityCompat.requestPermissions(this, permissionsList.toArray(strings), CAMERA_PERMISSION_REQ_CODE);
            }
        }

        return permissionsList.isEmpty();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult->requestCode: " + requestCode + " ret: " + grantResults[0]);

        if (LOCATION_PERMISSION_REQ_CODE == requestCode) {
            if (null != cbPermission) {
                cbPermission.onResult(grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        } else if (CAMERA_PERMISSION_REQ_CODE == requestCode) {
            if (null != cbPermissionCamera) {
                cbPermissionCamera.onResult(grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

}
