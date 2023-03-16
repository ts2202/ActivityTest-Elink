package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    Button button ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        button=findViewById(R.id.button_1);
        TextView tv_hello = findViewById(R.id.textView);
        tv_hello.setText("这是第二个界面，按钮点击后返回上个页面");
       /* tv_hello.setTextColor(0xff00ff00);*///代码中设置颜色要加0x前缀，表示十六进制数
        /*tv_hello.setTextColor(0x00ff00);//透明度00表示完全透明  控件变透明看不到了*/

/*         在代码中修改宽高
        // 获取tv_hello的布局参数（含宽度和高度）
        ViewGroup.LayoutParams params = tv_hello.getLayoutParams();
        // 修改布局参数中的宽度数值，注意默认px单位，需要把dp数值转成px数值
        params.width = dip2px(this, 300);
        tv_hello.setLayoutParams(params); // 设置tv_hello的布局参数*/

        //按钮点击后跳转上个页面
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*销毁当前活动  与返回键作用相同 退栈
                finish();*/
                // 创建一个意图对象，准备跳到指定的活动页面
                Intent intent = new Intent(SecondActivity.this, FirstActivity.class);
              // 当栈中存在待跳转的活动实例时，则重新创建该活动的实例，并清除原实例上方的所有实例
                /*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志*/
                startActivity(intent); // 跳转到意图指定的活动页面
                /*
                1.以standard模式启动活动， 第二个页面以finish()方法返回
                点击跳转按钮栈中的情况：
                1    2    1    2   返  1  返回  首页
                     1         1   回

                多次点击跳转按钮跳转页面但最后在第一个页面点击返回键则一次就退回到首页
                两个页面都设置启动标志FLAG_ACTIVITY_CLEAR_TOP，第二个页面采用startActivity(intent)方法跳转的情况，也可实现一次返回首页

                2.以standard模式启动活动， 第二个页面以startActivity(intent)方法返回栈中的情况
                点击跳转按钮栈中的情况：
                1   2     1     2        1       2      1
                    1     2     1   返   2   返   1  返      返    首页
                          1     2   回   1   回      回      回
                                1
                多次点击跳转按钮跳转页面但最后在第一个页面点击返回键需点击同样的跳转次数才退回到首页
                 */
            }
        });

        //点击按钮3跳转
        findViewById(R.id.button_3).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this,ThirdActivity.class));
            }
        });
    }

    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context, float dpValue) {
    // 获取当前手机的像素密度（1个dp对应几个px）
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f); // 四舍五入取整
    }

    @Override
    protected void onResume() {
        super.onResume();

        //刷新界面
/*        button.setText("uiii");*/
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}