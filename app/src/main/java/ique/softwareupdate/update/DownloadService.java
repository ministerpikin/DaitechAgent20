package ique.softwareupdate.update;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import ique.daitechagent.BuildConfig;

public class DownloadService extends IntentService {
	

    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private static final String TAG = "DownloadService";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    //private Builder mBuilder;
    private NotificationCompat.Builder mBuilder;


    public DownloadService() {
        super("DownloadService");
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //mBuilder = new Builder(this);

         mBuilder =
                new NotificationCompat.Builder(this, "agent_download_system");

        String appName = getString(getApplicationInfo().labelRes);
        int icon = getApplicationInfo().icon;

        mBuilder.setContentTitle(appName).setSmallIcon(icon);
        String urlStr = intent.getStringExtra(Constants.APK_DOWNLOAD_URL);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = StorageUtils.getCacheDirectory(this);
            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
            File apkFile = new File(dir, apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);

                if (progress != oldProgress) {
                    updateProgress(progress);
                }
                oldProgress = progress;
            }


            installAPk(apkFile);



            mNotifyManager.cancel(NOTIFICATION_ID);

        } catch (Exception e) {
            Log.e(TAG, "download apk file error",e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
	}
	 private void updateProgress(int progress) {

	        mBuilder.setContentText("Downloaded: " +  progress + "%").setProgress(100, progress, false);

	        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
	        mBuilder.setContentIntent(pendingintent);
	        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
	    }


	    private void installAPk(File apkFile) {
	        Intent intent = new Intent(Intent.ACTION_VIEW);

	        try {
	            String[] command = {"chmod", "777", apkFile.toString()};
	            ProcessBuilder builder = new ProcessBuilder(command);
	            builder.start();
	        } catch (IOException ignored) {
	            Log.e("DownloadService", ignored.getMessage(), ignored);
	        } catch (Exception ex){
	            Log.e(TAG, ex.getMessage(), ex);
            }

            if (Build.VERSION.SDK_INT >= 24) { //7.0以上 适配
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri apkUri = GenericFileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", apkFile);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }

            /*
	        //intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = GenericFileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", apkFile);

            intent.setDataAndType(uri, "application/vnd.android.package-archive");
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            */
	        startActivity(intent);

	    }
}
