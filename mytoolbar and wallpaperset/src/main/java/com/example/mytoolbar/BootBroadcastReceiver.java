package com.example.mytoolbar;

import static android.app.WallpaperManager.*;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;


public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_BOOT)) { //开机启动完成后，要做的事情
            Log.i(TAG, "BootBroadcastReceiver onReceive(), Do thing!");
            Drawable drawable = context.getResources().getDrawable(R.drawable.default_lock_wallpaper);
            WallpaperManager wallpaperManager =WallpaperManager.getInstance(context);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                try{
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                }
                catch (IOException e){

                }

            Log.i(TAG ," -- changed and reset default system lockwallpaper......");
        }
    }
}
