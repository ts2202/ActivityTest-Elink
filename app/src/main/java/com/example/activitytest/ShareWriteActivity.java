package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ShareWriteActivity extends AppCompatActivity /*implements RadioGroup.OnCheckedChangeListener*/{
    public Boolean id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_write);
        SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit(); // 获得编辑器的对象
        // 从share.xml获取共享参数实例
/*      editor.putString("name", "Mr Ten"); // 添加一个名为name的字符串参数
        editor.putInt("age", 20); // 添加一个名为age的整型参数
        editor.putBoolean("married", true); // 添加一个名为married的布尔型参数
        editor.putFloat("weight", 100f); // 添加一个名为weight的浮点数参数
        editor.commit(); // 提交编辑器中的修改*/
        RadioGroup rg_sex = findViewById(R.id.rg_sex);
        System.out.println("这是id："+R.id.tv_sex);
        // 设置单选监听器，一旦点击组内的单选按钮，就触发监听器的onCheckedChanged方法
        /*rg_sex.setOnCheckedChangeListener(this);*/
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t=findViewById(R.id.name);
                TextView t2=findViewById(R.id.age);
                TextView t3=findViewById(R.id.weight);
                editor.putString("name",t.getText().toString()); // 添加一个名为name的字符串参数
                editor.putInt("age", Integer.parseInt(t2.getText().toString())); // 添加一个名为age的整型参数
                System.out.println("这是rb_male id："+R.id.rb_male);
                System.out.println("这是rg_sex id："+rg_sex.getCheckedRadioButtonId());
                if(rg_sex.getCheckedRadioButtonId()==R.id.rb_male){
                    editor.putBoolean("married", false); // 添加一个名为married的布尔型参数
                }
                else{
                    editor.putBoolean("married", true); // 添加一个名为married的布尔型参数
                }
                editor.putFloat("weight", Float.parseFloat(t3.getText().toString())); // 添加一个名为weight的浮点数参数
                editor.commit(); // 提交编辑器中的修改
                Intent intent=new Intent(ShareWriteActivity.this,ShareReadActivity.class);
                startActivity(intent);
            }
        });
    }
/*    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == R.id.rb_male) {
            id=false;
        } else if (checkedId == R.id.rb_female) {
            id=true;
        }
    }*/

}