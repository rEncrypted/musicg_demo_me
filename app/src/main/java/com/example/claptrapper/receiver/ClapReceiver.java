package com.example.claptrapper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.claptrapper.services.ClapService;
import com.example.claptrapper.utils.Constants;

public class ClapReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("restartservice")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent i = new Intent(context, ClapService.class);
                    i.setAction(Constants.STARTFOREGROUND_ACTION);
                    context.startForegroundService(i);
                } else {
                    Intent i = new Intent(context, ClapService.class);
                    i.setAction(Constants.STARTFOREGROUND_ACTION);
                    context.startService(i);
                }
            }
        }
    }
}
