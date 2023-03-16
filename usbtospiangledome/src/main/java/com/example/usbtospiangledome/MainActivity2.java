package com.example.usbtospiangledome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {
    private float getAngle;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = new Intent("action_angle_get");
        intent.setComponent(new ComponentName("com.example.usbtospiangledome", "com.example.usbtospiangledome.UsbToSpiAngleDomeService"));
        startService(intent);
        registerHeadsetPlugReceiver();
        TextView t=findViewById(R.id.textView2);
        t.setText("角度值为"+getAngle);
    }
    private void registerHeadsetPlugReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action_angle_report");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getAngle = intent.getFloatExtra("action_angle_get_value", 0);
                Toast.makeText(context,"值："+getAngle,Toast.LENGTH_SHORT).show();
            }
        },intentFilter);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}