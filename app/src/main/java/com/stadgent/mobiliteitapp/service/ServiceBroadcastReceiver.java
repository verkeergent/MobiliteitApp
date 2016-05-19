package com.stadgent.mobiliteitapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by floriangoeteyn on 11-May-16.
 */
public class ServiceBroadcastReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, PushService.class);
        context.startService(startServiceIntent);
    }
}