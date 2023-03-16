package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        ImageButton button=findViewById(R.id.button);
        ImageView imageView=findViewById(R.id.iv_scale3);
        imageView.setImageResource(R.drawable.icon3);
        //在Java代码中可调用setScaleType方法设置图像视图的缩放类型
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();//终止当前活动，关闭当前页面即返回上一级
            }
        });
    }
}