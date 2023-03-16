package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class Intent2Activity extends AppCompatActivity implements View.OnClickListener{
    public int a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent2);
/*        String phoneNo = "12345";
        //隐式Intent，没有明确指定要跳转的目标活动，只给出一个动作字符串让系统自动匹配，属于模糊匹配
        Intent intent = new Intent(); // 创建一个新意图
        intent.setAction(Intent.ACTION_DIAL); // 设置意图动作为准备拨号
        Uri uri = Uri.parse("tel:" + phoneNo); // 声明一个拨号的Uri
        intent.setData(uri); // 设置意图前往的路径
        startActivity(intent); // 启动意图通往的活动页面*/
        Button button=findViewById(R.id.button_send);
        button.setOnClickListener(this);
        /*showStringResource();*/
        showMetaData();

/*        // 从布局文件中获取名为v_content的视图
        View v_content = findViewById(R.id.v_content);
        // v_content的背景设置为+
        +.圆角矩形
        v_content.setBackgroundResource(R.drawable.icon2);*/
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_send) {
            //显式创建一个意图对象，准备跳到指定的活动页面
            Intent intent = new Intent(this, Intent3Activity.class);
            Bundle bundle = new Bundle(); // 创建一个新包裹
            TextView tv_send=findViewById(R.id.tv_send);
            // 往包裹存入名为request_time的字符串
            bundle.putString("request_time",  new Date().toString());
            // 往包裹存入名为request_content的字符串
            bundle.putString("request_content",tv_send.getText().toString());
            intent.putExtras(bundle); // 把快递包裹塞给意图
            // 期望接收下个页面的返回数据。第二个参数为本次请求代码
            startActivityForResult(intent, 0);
            //startActivity(intent); // 跳转到意图指定的活动页面*/
        }
    }

    // 从下一个页面携带参数返回当前页面时触发。其中requestCode为请求代码，
   // resultCode为结果代码，intent为下一个页面返回的意图对象
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    { // 接收返回数据
        super.onActivityResult(requestCode, resultCode, intent);
        // 意图非空，且请求代码为之前传的0，结果代码也为成功
        if (intent!=null && requestCode==0 && resultCode== Activity.RESULT_OK) {
            Bundle bundle = intent.getExtras(); // 从返回的意图中获取快递包裹
         // 从包裹中取出名叫response_time的字符串
            String response_time = bundle.getString("response_time");
         // 从包裹中取出名叫response_content的字符串
            String response_content = bundle.getString("response_content");
            String desc = String.format("收到返回消息：\n应答时间为：%s\n应答内容为：%s",
                    response_time, response_content);
            TextView tv_response=findViewById(R.id.tv_response);
            tv_response.setText(desc); // 把返回消息的详情显示在文本视图上
        }
    }
    // 显示字符串资源
    private void showStringResource() {
        String value = getString(R.string.weather_str); // 从strings.xml获取名叫weather_str的字符串值
        TextView tv_resource=findViewById(R.id.tv_resource);
        tv_resource.setText("来自字符串资源：今天的天气是"+value); // 在文本视图上显示文字
    }

    // 显示配置的元数据
    private void showMetaData() {
        try {
            PackageManager pm = getPackageManager(); // 获取应用包管理器
            // 从应用包管理器中获取当前的活动信息
            ActivityInfo act = pm.getActivityInfo(getComponentName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = act.metaData; // 获取活动附加的元数据信息
            String value = bundle.getString("weather"); // 从包裹中取出名叫weather的字符串
            TextView tv_resource=findViewById(R.id.tv_resource);
            tv_resource.setText("来自元数据信息：今天的天气是"+value); // 在文本视图上显示文字
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}