package com.appler.mettingsystem_xuchang.layer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.appler.mettingsystem_xuchang.R;
import com.appler.mettingsystem_xuchang.metting.ShowMeetingPop;


public class ShowTreePop extends PopupWindow implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "ShowTreePop";


    private WebView mWebView;
    private Activity context;
    private View view;
    private PopupWindow popupWindow;


    private Button btn_yx;
    private Button btn_gh;
    private LinearLayout ll_yx;
    private LinearLayout ll_gh;
    public static CheckBox cb_xc_yx;
    public static RadioButton rb_xc_yx;
    public static CheckBox cb_xc_lmlw;
    public static RadioButton rb_xc_lmlw;
    public static CheckBox cb_xc_zgt;
    public static RadioButton rb_xc_zgt;

    private boolean yxClick = true;
    private boolean ghClick = true;


    public ShowTreePop(Activity context, WebView mweb) {
        super(context);
        this.context = context;
        this.mWebView = mweb;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.pop_pattern, null);

        initView();
        setOnClick();
    }

    public void showpop(View parent) {

        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = manager.getDefaultDisplay().getWidth();
        int height = manager.getDefaultDisplay().getHeight();
        popupWindow = new PopupWindow(view, width / 2, ViewGroup.LayoutParams.WRAP_CONTENT, false);
//        popupWindow = new PopupWindow(view, width / 2, height * 2 / 3, false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
//        popupWindow.setFocusable(true);   //1201修改 注释掉
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(parent, 0, 20);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yx:  //影像数据
                if (yxClick == false) {
                    ll_yx.setVisibility(View.VISIBLE);
                    yxClick = true;
                } else if (yxClick == true) {
                    ll_yx.setVisibility(View.GONE);
                    yxClick = false;
                }
                break;
            case R.id.btn_gh:  //影像数据
                if (ghClick == false) {
                    ll_gh.setVisibility(View.VISIBLE);
                    yxClick = true;
                } else if (ghClick == true) {
                    ll_gh.setVisibility(View.GONE);
                    ghClick = false;
                }
                break;
            case R.id.rb_xc_yx:
                setRbWhite(rb_xc_yx);
                RadioClick("xc_yx", rb_xc_yx);
                break;
            case R.id.rb_xc_lmlw:
                setRbWhite(rb_xc_lmlw);
                RadioClick("xc_lmlw", rb_xc_lmlw);
                break;
            case R.id.rb_xc_zgt:
                setRbWhite(rb_xc_zgt);
                RadioClick("xc_zgt", rb_xc_zgt);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_xc_yx:
                if (isChecked) {
                    SettingWebView("xc_yx", rb_xc_yx);
                    ShowMeetingPop.jbqkClick = true;
                } else {
                    FalseWeb("xc_yx", rb_xc_yx);
                    ShowMeetingPop.jbqkClick = false;
                }
                break;
            case R.id.cb_xc_lmlw:
                if (isChecked) {
                    SettingWebView("xc_lmlw", rb_xc_lmlw);
                } else {
                    FalseWeb("xc_lmlw", rb_xc_lmlw);
                }
                break;
            case R.id.cb_xc_zgt:
                if (isChecked) {
                    SettingWebView("xc_zgt", rb_xc_zgt);
                    ShowMeetingPop.wztClick = true;
                } else {
                    FalseWeb("xc_zgt", rb_xc_zgt);
                    ShowMeetingPop.wztClick = false;
                }
                break;
        }

    }


    private void RadioClick(String name, RadioButton radioButton) {
        mWebView.loadUrl("javascript:zhiding('" + name + "')");
        setRbWhite(radioButton);
    }

    private void setRbWhite(RadioButton radioButton) {
        rb_xc_yx.setTextColor(Color.WHITE);
        rb_xc_lmlw.setTextColor(Color.WHITE);
        rb_xc_zgt.setTextColor(Color.WHITE);

        radioButton.setTextColor(Color.parseColor("#FFFD4080"));

    }


    private void SettingWebView(String name, RadioButton radioButton) {
        mWebView.loadUrl("javascript:layer('" + name + "','true')");
        radioButton.setVisibility(View.VISIBLE);
    }

    private void FalseWeb(String name, RadioButton radioButton) {
        mWebView.loadUrl("javascript:layer('" + name + "','false')");
        radioButton.setVisibility(View.GONE);
    }

//    private void SettingShpWebView(String name, RadioButton radioButton) {
//        mWebView.loadUrl("javascript:loadLayerShp('" + name + "',true)");
//        radioButton.setVisibility(View.VISIBLE);
//    }
//
//    private void FalseShpWeb(String name, RadioButton radioButton) {
//        mWebView.loadUrl("javascript:unloadLayer('" + name + "',false)");
//        radioButton.setVisibility(View.INVISIBLE);
//    }
//

    private void initView() {

        btn_yx = (Button) view.findViewById(R.id.btn_yx);
        btn_gh = (Button) view.findViewById(R.id.btn_gh);
        ll_yx = (LinearLayout) view.findViewById(R.id.ll_yx);
        ll_gh = (LinearLayout) view.findViewById(R.id.ll_gh);
        cb_xc_yx = view.findViewById(R.id.cb_xc_yx);
        cb_xc_lmlw = view.findViewById(R.id.cb_xc_lmlw);
        cb_xc_zgt = view.findViewById(R.id.cb_xc_zgt);
        rb_xc_yx = (RadioButton) view.findViewById(R.id.rb_xc_yx);
        rb_xc_lmlw = (RadioButton) view.findViewById(R.id.rb_xc_lmlw);
        rb_xc_zgt = (RadioButton) view.findViewById(R.id.rb_xc_zgt);

    }

    private void setOnClick() {

        cb_xc_yx.setOnCheckedChangeListener(this);
        cb_xc_lmlw.setOnCheckedChangeListener(this);
        cb_xc_zgt.setOnCheckedChangeListener(this);

        rb_xc_yx.setOnClickListener(this);
        rb_xc_lmlw.setOnClickListener(this);
        rb_xc_zgt.setOnClickListener(this);

        btn_yx.setOnClickListener(this);
        btn_gh.setOnClickListener(this);
    }


}
