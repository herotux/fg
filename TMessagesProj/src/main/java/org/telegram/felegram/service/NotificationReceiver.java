package org.telegram.felegram.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by reza on 4/19/2016 AD.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("rec", "onReceive");
        long now = System.currentTimeMillis();
        SharedPreferences preferences = context.getSharedPreferences("feleconfig", Activity.MODE_PRIVATE);

        long last = preferences.getLong("service_started", 0);
        if(now - last > 10000) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("service_started", now).commit();

            Intent service = new Intent(context, NotificationService.class);
            intent.putExtra("type", "notif");
            context.startService(service);
        }
    }




}
