package com.czy.mp002;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by CZY on 2019/2/8.
 */

public class MusicAdapter extends BaseAdapter {
    private LinkedList<Music> MusicList;
    private Context mContext;

    public MusicAdapter(LinkedList<Music> MusicList,Context mContext)
    {
        this.mContext=mContext;
        this.MusicList=MusicList;
    }
    //下面好像是一些用不到（但是必须覆盖）的方法？返回值都不太对
    @Override
    public int getCount()
    {
        return MusicList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=null;
        View view=null;
        if(convertView==null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item,null);

            holder=new ViewHolder();

            holder.img_icon = view.findViewById(R.id.SongPic);
            holder.txt_name = (TextView) view.findViewById(R.id.SongName);
            holder.txt_artist = (TextView) view.findViewById(R.id.SongArtist);
            view.setTag(holder);

        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
            view=convertView;
        }
        // countV.setText(String.valueOf(MusicList.get(position).count));

        holder.img_icon.setText(String.valueOf(MusicList.get(position).count));

        //String texttemp = String.valueOf(MusicList.get(position).count) + "-" + MusicList.get(position).title;
        String texttemp=MusicList.get(position).title;
        //holder.img_icon.setImageBitmap(MusicList.get(position).albumCover);
        //MusicList.get(position).coverLocation=img_icon;
        holder.txt_name.setText(texttemp);


        String texttemp2 = MusicList.get(position).artist + "-" + MusicList.get(position).album;
        holder.txt_artist.setText(texttemp2);



        return view;



        //这里adapter完成……大概？
    }
    public class ViewHolder
    {
        TextView img_icon;
        TextView txt_name;
        TextView txt_artist;
    }
}
