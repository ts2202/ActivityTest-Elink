package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class ShareReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_read);
        // 从share.xml获取共享参数实例
        SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
        String name = shared.getString ( "name","小李子");//从共享参数获取名为name的字符串，第二个参数表示默认值
        int age = shared.getInt ("age",0);// 从共享参数获取名为age 的整型数
        boolean married = shared.getBoolean ( "married",false);//从共享参数获取名为married的布尔数
        float weight = shared.getFloat ( "weight",0);//从共享参数获取名为weight的浮点数
        TextView textView=findViewById(R.id.read);
        textView.setText("共享参数中保存的信息如下"+"\n"+"name:"+name+"\n"+"age:"+age+"\n"+"married:"+married+"\n"+"weight:"+weight);
        textView.setTextSize(20);


    }
}