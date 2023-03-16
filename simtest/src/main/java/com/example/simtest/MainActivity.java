package com.example.simtest;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.size_compat_mode_layout);
        setTitle("SIM卡测试");
        registerairplaneReceiver();
        wifiManager.setWifiEnabled(false);

    }
    private void registerairplaneReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("action_angle_report");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        Log.e("tlh", "111");
        registerReceiver(new AirplaneReceiver() {
        },intentFilter);

    }
}