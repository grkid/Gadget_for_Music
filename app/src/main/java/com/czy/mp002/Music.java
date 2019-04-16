package com.czy.mp002;

import android.graphics.Bitmap;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by CZY on 2019/2/8.
 */

public class Music {
    public String title;//歌名
    public String artist;//歌手
    public String album;//所属专辑
    public int length;//长度。应该是秒？
    public Bitmap albumCover=null;
    int coverID;//专辑封面ID
    //ImageView coverLocation;//不得已的办法……
    public String path;//路径
    public boolean isPlaying;//是否正在播放

    int count;//记录数字。多加一个方便统计歌曲数量
}
