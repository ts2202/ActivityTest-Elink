package com.example.simtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class AirplaneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.e("tlh", "Service state changed");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object o = bundle.get("state");
            Log.e("tlh", o.toString());
            switch (o.toString()) {
                case "false":
                    Log.e("tlh", "AirPlaneMode ----- false.");
                    //wifiManager.enableNetwork(1,true);
                    break;
                case "true":
                    Log.e("tlh", "AirPlaneMode ----- true");
                    //wifiManager.enableNetwork(1,true);
                    break;
                default:
                    Log.e("tlh", "Set AirPlaneMode default.");
                    break;
            }

        } else {
            Log.e("tlh", "bundle is null");
        }
    }
}
