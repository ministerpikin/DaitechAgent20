package ique.softwareupdate.update;


import android.content.Context;
import android.util.Log;

public class UpdateChecker {
	public static void checkForDialog(Context context, String userEmail) {
        if (context != null) {
            new CheckUpdateTask(context, Constants.TYPE_DIALOG, true, userEmail).execute();
        } else {
            Log.e(Constants.TAG, "The arg context is null");
        }
    }


    public static void checkForNotification(Context context, String userEmail) {
        if (context != null) {
            new CheckUpdateTask(context, Constants.TYPE_NOTIFICATION, false, userEmail).execute();
        } else {
            Log.e(Constants.TAG, "The arg context is null");
        }

    }

}
