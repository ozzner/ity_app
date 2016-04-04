package com.italkyou.receivers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.italkyou.services.ItalkYouService;

/**
 * Created by RenzoD on 04/03/2016.
 */
public class ItalkYouWakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null)
            return;


        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            return;

        startWakefulService(context, intent.setComponent(
                new ComponentName(context.getPackageName(), ItalkYouService.class.getName())));
    }
}
