package com.example.claptrapper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.claptrapper.services.ClapService;

public class ClapReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("restartservice")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, ClapService.class));
                } else {
                    context.startService(new Intent(context, ClapService.class));
                }
            }
        }
    }
}
