package com.appler.mettingsystem_xuchang.metting;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appler.mettingsystem_xuchang.R;

import java.util.ArrayList;
import java.util.List;

public class DiKuaiAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = "SearchAdapter";
    private Context context;
    private LayoutInflater inflater;
    private List<ZhuDiKuaiData.FeaturesBean> datas;

    private ArrayFilter mFilter;

    private final Object mLock = new Object();
    private ArrayList<ZhuDiKuaiData.FeaturesBean> mOriginalValues;

    private int selectedPosition = -1;// 选中的位置

    public DiKuaiAdapter(Context context, List<ZhuDiKuaiData.FeaturesBean> datas) {
        this.context = context;
        this.datas = datas;

        inflater = LayoutInflater.from(context);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
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
            view = inflater.inflate(R.layout.dikuai_item_layout, null);
            holder.tv_dk_name = view.findViewById(R.id.tv_dk_name);
            holder.tv_dk_weizhi = view.findViewById(R.id.tv_dk_weizhi);
            holder.tv_dk_xzyx = view.findViewById(R.id.tv_dk_xzyx);
            holder.ll_dk_item = view.findViewById(R.id.ll_dk_item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ZhuDiKuaiData.FeaturesBean data = datas.get(position);

        holder.tv_dk_name.setText(data.getProperties().getNAME());
        if (selectedPosition == position) {
            holder.tv_dk_name.setTextColor(Color.parseColor("#09488b"));
            holder.tv_dk_name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            holder.tv_dk_name.setTextColor(Color.WHITE);
            holder.tv_dk_name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        holder.tv_dk_weizhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diKuaiItemClickListener.dk_weizhituClick(position, data);
            }
        });
        holder.tv_dk_xzyx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diKuaiItemClickListener.dk_xzyxClick(position, data);
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    class ViewHolder {

        TextView tv_dk_name;
        TextView tv_dk_weizhi;
        TextView tv_dk_xzyx;
        LinearLayout ll_dk_item;

    }

    public interface DiKuaiItemClickListener {

        void dk_weizhituClick(int position, ZhuDiKuaiData.FeaturesBean featuresBean);

        void dk_xzyxClick(int position, ZhuDiKuaiData.FeaturesBean featuresBean);
    }

    private DiKuaiItemClickListener diKuaiItemClickListener;

    public void setDiKuaiItemClickListener(DiKuaiItemClickListener diKuaiItemClickListener) {
        this.diKuaiItemClickListener = diKuaiItemClickListener;
    }

    private class ArrayFilter extends Filter {
        //执行刷选
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();//过滤的结果
            //原始数据备份为空时，上锁，同步复制原始数据
            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(datas);
                }
            }
            //当首字母为空时
            if (prefix == null || prefix.length() == 0) {
                ArrayList<ZhuDiKuaiData.FeaturesBean> list;
                synchronized (mLock) {//同步复制一个原始备份数据
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();//此时返回的results就是原始的数据，不进行过滤
                Log.i(TAG, "performFiltering: " + list.size());
            } else {
                String prefixString = prefix.toString().toLowerCase();//转化为小写

                ArrayList<ZhuDiKuaiData.FeaturesBean> values;
                synchronized (mLock) {//同步复制一个原始备份数据
                    values = new ArrayList<>(mOriginalValues);
                }
                final int count = values.size();
                final List<ZhuDiKuaiData.FeaturesBean> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final ZhuDiKuaiData.FeaturesBean value = values.get(i);//从List<User>中拿到User对象
//                    final String valueText = value.toString().toLowerCase();
                    final String valueText = value.getProperties().getNAME().toLowerCase();//User对象的name属性作为过滤的参数
                    Log.i(TAG, "performFiltering: " + prefixString);
                    Log.i(TAG, "performFiltering: " + valueText);
//                    final String valueid = value.getProperties().getId().toLowerCase();//User对象的name属性作为过滤的参数
                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString) || valueText.indexOf(prefixString.toString()) != -1
//                            || valueid.startsWith(prefixString) || valueid.indexOf(prefixString.toString()) != -1
                    ) {//第一个字符是否匹配
                        newValues.add(value);//将这个item加入到数组对象中
//                        Log.i(TAG, "performFiltering: " + newValues.size());
                    } else {//处理首字符是空格
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {//一旦找到匹配的就break，跳出for循环
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;//此时的results就是过滤后的List<User>数组
                Log.i(TAG, "performFiltering: " + newValues.size());
                results.count = newValues.size();
            }
            return results;
        }

        //刷选结果
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            datas = (List<ZhuDiKuaiData.FeaturesBean>) results.values;//此时，Adapter数据源就是过滤后的Results
            if (results.count > 0) {
                notifyDataSetChanged();//这个相当于从mDatas中删除了一些数据，只是数据的变化，故使用notifyDataSetChanged()
            } else {
                /**
                 * 数据容器变化 ----> notifyDataSetInValidated

                 容器中的数据变化  ---->  notifyDataSetChanged
                 */
                notifyDataSetInvalidated();//当results.count<=0时，此时数据源就是重新new出来的，说明原始的数据源已经失效了
            }
        }
    }


}
