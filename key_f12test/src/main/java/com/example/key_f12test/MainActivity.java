package com.example.key_f12test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView F12;
    private boolean flag=true;
    private boolean tt=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        F12 = findViewById(R.id.keyboard_F12_status);
        registerHeadsetPlugReceiver();
    }

  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //return super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_F12) {
            if (flag){
            F12.setText("pressed");
            flag=false;
            }
            else {
                F12.append("     "+"pressed");
            }
            F12.setTextColor(Color.parseColor("#0000FF"));
        }
        return true;
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void registerHeadsetPlugReceiver() {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("action_F12_press");
    registerReceiver(new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String f12_status=intent.getStringExtra("status");
            if(tt){
            F12.setText(flag+"");
            tt=false;
                Log.d("TS","FIRST");
            }
            else{
                F12.append("  "+flag);
                Log.d("TS","NOTFIRST");
            }
            Toast.makeText(context,"值："+f12_status,Toast.LENGTH_SHORT).show();
        }
    },intentFilter);
}
}