package com.example.headsetchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private HeadsetChangeReceiver headsetPlugReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerHeadsetPlugReceiver();
        }
        private void registerHeadsetPlugReceiver() {

            headsetPlugReceiver = new HeadsetChangeReceiver();
            IntentFilter intentFilter = new IntentFilter();

            intentFilter.addAction("android.intent.action.HEADSET_PLUG");

            registerReceiver(headsetPlugReceiver, intentFilter);

        }
        @Override

        public void onDestroy() {

            unregisterReceiver(headsetPlugReceiver);
            super.onDestroy();

        }

    }