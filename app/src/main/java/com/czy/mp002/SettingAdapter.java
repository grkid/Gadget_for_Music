package com.czy.mp002;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by CZY on 2019/2/27.
 */

public class SettingAdapter extends BaseAdapter {
    private String[] up;
    private String[] down;
    private Context mContext;
    public SettingAdapter(String[] up, String[] down, Context mContext)
    {
        this.up=up;
        this.down=down;
        this.mContext=mContext;

    }

    @Override
    public int getCount()
    {
        return up.length;
        //这个其实很重要。
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder=null;
        View view=null;

        if(convertView==null)
        {
            view= LayoutInflater.from(mContext).inflate(R.layout.setting_list_item,null);
            holder=new ViewHolder();
            holder.SettingTheme=view.findViewById(R.id.SettingTheme);
            holder.SettingSub=view.findViewById(R.id.SettingSub);
            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
            view=convertView;
        }

        holder.SettingTheme.setText(up[position]);
        holder.SettingSub.setText(down[position]);
        //暂时就这样？

        return view;
    }

    private class ViewHolder
    {

        public TextView SettingTheme;
        public TextView SettingSub;
    }
}
