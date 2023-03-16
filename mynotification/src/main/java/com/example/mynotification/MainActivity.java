package com.example.mynotification;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
private NotificationManager manager;
private Notification notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSystemService(NOTIFICATION_SERVICE)获取NotificationManager的一个实例，不能直接实例化
        manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //Android8.0后引入通知渠道channelid，可设置通知重要程度，8.0后必须加，否则无法显示
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel("leo","测试通知",NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        Intent intent=new Intent(this,MainActivity2.class);
        //PendingIntent决定点击通知后的跳转意图

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
             pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
             pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        //PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);

        //setAutoCancel设置点击通知后自动清除通知
         notification = new NotificationCompat.Builder(this, "leo")
                .setContentTitle("官方通知")
                .setContentText("世界那么大，想去走走吗")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                 .setContentIntent(pendingIntent)
                 .setAutoCancel(true)
                .build();
    }

    public void sendNotification(View view) {
        //发送通知
        manager.notify(1,notification);
    }

    public void cancelNotification(View view) {
        manager.cancel(1);
    }
}