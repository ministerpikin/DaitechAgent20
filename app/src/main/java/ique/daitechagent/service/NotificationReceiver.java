package ique.daitechagent.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "onReceive");

        /*
        ComponentName componentName = new ComponentName(context,LocationSharingService.class);
        intent.setComponent(componentName);
        context.stopService(intent);
        */

        ComponentName componentName = new ComponentName(context,LocationSharingService.class);
        intent.setComponent(componentName);
        //Intent intent = new Intent(CreateForegroundServiceActivity.this, MyForeGroundService.class);
        intent.setAction(LocationSharingService.ACTION_STOP_FOREGROUND_SERVICE);
        context.startService(intent);
    }

}