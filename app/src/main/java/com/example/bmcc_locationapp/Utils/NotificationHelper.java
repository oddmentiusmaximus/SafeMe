package com.example.bmcc_locationapp.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.bmcc_locationapp.R;

import androidx.annotation.RequiresApi;

public class NotificationHelper extends ContextWrapper {
    private static  final  String ESHU_BAL_ID="com.example.bmcc_locationapp";
private static  final  String ESHU_BAL_NAME="BMCC_LOCATION_APP";
private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel eshuBalChannel=new NotificationChannel(ESHU_BAL_ID,
                ESHU_BAL_NAME,NotificationManager.IMPORTANCE_DEFAULT);

        eshuBalChannel.enableLights(false);
        eshuBalChannel.enableVibration(true);
        eshuBalChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(eshuBalChannel);
    }

    public NotificationManager getManager() {
        if(manager==null){
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealtimeTrackNotification(String title, String content, Uri defaultSound) {
    return new Notification.Builder(getApplicationContext(),ESHU_BAL_ID).setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText(content)
            .setSound(defaultSound)
            .setAutoCancel(false);
    }
}
