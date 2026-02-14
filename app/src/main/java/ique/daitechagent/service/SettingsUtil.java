package ique.daitechagent.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

public class SettingsUtil {
    private static String TAG = SettingsUtil.class.getName();

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        for (RunningServiceInfo runningServiceInfo : ((ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            Log.d(TAG, String.format("Service:%s", runningServiceInfo.service.getClassName()));
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }
}
