package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

// 该页面类实现了接口OnDateSetListener，意味着要重写日期监听器的onDateSet方法
public class DatePickerActivity extends AppCompatActivity implements
        View.OnClickListener, DatePickerDialog.OnDateSetListener ,TimePickerDialog.OnTimeSetListener{
    private TextView tv_date; // 声明一个文本视图对象
    private DatePicker dp_date; // 声明一个日期选择器对象
    private TextView tv_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        tv_date = findViewById(R.id.tv_date);
       // 从布局文件中获取名叫dp_date的日期选择器
        dp_date = findViewById(R.id.dp_date);
        tv_time=findViewById(R.id.tv_time);
        findViewById(R.id.btn_date).setOnClickListener(this);
        findViewById(R.id.btn_time).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_date) {
            // 获取日历的一个实例，里面包含了当前的年月日
            Calendar calendar = Calendar.getInstance();
            // 构建一个日期对话框，该对话框已经集成了日期选择器。
           // DatePickerDialog的第二个构造参数指定了日期监听器
            DatePickerDialog dialog = new DatePickerDialog(this, this,
                    calendar.get(Calendar.YEAR), // 年份
                    calendar.get(Calendar.MONTH), // 月份
                    calendar.get(Calendar.DAY_OF_MONTH)); // 日子
            dialog.show(); // 显示日期对话框
        }
        else if(v.getId() == R.id.btn_time){
             // 获取日历的一个实例，里面包含了当前的时分秒
            Calendar calendar = Calendar.getInstance();
            // 构建一个时间对话框，该对话框已经集成了时间选择器。
            // TimePickerDialog的第二个构造参数指定了时间监听器
            TimePickerDialog dialog = new TimePickerDialog(this, this,
                    calendar.get(Calendar.HOUR_OF_DAY), // 小时
                    calendar.get(Calendar.MINUTE), // 分钟
                    true); // true表示24小时制，false表示12小时制
            dialog.show(); // 显示时间对话框
        }
    }
    // 一旦点击日期对话框上的确定按钮，就会触发监听器的onDateSet方法
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int
            dayOfMonth) {
        // 获取日期对话框设定的年月份
        String desc = String.format("您选择的日期是%d年%d月%d日",
                year, monthOfYear + 1, dayOfMonth);
        tv_date.setText(desc);
        tv_date.setTextSize(20);
        tv_date.setTextColor(getResources().getColor(R.color.pink));
    }

    // 一旦点击时间对话框上的确定按钮，就会触发监听器的onTimeSet方法
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    // 获取时间对话框设定的小时和分钟
        String desc = String.format("您选择的时间是%d时%d分", hourOfDay, minute);
        tv_time.setText(desc);
        tv_time.setTextSize(20);
        tv_time.setTextColor(getResources().getColor(R.color.teal_700));
    }
}