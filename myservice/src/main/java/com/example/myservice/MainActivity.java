package com.example.myservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private float getAngle;
    public String text;
    private Context context;
    TextView t;
    TextView t2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent intent = new Intent("action_angle_get");
        Intent intent = new Intent("action_SPI_read_write");
        intent.putExtra("action_value","0010");
        intent.setComponent(new ComponentName("com.android.usbtospiangledome", "com.android.usbtospiangledome.UsbToSpiAngleDomeService"));
        startService(intent);
        UsbManager usbManager= (UsbManager)getApplicationContext().getSystemService(Context.USB_SERVICE);
        registerHeadsetPlugReceiver();
        t=findViewById(R.id.textView2);
        t2=findViewById(R.id.textView3);
    }
    private void registerHeadsetPlugReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("action_angle_report");
        intentFilter.addAction("action_SPI_read_write_report");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //getAngle = intent.getFloatExtra("action_angle_get_value", 0);
                //text=intent.getStringExtra("action_angle_get_16_bit_register_value");
                text=intent.getStringExtra("action_SPI_read_write_value");
                //t2.setText("16位寄存器值"+text);
                //t.setText("角度值为"+getAngle);
                t2.setText("SPI收到的值"+text);
                Toast.makeText(context,"值："+text,Toast.LENGTH_SHORT).show();
            }
        },intentFilter);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}