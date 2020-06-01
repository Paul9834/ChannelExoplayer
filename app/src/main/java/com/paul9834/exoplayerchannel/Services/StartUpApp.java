package com.paul9834.exoplayerchannel.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.paul9834.exoplayerchannel.Activities.CanalActivity;

public class StartUpApp extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, CanalActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
