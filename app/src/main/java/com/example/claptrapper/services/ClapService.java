package com.example.claptrapper.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.claptrapper.DetectorThread;
import com.example.claptrapper.MainActivity;
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
    DetectorThread detectorThread;
    public static boolean isAlarmTriggered = false;


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
        // Create a RemoteViews object for the custom layout
        RemoteViews customNotificationView = new RemoteViews(getPackageName(), R.layout.notification_layout);


        // Set up setOnClickPendingIntent for stop
        Intent yesIntent = new Intent(this, MainActivity.class);
        yesIntent.setAction("YES_ACTION");
        yesIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        yesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        yesIntent.putExtra("notificationIntent", true);
//        isIntent = true;
        PendingIntent customNotificationIntent = PendingIntent.getActivity(this, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        customNotificationView.setOnClickPendingIntent(R.id.stop, customNotificationIntent);
        // You can customize this notification as needed
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setCustomContentView(customNotificationView)
                .setAutoCancel(false)
                .setOngoing(true);

        return builder.build();
    }

}
