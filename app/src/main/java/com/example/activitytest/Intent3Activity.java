package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Date;

public class Intent3Activity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent3);
        // 从布局文件中获取名为tv_receive的文本视图
        TextView tv_receive = findViewById(R.id.tv_receive);
        // 从上一个页面传来的意图中获取快递包裹
        Bundle bundle = getIntent().getExtras();
       // 从包裹中取出名为request_time的字符串
        String request_time = bundle.getString("request_time");
        // 从包裹中取出名为request_content的字符串
        String request_content = bundle.getString("request_content");
        String desc = String.format("收到请求消息：\n请求时间为%s\n请求内容为%s",
                request_time, request_content);
        tv_receive.setText(desc); // 把请求消息的详情显示在文本视图上
        Button button_send=findViewById(R.id.button_send);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = "我看过了，天气确实很不错";
                Intent intent = new Intent(); // 创建一个新意图
                Bundle bundle2 = new Bundle(); // 创建一个新包裹
                // 往包裹存入名为response_time的字符串
                bundle2.putString("response_time", new Date().toString());
                // 往包裹存入名为response_content的字符串
                bundle2.putString("response_content", response);
                intent.putExtras(bundle2); // 把快递包裹塞给意图
                // 携带意图返回上一个页面。RESULT_OK表示处理成功
                setResult(Activity.RESULT_OK, intent);
                finish(); // 结束当前的活动页面
            }
        });
        // 从布局文件中获取名为v_content的视图
        View v_content = findViewById(R.id.v_content);
        // v_content的背景设置为圆角矩形
        v_content.setBackgroundResource(R.drawable.shape_oval_rose);

        // 从布局文件中获取名叫ck_system的复选框
        CheckBox ck_system = findViewById(R.id.ck_system);
        // 给ck_system设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        ck_system.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        String desc = String.format("您%s了这个CheckBox", isChecked ? "勾选" : "取消勾选");
        compoundButton.setText(desc);
    }
}