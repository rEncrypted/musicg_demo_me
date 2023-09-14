package com.example.claptrapper.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.claptrapper.DetectorThread;
import com.example.claptrapper.R;
import com.example.claptrapper.RecorderThread;
import com.example.claptrapper.receiver.ClapReceiver;

public class ClapService extends Service {
    private static final int NOTIFICATION_ID = 501;
    private static final String CHANNEL_ID = "channel-02";


    //notification manager
    NotificationManager notificationManager;

    ///
    RecorderThread recorderThread;
    DetectorThread detectorThread;;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ClapReceiver.class);
        this.sendBroadcast(broadcastIntent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
        Notification notification = createNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "channel_name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID, notification);

        //
        recorderThread = new RecorderThread();
        recorderThread.start();
        detectorThread = new DetectorThread(recorderThread, this);
        detectorThread.start();

        return START_STICKY;
    }

    private Notification createNotification() {
        // You can customize this notification as needed
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ClapTrapper")
                .setContentText("Running in the background")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(false)
                .setOngoing(true);

        return builder.build();
    }
}
