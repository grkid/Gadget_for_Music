package com.czy.mp002;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class SettingActivity extends AppCompatActivity {
    private SettingAdapter adapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        String[] up=new String[1];
        String[] down=new String[1];
        up[0]="这里是设置界面！";
        down[0]="具体的设置信息正在处理中……";

        adapter=new SettingAdapter(up,down,this);
        ListView listView=findViewById(R.id.SettingListView);
        listView.setAdapter(adapter);

    }
}
