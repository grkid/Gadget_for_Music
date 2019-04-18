package com.czy.mp002;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {
    private SettingAdapter adapter=null;

    CheckBox isRecognitionValid;
    TextView TargetIP,TargetPort;
    EditText TargetIP_in,TargetPort_in;
    Button MakeSureButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
        setListeners();

    }

    private void init()
    {
        isRecognitionValid=findViewById(R.id.CheckBox_RecognitionIsValid);
        TargetIP=findViewById(R.id.TextView_TargetIP);
        TargetPort=findViewById(R.id.TextView_TargetPort);

        TargetIP_in=findViewById(R.id.EditText_TargetIP);
        TargetPort_in=findViewById(R.id.EditText_TargetPort);

        MakeSureButton=findViewById(R.id.Button_InputNetConfig);

        isRecognitionValid.setChecked(NetworkConfig.isValid);
        if(!NetworkConfig.isValid)
        {
            TargetIP.setVisibility(View.INVISIBLE);
            TargetPort.setVisibility(View.INVISIBLE);
            TargetIP_in.setVisibility(View.INVISIBLE);
            TargetPort_in.setVisibility(View.INVISIBLE);
            MakeSureButton.setVisibility(View.INVISIBLE);
        }
    }
    private void setListeners()
    {
        isRecognitionValid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b)
                {
                    TargetIP.setVisibility(View.INVISIBLE);
                    TargetPort.setVisibility(View.INVISIBLE);
                    TargetIP_in.setVisibility(View.INVISIBLE);
                    TargetPort_in.setVisibility(View.INVISIBLE);
                    MakeSureButton.setVisibility(View.INVISIBLE);
                    NetworkConfig.isValid=false;
                }
                else
                {
                    TargetIP.setVisibility(View.VISIBLE);
                    TargetPort.setVisibility(View.VISIBLE);
                    TargetIP_in.setVisibility(View.VISIBLE);
                    TargetPort_in.setVisibility(View.VISIBLE);
                    MakeSureButton.setVisibility(View.VISIBLE);
                    NetworkConfig.isValid=true;
                }

            }
        });

        MakeSureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkConfig.ip=String.valueOf(TargetIP_in.getText());
                NetworkConfig.port= Integer.valueOf(String.valueOf(TargetPort_in.getText()));
            }
        });
    }
}
