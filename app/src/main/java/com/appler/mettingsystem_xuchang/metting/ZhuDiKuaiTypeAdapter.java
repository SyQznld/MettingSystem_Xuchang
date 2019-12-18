package com.appler.mettingsystem_xuchang.metting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appler.mettingsystem_xuchang.R;

import java.util.List;


/**
 * 主地块类型  出让 划拨 收储  特殊
 */
public class ZhuDiKuaiTypeAdapter extends BaseAdapter {
    private static final String TAG = "DiKuaiTypeAdapter";
    private Context context;
    private LayoutInflater inflater;
    private List<String> datas;
    private boolean showDkList;

    public ZhuDiKuaiTypeAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.zhudikuai_type_layout, null);
            holder.tv_zdkType_name = view.findViewById(R.id.tv_zdkType_name);
            holder.ll_zdkType = view.findViewById(R.id.ll_zdkType);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final String type = datas.get(position);
        holder.tv_zdkType_name.setText(type);

        holder.ll_zdkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mettingItemClickListenerClick.mettingItemClick(position, type);
            }
        });
        return view;
    }


    class ViewHolder {

        TextView tv_zdkType_name;
        LinearLayout ll_zdkType;

    }

    public interface MettingItemClickListenerClick {
        void mettingItemClick(int position, String mettingData);
    }

    private MettingItemClickListenerClick mettingItemClickListenerClick;

    public void setMettingItemClickListenerClick(MettingItemClickListenerClick mettingItemClickListenerClick) {
        this.mettingItemClickListenerClick = mettingItemClickListenerClick;
    }


}
