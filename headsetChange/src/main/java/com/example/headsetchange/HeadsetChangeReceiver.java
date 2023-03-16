package com.example.headsetchange;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import android.util.Log;

public class HeadsetChangeReceiver extends BroadcastReceiver {
        private static final String STATE = "state";
        private AudioManager mAudioManager;
        private int volume;
        private boolean flag =true;
        private int volume2;
    @SuppressLint("MissingPermission")
     public void onReceive(Context context, Intent intent) {
         Log.d("rr","FLAG_IEC_UNITS="+intent);
         //int isfirst = Settings.Global.getInt(context.getContentResolver(),"isfirst",0);
        if (intent.hasExtra(STATE)) {
        if (intent.getIntExtra(STATE, 0) == 0) {
        // 断开耳机
           // Log.d("rr","isfirst="+isfirst);
            /*if (isfirst==0){
                mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                volume=mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
                Settings.Global.putInt(context.getContentResolver(),"volume",volume);
            }*/
            Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();
            //volume = Settings.Global.getInt(context.getContentResolver(),"volume",0);
            //Log.d("rr","disconnectvolume="+volume);
            //mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC,AudioManager.ADJUST_SAME,0);
           // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC ),
                   // AudioManager.FLAG_PLAY_SOUND
                           // | AudioManager.FLAG_SHOW_UI);
            mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8,
                    0);
            //Settings.Global.putInt(context.getContentResolver(),"isfirst",1);
        } else if (intent.getIntExtra(STATE, 0) == 1) {
        // 连接耳机
            Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
            mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            //mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC,AudioManager.ADJUST_SAME,0);
           // Log.d("rr","connectvolume="+volume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8,
                    0);
        }
        }
        }
        }
