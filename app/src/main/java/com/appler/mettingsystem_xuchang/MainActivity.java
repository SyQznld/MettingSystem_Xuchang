package com.appler.mettingsystem_xuchang;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appler.mettingsystem_xuchang.layer.ShowTreePop;
import com.appler.mettingsystem_xuchang.metting.MeetingData;
import com.appler.mettingsystem_xuchang.metting.ShowMeetingPop;
import com.appler.mettingsystem_xuchang.utils.CommonUtil;
import com.zhy.m.permission.MPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.feeeei.circleseekbar.CircleSeekBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Button mBtnPattern;
    private WebView mMWebView;
    private LinearLayout mLlSide;
    private TextView mBtnSide;
    private TextView mBtnCeju;
    private TextView mBtnEnd;
    private TextView mTextview;
    private TextView mBtnLocation;
    private TextView mBtnShuju;
    private TextView mBtnBack;
    private ImageView mIvDingwei;
    private ImageButton mIbClear;
    private TextView mTvPercent;
    private ImageButton ib_nowLocation;
    private ImageButton mIbSetMapView;
    private ImageButton mBtnJia;
    private ImageButton mBtnJian;
    private CircleSeekBar myseekbar;


    private List<String> datasName;
    private LocationManager lm;
    private double longitude, latitude;
    private static final int REQUECT_CODE_SDCARD = 0;


    private ShowTreePop showTreePop;  //图层树弹框
    private List<MeetingData> allFileNamesByRoot;

    //会议
    private TextView tv_mettingList;
    private ShowMeetingPop mettingPopup;//会议列表弹框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        setPermission();

        initView();

        setViewOnClick();

        //读取会议列表数据并且初始化会议弹框显示
        readMeetingList();

        showTreePop = new ShowTreePop(MainActivity.this, mMWebView);   //图层树

        myseekbar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i) {
                mMWebView.loadUrl("javascript:layerOpacity('" + i + "')");
                mTvPercent.setText(i + "%");
            }
        });
        myseekbar.setCurProcess(0);
        mBtnPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTreePop.showpop(mBtnPattern);
            }
        });


        IntentFilter filter = new IntentFilter();
        filter.addAction("2222");
        MainRecever recever = new MainRecever();
        registerReceiver(recever, filter);

        initWebView();


        //设置定位
        initGpsSetting();

    }

    private void readMeetingList() {
        String storagePath = CommonUtil.getStoragePath(MainActivity.this, true);
        Log.i(TAG, "onClick: " + storagePath);
        String mettingPath = null;
        if (null == storagePath) {
            mettingPath = CommonUtil.getSDPath() + File.separator + CommonUtil.HUIYI_PATH_NAME;
        } else {
            mettingPath = storagePath + File.separator + CommonUtil.HUIYI_PATH_NAME;
        }
        if (new File(mettingPath).exists()) {
            allFileNamesByRoot = CommonUtil.getAllHuiYiList(mettingPath);
            if (allFileNamesByRoot.size() > 0) {
                mettingPopup = new ShowMeetingPop(MainActivity.this, mMWebView, allFileNamesByRoot);   //会议列表弹框显示

            } else {
                Toast.makeText(this, "暂无会议记录", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "暂无会议记录", Toast.LENGTH_SHORT).show();
        }
    }

    private void initWebView() {
        mMWebView.addJavascriptInterface(new JavaScriptinterface(),
                "android");
        mMWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;//
            }
        });


        String sdPath = CommonUtil.getStoragePath(this, true);
        WebSettings webSettings = mMWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false); //隐藏webview缩放按钮
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        mMWebView.loadUrl("file:///android_asset/map.html?SDPath=" + sdPath);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mettingList:  //会议支持
                if (allFileNamesByRoot.size() > 0) {
                    mettingPopup.showMeettingList(tv_mettingList);
                } else {
                    Toast.makeText(this, "暂无会议记录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_shuju:
                mIvDingwei.setVisibility(View.VISIBLE);
                mBtnShuju.setVisibility(View.INVISIBLE);
                mBtnBack.setVisibility(View.VISIBLE);
                mLlSide.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_back:
                mMWebView.loadUrl("javascript:m_b()");
                mIvDingwei.setVisibility(View.INVISIBLE);
                mIbClear.setVisibility(View.INVISIBLE);
                mBtnShuju.setVisibility(View.VISIBLE);
                mBtnBack.setVisibility(View.INVISIBLE);
                mLlSide.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_jia:
                mMWebView.loadUrl("javascript:fangda()");
                break;
            case R.id.btn_jian:
                mMWebView.loadUrl("javascript:suoxiao()");
                break;
            case R.id.btn_side:
                mMWebView.loadUrl("javascript:m_a()");
                mBtnLocation.setVisibility(View.VISIBLE);
                mTextview.setVisibility(View.VISIBLE);
                mIbClear.setVisibility(View.VISIBLE);
                mBtnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMWebView.loadUrl("javascript:m_get()");
                    }
                });
                break;
            case R.id.btn_ceju:
                mMWebView.loadUrl("javascript:m_d()");
                mBtnLocation.setVisibility(View.VISIBLE);
                mTextview.setVisibility(View.VISIBLE);
                mIbClear.setVisibility(View.VISIBLE);
                mBtnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMWebView.loadUrl("javascript:m_get()");
                    }
                });
                break;
            case R.id.btn_end:
                mMWebView.loadUrl("javascript:m_e()");
                mBtnLocation.setVisibility(View.GONE);
                mTextview.setVisibility(View.GONE);
                break;
            case R.id.ib_setMapView:
                mMWebView.loadUrl("javascript:setMapCenter()");
                break;
            case R.id.ib_clear:
                mMWebView.loadUrl("javascript:m_c()");
                mBtnLocation.setVisibility(View.GONE);
                mTextview.setVisibility(View.GONE);
                break;
            case R.id.ib_nowLocation:
                Log.i(TAG, "onClick longitude: " + longitude);
                Log.i(TAG, "onClick latitude: " + latitude);
                if (!"4.9E-324".equals(String.valueOf(latitude)) && !"4.9E-324".equals(String.valueOf(longitude))
                        && 0.0 != longitude && 0.0 != latitude) {
//                    mMWebView.loadUrl("javascript:clickDingwei('" + longitude + "','" + latitude + "')");
                    mMWebView.loadUrl("javascript:WGS84Tomap('" + longitude + "','" + latitude + "')");
                } else {
                    Toast.makeText(this, "请位于宽阔地带，并检查位置信息是否打开", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

        }
    }


    public class JavaScriptinterface {

        @JavascriptInterface
        public void skip(String title, String context) {
            Log.i(TAG, "skip: " + title + "---->" + context);
            String[] datas = title.split("=");
            datasName = new ArrayList<>();
            for (int i = 0; i < datas.length; i++) {
                if (i == 0) {
                    continue;
                } else {
                    datasName.add(datas[i]);
                }

            }
            String s = datasName.get(datasName.size() - 1);
//            showPropertyPop.params(datasName, s, context);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    showPropertyPop.showpupwindow(mBtnPattern);
//                }
//            });
        }


        @JavascriptInterface
        public void Info(String title, String context, String contextName) {
            Log.i(TAG, "ZZZ: " + title + context + contextName);
            String[] datas = title.split("=");
            List<String> Title = new ArrayList<>();
            for (int i = 0; i < datas.length; i++) {
                if (i == 0) {
                    continue;
                } else {
                    Title.add(datas[i]);
                }

            }
//            showPropertyPop.params(Title, contextName, context);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    showPropertyPop.showpupwindow(mBtnPattern);
//                }
//            });

        }


    }

    private class MainRecever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("2222")) {
                String contextName = intent.getStringExtra("contextName");
                mMWebView.loadUrl("javascript:mapClick2('" + contextName + "')");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * 动态申请权限
     */
    private boolean setPermission() {
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD,
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.MOUNT_UNMOUNT_FILESYSTEMS",
                "android.permission.READ_PHONE_STATE",
                "android.permission.VIBRATE",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.INTERNET",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.BAIDU_LOCATION_SERVICE",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_GPS",
                "android.permission.READ_LOGS",
                "android.permission.CAMERA",
                "android.permission.WRITE_SETTINGS");
        return true;
    }


    /**
     * 定位基本设置
     */
    private boolean initGpsSetting() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isGpsAble(lm)) {
            Toast.makeText(MainActivity.this, "请打开GPS~", Toast.LENGTH_SHORT).show();
            openGPS2();
        }
        //从GPS获取最近的定位信息
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        Location lc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateShow(lc);
        //设置间隔两秒获得一次GPS定位信息
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 当GPS定位信息发生改变时，更新定位
                updateShow(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                // 当GPS LocationProvider可用时，更新定位
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                updateShow(lm.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {
                updateShow(null);
            }
        });
        return false;
    }


    //定义一个更新显示的方法
    private void updateShow(Location location) {
        Log.i(TAG, "updateShow location: " + location);
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            if (longitude != 0.0 && latitude != 0.0) {
                mMWebView.loadUrl("javascript:nowLocationView()");
//                tv_gps.setText(sb.toString());
            }
        } else {
            longitude = 0.0;
            latitude = 0.0;
        }
    }

    //gps是否可用
    private boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ? true : false;
    }


    //打开设置页面让用户自己设置
    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }


    private void setViewOnClick() {
        mBtnShuju.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnJia.setOnClickListener(this);
        mBtnJian.setOnClickListener(this);
        mBtnSide.setOnClickListener(this);
        mBtnCeju.setOnClickListener(this);
        mBtnEnd.setOnClickListener(this);
        mIbSetMapView.setOnClickListener(this);
        ib_nowLocation.setOnClickListener(this);
        mBtnLocation.setOnClickListener(this);
        mIbClear.setOnClickListener(this);
        tv_mettingList.setOnClickListener(this);

    }

    private void initView() {
        mBtnPattern = findViewById(R.id.btn_pattern);
        mMWebView = findViewById(R.id.mWebView);
        mLlSide = findViewById(R.id.ll_side);
        mBtnSide = findViewById(R.id.btn_side);
        mBtnCeju = findViewById(R.id.btn_ceju);
        mBtnEnd = findViewById(R.id.btn_end);
        mTextview = findViewById(R.id.textview);
        mBtnLocation = findViewById(R.id.btn_location);
        mBtnShuju = findViewById(R.id.btn_shuju);
        mBtnBack = findViewById(R.id.btn_back);
        mBtnJian = findViewById(R.id.btn_jian);
        mBtnJia = findViewById(R.id.btn_jia);
        mIvDingwei = findViewById(R.id.iv_dingwei);
        mIbClear = findViewById(R.id.ib_clear);
        mTvPercent = findViewById(R.id.tv_percent);
        ib_nowLocation = findViewById(R.id.ib_nowLocation);
        mIbSetMapView = findViewById(R.id.ib_setMapView);
        myseekbar = findViewById(R.id.myseekbar);
        tv_mettingList = findViewById(R.id.tv_mettingList);


    }


    //接收消息
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(String data) {
        Log.i(TAG, "onEventMainThread: " + data);
        if ("removePop".equals(data)) {
            if (mettingPopup != null) {
                mettingPopup.dismiss();
            }
            mettingPopup = new ShowMeetingPop(MainActivity.this, mMWebView, allFileNamesByRoot);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mettingPopup != null) {
            mettingPopup.dismiss();
        }

        if (showTreePop != null) {
            showTreePop.dismiss();
        }
    }
}
