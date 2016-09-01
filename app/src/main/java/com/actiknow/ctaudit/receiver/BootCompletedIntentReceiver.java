package com.actiknow.ctaudit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.actiknow.ctaudit.service.LocationService;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {
        Log.e ("karman", "in boot completed");
        if ("android.intent.action.BOOT_COMPLETED".equals (intent.getAction ())) {
            Intent pushIntent = new Intent (context, LocationService.class);
            context.startService (pushIntent);
        }
    }
}