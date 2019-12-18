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

public class MeetingListAdapter extends BaseAdapter  {
    private static final String TAG = "MettingListAdapter";
    private Context context;
    private LayoutInflater inflater;
    private List<MeetingData> datas;


    public MeetingListAdapter(Context context, List<MeetingData> datas) {
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
            view = inflater.inflate(R.layout.metting_name_layout, null);
            holder.tv_meeting_name = view.findViewById(R.id.tv_meeting_name);
            holder.ll_meeting = view.findViewById(R.id.ll_meeting);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final MeetingData meetingData = datas.get(position);
        final String data = meetingData.getMettingName();
        holder.tv_meeting_name.setText(data);

        holder.ll_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mettingItemClickListenerClick.mettingItemClick(position, meetingData);
            }
        });
        return view;
    }


    class ViewHolder {
        TextView tv_meeting_name;
        LinearLayout ll_meeting;

    }

    public interface MettingItemClickListenerClick {
        void mettingItemClick(int position, MeetingData mettingData);
    }

    private MettingItemClickListenerClick mettingItemClickListenerClick;

    public void setMettingItemClickListenerClick(MettingItemClickListenerClick mettingItemClickListenerClick) {
        this.mettingItemClickListenerClick = mettingItemClickListenerClick;
    }


}
