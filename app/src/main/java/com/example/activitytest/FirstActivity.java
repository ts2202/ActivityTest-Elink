package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class FirstActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        setTitle(R.string.firstactivity);//重置activity标题
        Button button=findViewById(R.id.button_1);
        TextView tv_hello = findViewById(R.id.textView);
        tv_hello.setText(R.string.c);//按钮点击后进入下个页面  引用string.xml中的字符串
        tv_hello.setTextSize(30);//代码中设置size默认以sp为单位
        tv_hello.setTextColor(Color.BLUE);
       /* tv_hello.setTextColor(this.getResources().getColor(R.color.yellow));*///设置名称为yellow的颜色资源
        tv_hello.setBackgroundColor(Color.GRAY);//色值来源于Color类或十六进制数，则调用setBackgroundColor方法设置背景
        tv_hello.setBackgroundResource(R.color.yellow);//设置名称为yellow的颜色资源
        //色值来源于colors.xml中的颜色资源，则调用setBackgroundResource方法
        //setBackgroundResource用来设置控件的背景，不单单是 背景颜色，还包括背景图片
        tv_hello.setBackgroundResource(R.drawable.yellow);//设置名称为yellow的图片为背景
        context = this;

        //实现按钮跳转的方式
        //1.按钮点击后跳转下个页面 为按钮注册一个监听器
       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从当前页面跳到新页面;
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                startActivity(intent);

                *//*context.startService(new Intent("android.sssssss"));*//*
            }
        });*/



        //按钮长按后弹出用户消息提示信息
        Button button2=findViewById(R.id.button_2);
        button2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //第三个参数是Toast显示的时长，内置常量可以选择Toast.LENGTH_SHORT和Toast.LENGTH_LONG
                Toast.makeText(FirstActivity.this,"你长按了按钮2",
                        Toast.LENGTH_SHORT).show();

                /* sendBroadcast(new Intent("android.mmmmm"));*/
                return true;
            }
        });
    }

   // 2.在布局文件中为Button标签指定了onClick属性值为doClick，android:onClick="doClick"，表示点击该按钮会触发Java代码中的doClick方法
    public void doClick(View v) {
        //从当前页面跳到新页面;
        Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
        // 当栈中存在待跳转的活动实例时，则重新创建该活动的实例，并清除原实例上方的所有实例
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志 不返回重复页面
        startActivity(intent); // 跳转到意图对象指定的活动页面
/*        // 设置启动标志：跳转到新页面时，栈中的原有实例都被清空，同时开辟新任务的活动栈，即新页面跳转后点返回不再返回第一个页面而是返回首页
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent); // 跳转到意图指定的活动页面*/
    }

   /* 定时器3秒后跳转下个页面
   @Override
    protected void onResume() {
        super.onResume();
        goNextPage(); // 跳到下个页面
    }
    // 跳到下个页面
    private void goNextPage() {
        TextView tv_hello = findViewById(R.id.textView);
        Button button=findViewById(R.id.button_1);
        tv_hello.setText("3秒后进入下个页面");
// 延迟3秒（3000毫秒）后启动任务mGoNext
        new Handler(Looper.myLooper()).postDelayed(mGoNext, 3000);
    }
    private Runnable mGoNext = new Runnable() {
        @Override
        public void run() {
// 活动页面跳转，从MainActivity跳到Main2Activity
            startActivity(new Intent(FirstActivity.this, SecondActivity.class));
        }
    };*/
}