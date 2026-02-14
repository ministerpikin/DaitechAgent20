package ique.softwareupdate.update;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class CheckUpdateTask extends AsyncTask<Void, Void, String> {
	
	private ProgressDialog dialog;
    private Context mContext;
    private int mType;
    private boolean mShowProgressDialog;
    private static final String url = Constants.UPDATE_URL;
    private String userEmail;
    
    CheckUpdateTask(Context context, int type, boolean showProgressDialog, String userEmail) {

        this.mContext = context;
        this.mType = type;
        this.mShowProgressDialog = showProgressDialog;
        this.userEmail = userEmail;

    }
    
    protected void onPreExecute() {
        if (mShowProgressDialog) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Update");
            dialog.show();
        }
    }
    
    @Override
    protected void onPostExecute(String result) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (!TextUtils.isEmpty(result)) {
            parseJson(result);
        }
    }

	private void parseJson(String result) {
		// TODO Auto-generated method stub{    
        try {

            JSONObject obj = new JSONObject(result);

            String updateMessage = obj.getString(Constants.APK_UPDATE_CONTENT);
            String apkUrl = obj.getString(Constants.APK_DOWNLOAD_URL);
            int apkCode = obj.getInt(Constants.APK_VERSION_CODE);
            int versionCode = AppUtils.getVersionCode(mContext);

            boolean success = obj.getBoolean(Constants.APK_VERSION_STATUS);

            if (success) {
                if (apkCode > versionCode) {
                    if (mType == Constants.TYPE_NOTIFICATION) {
                        showNotification(mContext, updateMessage, apkUrl);
                    } else if (mType == Constants.TYPE_DIALOG) {
                        showDialog(mContext, updateMessage, apkUrl);
                    }
                } else if (mShowProgressDialog) {
                    Toast.makeText(mContext, "Show Progress Dialog", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(mContext, "Server Internal Error.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e(Constants.TAG, "parse json error");
        }
    }
		
	private void showDialog(Context context, String content,
			String apkUrl) {
		// TODO Auto-generated method stub
		UpdateDialog.show(context, content, apkUrl);
	}

	private void showNotification(Context context, String content,
			String apkUrl) {
		// TODO Auto-generated method stub{
        Intent myIntent = new Intent(context, DownloadService.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.putExtra(Constants.APK_DOWNLOAD_URL, apkUrl);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int smallIcon = context.getApplicationInfo().icon;
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context, "agent_download_system")
                .setTicker("Daitech Agent")
                .setContentTitle("Update Available")
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setContentIntent(pendingIntent);//.build();
        //Notification notify = mBuilder.build();

        //notify.flags = Notification.FLAG_AUTO_CANCEL;
        //notify.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )// Android.OS.BuildVersionCodes.O)
        {
            String channelId = "agent_download_system";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Daitech Workforce Download",
                    NotificationManager.IMPORTANCE_DEFAULT);//Android.App.NotificationImportance.Default);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        //Notification notification = mBuilder.build();
        Notification notify = mBuilder.build();
        //notify.flags |= Notification.FLAG_ONGOING_EVENT;
        //notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notify.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notify);


        //notificationManager.notify(0, notify);
    }
		
	

	

	@Override
	protected String doInBackground(Void... args) {
		// TODO Auto-generated method stub
		return HttpUtils.get(url+ "?username="+userEmail);
	}
	
	

}
