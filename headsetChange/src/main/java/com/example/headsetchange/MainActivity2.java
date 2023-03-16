package com.example.headsetchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;

public class MainActivity2 extends AppCompatActivity {
    private AudioManager mAudioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC,AudioManager.ADJUST_SAME,0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}