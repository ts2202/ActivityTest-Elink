package com.example.mydeviceinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceCategory;
import android.preference.PreferenceGroup;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

public class MainActivity2 extends PreferenceActivity{
    PreferenceScreen mscreen ;
    private PreferenceGroup preferenceCategory;
    int mDeviceInfoCountdown = 5;
    private Preference pfDeviceInfo;
    private final static String KEY_DEVICE_VISIABLE = "key_deviceinfo_visiable";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference2);
        mscreen = getPreferenceScreen();
        preferenceCategory=(PreferenceGroup)mscreen.findPreference("thirdC");
        pfDeviceInfo=mscreen.findPreference("elink_device_info2");
        setViewVisible(preferenceCategory, isDeviceInfoVisiable());
        Preference build_number=mscreen.findPreference("build_number");
        build_number.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.i("qyj", "HardwareRevisionPreferenceController clickSetDeviceInfoVisiable,mDeviceInfoCountdown="+mDeviceInfoCountdown);
                if (mDeviceInfoCountdown > 0) {
                    mDeviceInfoCountdown--;
                    if (mDeviceInfoCountdown == 0) {
                        if (!isDeviceInfoVisiable()) {
                            setViewVisible(preferenceCategory, true);
                        } else {
                            setViewVisible(preferenceCategory, false);
                        }
                        mDeviceInfoCountdown=5;
                    }
                }
                return true;
            }
        });
    }
    private boolean isDeviceInfoVisiable() {
        return this.getSharedPreferences(KEY_DEVICE_VISIABLE,
                Context.MODE_PRIVATE).getInt(KEY_DEVICE_VISIABLE, 0) == 0 ? false
                : true;
    }
    private void click(){

    }

    private void setViewVisible(PreferenceGroup preferenceGroup, boolean visiable) {
        try{
            if(visiable){
                preferenceGroup.addPreference(pfDeviceInfo);//添加到容器中
                Log.d("qyj", "deviceinfo setViewVisible true");
            }else{
                boolean oo = preferenceGroup.removePreference(pfDeviceInfo);
                Log.d("qyj", "deviceinfo setViewVisible false,oo="+oo);
            }
        } catch (RuntimeException e) {
            Log.d("qyj", "deviceinfo setViewVisible exception !!!"+e.getMessage());
        }
        this.getSharedPreferences(KEY_DEVICE_VISIABLE, Context.MODE_PRIVATE)
                .edit().putInt(KEY_DEVICE_VISIABLE, visiable==true?1:0).commit();
    }
}