package com.example.activitytest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

private static final String TAG ="1";
private EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout2);
       Button button=findViewById(R.id.btn);
       et=findViewById(R.id.et);
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String text=et.getText().toString();
               Log.d("leo","用户名"+text);
           }
       });
        /*Button btn=findViewById(R.id.button);
        //点击事件
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"onClick:");
            }
        });
        //长按事件
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.e(TAG,"onLongClick:");
                return false;
            }
        });
        //触摸事件
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG,"OnTouchListener:");
                return false;
            }
        });

    }

    public void leoClick(View view) {
        Log.e(TAG,"leoClick:");
    }*/
    }
}