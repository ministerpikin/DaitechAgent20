package ique.daitechagent.versioncheckservice;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import ique.daitechagent.activities.MainActivity.VersionCheckServiceReceiver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VersionCheckService extends IntentService {
  private static final int REGISTRATION_TIMEOUT = 3000;
  public static final String REQUEST_STRING = "myRequest";
  public static final String RESPONSE_MESSAGE = "myResponseMessage";
  public static final String RESPONSE_STRING = "myResponse";
  private static final String TAG = "VersionCheckService";
  private static final int WAIT_TIMEOUT = 30000;
  private String url = null;

  public VersionCheckService() {
    super(TAG);
  }

  /* access modifiers changed from: protected */
  public void onHandleIntent(Intent intent) {
    String responseMessage;
    if (intent != null) {
      String action = intent.getAction();
      String requestString = intent.getStringExtra(REQUEST_STRING);
      String str = TAG;
      Log.v(str, requestString);
      String str2 = "";
      StringBuffer chaine = new StringBuffer(str2);
      try {
        HttpURLConnection connection = (HttpURLConnection) new URL(requestString).openConnection();
        connection.setRequestProperty("User-Agent", str2);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setConnectTimeout(3000);
        connection.connect();
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while (true) {
          String readLine = rd.readLine();
          String line = readLine;
          if (readLine == null) {
            break;
          }
          chaine.append(line);
        }
        responseMessage = chaine.toString();
      } catch (MalformedURLException e) {
        Log.w(str, e);
        responseMessage = e.getMessage();
      } catch (IOException e2) {
        Log.w(str, e2);
        responseMessage = e2.getMessage();
      }
      Intent broadcastIntent = new Intent();
      broadcastIntent.setAction(VersionCheckServiceReceiver.PROCESS_RESPONSE);
      broadcastIntent.addCategory("android.intent.category.DEFAULT");
      broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
      sendBroadcast(broadcastIntent);
    }
  }
}
