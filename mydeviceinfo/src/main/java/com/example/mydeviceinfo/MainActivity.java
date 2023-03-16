package com.example.mydeviceinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.preference.Preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;

public class MainActivity extends PreferenceActivity{
    PreferenceScreen mscreen ;
    int mDeviceInfoCountdown = 5;
    private Preference pfDeviceInfo;
    private PreferenceGroup preferenceCategory;
    private final static String KEY_DEVICE_VISIABLE = "key_deviceinfo_visiable";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        mscreen = getPreferenceScreen();
        preferenceCategory=(PreferenceGroup)mscreen.findPreference("a");
         pfDeviceInfo=new Preference(this);//构建一个子Preference，待添加到容器中
        pfDeviceInfo.setKey("elink_device_info");//设置key
        pfDeviceInfo.setTitle("硬件信息");//设置title
        pfDeviceInfo.setSummary("显示屏，重力感应");
        pfDeviceInfo.setOrder(2);
        //mscreen.removePreference(findPreference("b"));
        //preferenceCategory.removeAll();
        if(isDeviceInfoVisiable())
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
                Log.d("qyj", "preferenceGroup="+preferenceGroup);
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