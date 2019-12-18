package com.appler.mettingsystem_xuchang.metting;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.appler.mettingsystem_xuchang.R;
import com.appler.mettingsystem_xuchang.layer.ShowTreePop;
import com.appler.mettingsystem_xuchang.utils.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ShowMeetingPop extends PopupWindow implements View.OnClickListener {
    private static final String TAG = "SearchPopup";
    private PopupWindow popupWindow;
    private WebView mWebView;
    private Activity context;
    private View view;


    private ImageView iv_back;
    private TextView tv_hyList;
    private TextView tv_typeList;
    private TextView tv_zdk_list;
    private TextView tv_close;
    private ListView lv_show;

    private List<MeetingData> clickMeetingDataList;
    private MeetingData clickMeetingData;
    private String zhudikuaiPath;
    private String zdkType;
    private String fushuDiKuaiPath;
    public static boolean wztClick = false;
    public static boolean jbqkClick = false;


    public ShowMeetingPop(Activity context, WebView mWebView, List<MeetingData> meetingDataList) {
        this.context = context;
        this.mWebView = mWebView;


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.meeting_pop_layout, null);

        initView();

        clickMeetingDataList = meetingDataList;
        showHyList(meetingDataList);

    }

    public void showMeettingList(View parent) {

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = manager.getDefaultDisplay().getWidth();
        int heigh = manager.getDefaultDisplay().getHeight();

        popupWindow = new PopupWindow(view, width / 4, heigh / 2, false);
//        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.AnimationRightFade);

        popupWindow.showAtLocation(parent, Gravity.CENTER_VERTICAL | Gravity.LEFT, 20, 50);
    }

    private void showHyList(List<MeetingData> meetingDataList) {
        iv_back.setVisibility(View.GONE);
        tv_close.setVisibility(View.GONE);
        tv_typeList.setVisibility(View.INVISIBLE);
        tv_zdk_list.setVisibility(View.INVISIBLE);
        final MeetingListAdapter[] meetingListAdapter = {new MeetingListAdapter(context, meetingDataList)};
        lv_show.setAdapter(meetingListAdapter[0]);
        meetingListAdapter[0].setMettingItemClickListenerClick(new MeetingListAdapter.MettingItemClickListenerClick() {
            @Override
            public void mettingItemClick(int position, MeetingData meetingData) {
                clickMeetingData = meetingData;
                meetingListAdapter[0] = null;
                showTypeList();
            }
        });
    }

    private void showTypeList() {
        Set<String> sets = new HashSet<>();
        //读取某一次会议 主地块类型
        Log.i(TAG, "showTypeList clickMeetingData: " + clickMeetingData);
        String mettingPath = clickMeetingData.getMettingPath();
        if (new File(mettingPath).exists()) {
            List<MeetingData> allFileNamesByRoot = CommonUtil.getAllFileNamesByRoot(mettingPath);
            for (int i = 0; i < allFileNamesByRoot.size(); i++) {
                String mettingName = allFileNamesByRoot.get(i).getMettingName();
                if (!"".equals(mettingName) && mettingName.contains("附属地块")) {
                    fushuDiKuaiPath = allFileNamesByRoot.get(i).getMettingPath();
                }
                if (!"".equals(mettingName) && mettingName.contains("主地块")) {
                    iv_back.setVisibility(View.VISIBLE);
                    tv_typeList.setVisibility(View.VISIBLE);
                    tv_zdk_list.setVisibility(View.INVISIBLE);
                    tv_close.setVisibility(View.VISIBLE);
                    //读取主地块TXT，将所有类型加到set集合中
                    zhudikuaiPath = allFileNamesByRoot.get(i).getMettingPath();
                    String fileContent = CommonUtil.getFileContent(new File(zhudikuaiPath));
                    if (!"".equals(fileContent) && fileContent.contains("{")) {
                        Gson gson = new Gson();
                        ZhuDiKuaiData zhuDiKuaiData = gson.fromJson(fileContent, new TypeToken<ZhuDiKuaiData>() {
                        }.getType());

                        List<ZhuDiKuaiData.FeaturesBean> features = zhuDiKuaiData.getFeatures();
                        for (int j = 0; j < features.size(); j++) {
                            String type = features.get(j).getProperties().getTYPE();
                            sets.add(type);
                        }
                        final ZhuDiKuaiTypeAdapter[] diKuaiTypeAdapter = {new ZhuDiKuaiTypeAdapter(context, new ArrayList(sets))};
                        lv_show.setAdapter(diKuaiTypeAdapter[0]);
                        diKuaiTypeAdapter[0].setMettingItemClickListenerClick(new ZhuDiKuaiTypeAdapter.MettingItemClickListenerClick() {
                            @Override
                            public void mettingItemClick(int position, String type) {
                                diKuaiTypeAdapter[0] = null;
//                            showZhudikuaiList(type, zhudikuaiPath, fushuDiKuaiPath);
                                zdkType = type;
                                showZhudikuaiList();

                            }
                        });
                    }
                }
            }
        } else {
            Toast.makeText(context, "地块信息文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void showZhudikuaiList() {
        if (new File(zhudikuaiPath).exists()) {
            String fileContent = CommonUtil.getFileContent(new File(zhudikuaiPath));
            if (!"".equals(fileContent) && fileContent.contains("{")) {
                iv_back.setVisibility(View.VISIBLE);
                tv_typeList.setVisibility(View.VISIBLE);
                tv_zdk_list.setVisibility(View.VISIBLE);
                tv_close.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                ZhuDiKuaiData zhuDiKuaiData = gson.fromJson(fileContent, new TypeToken<ZhuDiKuaiData>() {
                }.getType());
                List<ZhuDiKuaiData.FeaturesBean> typeFeatures = new ArrayList<>();
                final List<ZhuDiKuaiData.FeaturesBean> features = zhuDiKuaiData.getFeatures();
                for (int i = 0; i < features.size(); i++) {
                    String type = features.get(i).getProperties().getTYPE();
                    if (type.equals(zdkType)) {
                        typeFeatures.add(features.get(i));
                    }
                }
                if (typeFeatures.size() > 0) {
                    final DiKuaiAdapter diKuaiAdapter = new DiKuaiAdapter(context, typeFeatures);
                    lv_show.setAdapter(diKuaiAdapter);
                    diKuaiAdapter.setDiKuaiItemClickListener(new DiKuaiAdapter.DiKuaiItemClickListener() {
                        @Override
                        public void dk_weizhituClick(int position, ZhuDiKuaiData.FeaturesBean featuresBean) {
                            diKuaiAdapter.setSelectedPosition(position);
                            diKuaiAdapter.notifyDataSetInvalidated();
                            //点击位置图，默认打开总规图，关闭影像
                            Log.i(TAG, "dk_weizhituClick: " + wztClick);
                            if (wztClick == false) {
                                ShowTreePop.cb_xc_yx.setChecked(false);
                                ShowTreePop.cb_xc_zgt.setChecked(true);
                                ShowTreePop.cb_xc_lmlw.setChecked(true);
                                ShowTreePop.rb_xc_zgt.setVisibility(View.VISIBLE);
                                ShowTreePop.rb_xc_lmlw.setVisibility(View.VISIBLE);
                                ShowTreePop.rb_xc_yx.setVisibility(View.GONE);
//                                先去掉之前的，在显示图层，不然在图层树上会去不掉
                                mWebView.loadUrl("javascript:loadLayerShp('" + CommonUtil.getStoragePath(context, true) + "/会议列表/许昌上会地块总" + "',false)");
                                mWebView.loadUrl("javascript:layer('" + "xc_zgt" + "','false')");
                                mWebView.loadUrl("javascript:layer('" + "xc_lmlw" + "','false')");

                                mWebView.loadUrl("javascript:layer('" + "xc_yx" + "','false')");
                                mWebView.loadUrl("javascript:layer('" + "xc_zgt" + "','true')");
                                mWebView.loadUrl("javascript:layer('" + "xc_lmlw" + "','true')");
//                                mWebView.loadUrl("javascript:loadLayerShp('" + CommonUtil.getStoragePath(context, true) + "/会议列表/许昌上会地块总" + "',true)");
                                wztClick = true;

                            } else if (wztClick == true) {
//                                wztClick = false;
                                ShowTreePop.cb_xc_yx.setChecked(false);
                                ShowTreePop.rb_xc_yx.setVisibility(View.GONE);
                                mWebView.loadUrl("javascript:layer('" + "xc_yx" + "','false')");
                            }

                            List<List<List<Double>>> coordinates = featuresBean.getGeometry().getCoordinates();
                            String dkName = featuresBean.getProperties().getNAME();
                            String dkLocation = featuresBean.getProperties().getZDWZ();
                            if ("null".equals(dkLocation) || "null".equals(dkLocation) || null == dkLocation) {
                                dkLocation = "";
                            }

                            //位置图显示范围 并且显示地块名称以及位置
                            mWebView.loadUrl("javascript:showDKLocation('" + coordinates.toString() + "','" + dkName + "','" + dkLocation + "')");
                            popupWindow.dismiss();
                        }

                        @Override
                        public void dk_xzyxClick(int position, ZhuDiKuaiData.FeaturesBean featuresBean) {
                            mWebView.loadUrl("javascript:clearFsdkPolygon()");
                            diKuaiAdapter.setSelectedPosition(position);
                            diKuaiAdapter.notifyDataSetInvalidated();
                            if (jbqkClick == false) {
                                //点击现状影像，定位到当前地块，注记显示当前地块编号，同时打开影像，关闭总规
                                ShowTreePop.cb_xc_zgt.setChecked(false);
                                ShowTreePop.cb_xc_yx.setChecked(true);
                                ShowTreePop.cb_xc_lmlw.setChecked(true);
                                ShowTreePop.rb_xc_yx.setVisibility(View.VISIBLE);
                                ShowTreePop.rb_xc_lmlw.setVisibility(View.VISIBLE);
                                ShowTreePop.rb_xc_zgt.setVisibility(View.GONE);

                                mWebView.loadUrl("javascript:loadLayerShp('" + CommonUtil.getStoragePath(context, true) + "/会议列表/许昌上会地块总" + "',false)");
                                //先去掉之前的，在显示图层，不然在图层树上会去不掉
                                mWebView.loadUrl("javascript:layer('" + "xc_yx" + "','false')");
                                mWebView.loadUrl("javascript:layer('" + "xc_lmlw" + "','false')");

                                mWebView.loadUrl("javascript:layer('" + "xc_zgt" + "','false')");
                                mWebView.loadUrl("javascript:layer('" + "xc_yx" + "','true')");
                                mWebView.loadUrl("javascript:layer('" + "xc_lmlw" + "','true')");
                                mWebView.loadUrl("javascript:loadLayerShp('" + CommonUtil.getStoragePath(context, true) + "/会议列表/许昌上会地块总" + "',true)");
                                jbqkClick = true;
                            } else if (jbqkClick == true) {
//                                jbqkClick = false;
                                ShowTreePop.cb_xc_zgt.setChecked(false);
                                ShowTreePop.rb_xc_zgt.setVisibility(View.GONE);
                                mWebView.loadUrl("javascript:layer('" + "xc_zgt" + "','false')");
                            }

                            List<List<List<Double>>> zdk_coordinates = featuresBean.getGeometry().getCoordinates();
                            String zdbh = featuresBean.getProperties().getZDBH();
                            String zdk_bh = zdbh + "套合图";
                            String zdkName = featuresBean.getProperties().getNAME();
                            String zdk_type = featuresBean.getProperties().getTYPE();
                            String zdk_zdwz = featuresBean.getProperties().getZDWZ();
                            String zdk_ydxz = featuresBean.getProperties().getYDXZ();
                            String zdk_jzdj = featuresBean.getProperties().getJZDJ();
                            String zdk_pgdj = featuresBean.getProperties().getPGDJ();
                            if ("null".equals(zdk_zdwz) || "null".equals(zdk_zdwz) || null == zdk_zdwz) {
                                zdk_zdwz = "";
                            }
                            if ("null".equals(zdk_ydxz) || "null".equals(zdk_ydxz) || null == zdk_ydxz) {
                                zdk_ydxz = "";
                            }
                            if ("null".equals(zdk_jzdj) || "null".equals(zdk_jzdj) || null == zdk_jzdj) {
                                zdk_jzdj = "";
                            }
                            if ("null".equals(zdk_pgdj) || "null".equals(zdk_pgdj) || null == zdk_pgdj) {
                                zdk_pgdj = "";
                            }
                            String zdkContent = "名称：" + zdkName + "," +
                                    "编号：" + zdbh + "," +
                                    "类型：" + zdk_type + "," +
                                    "位置：" + zdk_zdwz + "," +
                                    "用地性质：" + zdk_ydxz + "," +
                                    "基准地价：" + zdk_jzdj + "," +
                                    "评估地价：" + zdk_pgdj;

                            if (!"null".equals(fushuDiKuaiPath) && null != fushuDiKuaiPath && !"".equals(fushuDiKuaiPath)) {
                                String fileContent = CommonUtil.getFileContent(new File(fushuDiKuaiPath));
                                if (!"".equals(fileContent) && fileContent.contains("{")) {
                                    Gson gson = new Gson();
                                    FushuDikuaiData fushuDikuaiData = gson.fromJson(fileContent, new TypeToken<FushuDikuaiData>() {
                                    }.getType());
                                    //筛选出来当前点击的主地块关联的附属地块
                                    //根据BH字段判断是否是范围线，范围线只圈出来范围，否则圈出来范围并且显示注记
                                    List<FushuDikuaiData.FeaturesBean> featuresBeanList = fushuDikuaiData.getFeatures();
                                    for (int i = 0; i < featuresBeanList.size(); i++) {
                                        String name = featuresBeanList.get(i).getProperties().getNAME();
                                        List<List<List<Double>>> coordinates = featuresBeanList.get(i).getGeometry().getCoordinates();
                                        String dkbh = null;
                                        if (name.equals(zdkName)) {
                                            dkbh = featuresBeanList.get(i).getProperties().getDKBH();
                                            if ("null".equals(dkbh) || "null".equals(dkbh) || null == dkbh) {
                                                //范围线 紫色
                                                dkbh = "";
                                            }
                                            mWebView.loadUrl("javascript:showZDKAndFushuDK('" + coordinates.toString() + "','" + name + "','" + dkbh + "','" + "fsdk" + "')");
                                        }
                                    }
                                }
                            }
                            mWebView.loadUrl("javascript:showZDKAndFushuDK('" + zdk_coordinates + "','" + zdkContent + "','" + zdk_bh + "','" + "zdk" + "')");
                            popupWindow.dismiss();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "暂无地块信息", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_metpop_back:
                if (tv_typeList.getVisibility() == View.VISIBLE) {
                    showHyList();
                }
                if (tv_zdk_list.getVisibility() == View.VISIBLE) {
                    showZdkTypeList();
                }
                break;
            case R.id.tv_metpop_hyList:
                showHyList();
                break;
            case R.id.tv_metpop_typeList:
                showZdkTypeList();
                break;
            case R.id.tv_metpop_zdkList:
                break;
            case R.id.tv_metpop_close:
                EventBus.getDefault().postSticky("removePop");
                popupWindow.dismiss();
                mWebView.loadUrl("javascript:clearWztPolygon()");
                mWebView.loadUrl("javascript:clearFsdkPolygon()");
                //地图初始化
                ShowTreePop.cb_xc_zgt.setChecked(false);
                ShowTreePop.cb_xc_lmlw.setChecked(false);
                ShowTreePop.cb_xc_yx.setChecked(true);
                ShowTreePop.rb_xc_zgt.setVisibility(View.GONE);
                ShowTreePop.rb_xc_lmlw.setVisibility(View.GONE);
                ShowTreePop.rb_xc_yx.setVisibility(View.VISIBLE);
                wztClick = false;
                jbqkClick = false;

                mWebView.loadUrl("javascript:loadLayerShp('" + CommonUtil.getStoragePath(context, true) + "/会议列表/许昌上会地块总" + "',false)");
                mWebView.loadUrl("javascript:layer('" + "xc_zgt" + "','false')");
                mWebView.loadUrl("javascript:layer('" + "xc_lmlw" + "','false')");
                mWebView.loadUrl("javascript:layer('" + "xc_yx" + "','false')");
                mWebView.loadUrl("javascript:layer('" + "xc_yx" + "','true')");
                mWebView.loadUrl("javascript:setMapCenter()");
                break;
        }
    }

    private void showZdkTypeList() {
        showTypeList();
        mWebView.loadUrl("javascript:clearWztPolygon()");
        mWebView.loadUrl("javascript:clearFsdkPolygon()");
    }

    private void showHyList() {
        showHyList(clickMeetingDataList);
        mWebView.loadUrl("javascript:clearWztPolygon()");
        mWebView.loadUrl("javascript:clearFsdkPolygon()");
    }

    private void initView() {
        iv_back = view.findViewById(R.id.iv_metpop_back);
        tv_hyList = view.findViewById(R.id.tv_metpop_hyList);
        tv_typeList = view.findViewById(R.id.tv_metpop_typeList);
        tv_zdk_list = view.findViewById(R.id.tv_metpop_zdkList);
        tv_close = view.findViewById(R.id.tv_metpop_close);
        lv_show = view.findViewById(R.id.lv_metpop_show);

        iv_back.setOnClickListener(this);
        tv_hyList.setOnClickListener(this);
        tv_typeList.setOnClickListener(this);
        tv_zdk_list.setOnClickListener(this);
        tv_close.setOnClickListener(this);

    }


}