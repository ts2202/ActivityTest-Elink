package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditFocusActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_focus);
        // 从布局文件中获取名为et_password的编辑框
        EditText et_password = findViewById(R.id.et_password);

        // 给编辑框注册一个焦点变化监听器，一旦焦点发生变化，就触发监听器的onFocusChange方法
        et_password.setOnFocusChangeListener(this);

        // 从布局文件中获取名为et_phone的手机号码编辑框
        EditText et_phone = findViewById(R.id.et_phone);

       // 给手机号码编辑框添加文本变化监听器
        et_phone.addTextChangedListener(new HideTextWatcher(et_phone, 11));
      // 给密码编辑框添加文本变化监听器
        et_password.addTextChangedListener(new HideTextWatcher(et_password, 6));
        Button button=findViewById(R.id.jump);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建提醒对话框的建造器
                AlertDialog.Builder builder = new AlertDialog.Builder(EditFocusActivity.this);
                builder.setTitle("尊敬的用户"); // 设置对话框的标题文本
                builder.setMessage("你真的要卸载我吗？"); // 设置对话框的内容文本
                TextView tv_alert=findViewById(R.id.tv_alert);
               // 设置对话框的肯定按钮文本及其点击监听器
                builder.setPositiveButton("残忍卸载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_alert.setText("虽然依依不舍，但是只能离开了");
                        tv_alert.setTextSize(28);
                    }
                });
                // 设置对话框的否定按钮文本及其点击监听器
                builder.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_alert.setText("让我再陪你三百六十五个日夜");
                        tv_alert.setTextSize(28);
                    }
                });
                AlertDialog alert = builder.create(); // 根据建造器构建提醒对话框对象
                alert.show(); // 显示提醒对话框
            }
        });
        Button button1=findViewById(R.id.jump2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EditFocusActivity.this,DatePickerActivity.class);
                startActivity(intent);
            }
        });
    }

    // 焦点变更事件的处理方法，hasFocus表示当前控件是否获得焦点。
    // 为什么光标进入事件不选onClick？因为EditText要点两下才会触发onClick动作（第一下是切换焦点动作）
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    // 判断密码编辑框是否获得焦点。hasFocus为true表示获得焦点，为false表示失去焦点
        if (v.getId()==R.id.et_password && hasFocus) {
            EditText et_phone = findViewById(R.id.et_phone);
            String phone = et_phone.getText().toString();
            if (TextUtils.isEmpty(phone) || phone.length()<11) { // 手机号码不足11位
       // 手机号码编辑框请求焦点，也就是把光标移回手机号码编辑框
                et_phone.requestFocus();
                Toast.makeText(this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //关闭软键盘
    public static void hideOneInputMethod(Activity act, View v) {
    // 从系统服务中获取输入法管理器
        InputMethodManager imm = (InputMethodManager)
                act.getSystemService(Context.INPUT_METHOD_SERVICE);
    // 关闭屏幕上的输入法软键盘
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    class HideTextWatcher implements TextWatcher {
        private EditText mView; // 声明一个编辑框对象
        private int mMaxLength; // 声明一个最大长度变量

        public HideTextWatcher(EditText v, int maxLength) {
            super();
            mView = v;
            mMaxLength = maxLength;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        // 在编辑框的输入文本变化后触发
        @Override
        public void afterTextChanged(Editable editable) {
            String str = editable.toString(); // 获得已输入的文本字符串
            // 输入文本达到11位（如手机号码），或者达到6位（如登录密码）时关闭输入法
            if ((str.length() == 11 && mMaxLength == 11)
                    || (str.length() == 6 && mMaxLength == 6)) {
                hideOneInputMethod(EditFocusActivity.this, mView); // 隐藏输入法软键盘
            }
        }

    }
}
