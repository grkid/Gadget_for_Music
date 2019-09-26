package com.czy.mp002;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by CZY on 2019/4/16.
 */

public class NetAdapter {
    //用于和服务器进行通信的类
    //录音的事情不在其责任范围内
    //负责把wav文件发送到服务器并接收结果，返回这个结果
    public static AsyncHttpClient client=new AsyncHttpClient();
    //链接超时时间默认为10s

    public static long recordStartTime;




}
