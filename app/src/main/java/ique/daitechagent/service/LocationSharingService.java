package ique.daitechagent.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ique.daitechagent.R;
import ique.daitechagent.activities.MainActivity;
import ique.daitechagent.model.AgentTracking;

import static com.google.android.gms.location.LocationServices.API;


public class LocationSharingService extends Service implements
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


  private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
  public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
  public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
  public static final String ACTION_PAUSE = "ACTION_PAUSE";
  public static final String ACTION_PLAY = "ACTION_PLAY";


  private static String FIREBASE_TRACKING_NODE = null;

  FirebaseAuth firebaseAuth;
  FirebaseDatabase firebaseDatabase;
  DatabaseReference drefAgentTracking;
  LatLng latLng = null;

  FusedLocationProviderClient mFusedLocationClient = null;
  LocationRequest mLocationRequest = null;
  LocationCallback mLocationCallback = null;

  private GoogleApiClient mGoogleApiClient = null;
  private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();
  private AgentTracking agentTracking = null;
  private static final String TAG = "LocationSharingService";
  public static final String AGENT_TRACKING_NODE = "tracking";

  double currentLongitude;
  double currentLatitude;

  private NotificationManager mNotificationManager;

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    // throw new UnsupportedOperationException("Not yet implemented");
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    // Check if the intent is valid
    if (intent != null) {
      Bundle bundle = intent.getExtras();
      String action = intent.getAction();

      switch (action) {
        case ACTION_START_FOREGROUND_SERVICE:

          /**
           * Extracting paramters passed from
           * SignInActivity
           */
          FIREBASE_TRACKING_NODE = bundle.getString("userid");

          /**
           * Deserializing instances passed from
           * SignInActivity
           */
          agentTracking = (AgentTracking) bundle.getSerializable(FIREBASE_TRACKING_NODE);


          /**
           * Lets start the foreground service
           */
          startForegroundService();


          /**
           * Set up the API client for Places API
           */
          mGoogleApiClient = new GoogleApiClient.Builder(this)
                  .addConnectionCallbacks(this)
                  .addOnConnectionFailedListener(this)
                  .addApi(API)
                  .build();
          mGoogleApiClient.connect();

          firebaseDatabase = FirebaseDatabase.getInstance();
          drefAgentTracking = firebaseDatabase.getReference(AGENT_TRACKING_NODE);

          agentTracking.setAvailable(true);
          drefAgentTracking.child(FIREBASE_TRACKING_NODE).setValue(agentTracking);

          break;

        case ACTION_STOP_FOREGROUND_SERVICE:
          stopForegroundService();
          Log.d(TAG, "Foreground service is stopped.");
          break;
        case ACTION_PLAY:
          Log.d(TAG, "You click Play button.");
          break;
        case ACTION_PAUSE:
          Log.d(TAG, "You click Pause button.");
          break;
      }



    }

    Log.d(TAG, "onStartCommand");

    //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
    return START_NOT_STICKY;
  }

  /* Used to build and start foreground service. */
  private void startForegroundService() {
    Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");


    Intent stopSharingIntent = new Intent(this, NotificationReceiver.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(agentTracking.getUserid(), agentTracking);
    bundle.putString("userid", agentTracking.getUserid());
    stopSharingIntent.putExtras(bundle);

    //PendingIntent pStopIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) System.currentTimeMillis(), stopSharingIntent, 0);

    PendingIntent pStopIntent;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      pStopIntent = PendingIntent.getBroadcast(getApplicationContext(),
              (int) System.currentTimeMillis(), stopSharingIntent, PendingIntent.FLAG_MUTABLE);
//      pendingIntent = PendingIntent.getActivity(this,
//              0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

    }else {
      pStopIntent = PendingIntent.getBroadcast(getApplicationContext(),
              (int) System.currentTimeMillis(), stopSharingIntent, 0);

    }
    NotificationCompat.Action action = new NotificationCompat.Action.Builder(android.R.drawable.screen_background_light_transparent, "Stop Sharing", pStopIntent).build();

    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(getApplicationContext(), "agent_tracking_system");

    Intent ii = new Intent(getApplicationContext(), MainActivity.class);
    //PendingIntent pendingIntent = PendingIntent.getActivity(this,  (int) System.currentTimeMillis(), ii, 0);

    PendingIntent pendingIntent;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

      pendingIntent = PendingIntent.getActivity(this,  (int) System.currentTimeMillis(), ii, PendingIntent.FLAG_MUTABLE);

    }else {
      pendingIntent = PendingIntent.getActivity(this,  (int) System.currentTimeMillis(), ii, 0);

    }

    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
    bigText.bigText("Location is being monitored by regional Supervisors and HQ.");
    bigText.setBigContentTitle("Agent Workforce Monitoring System");
    bigText.setSummaryText("Monitor");

    mBuilder.setContentIntent(pendingIntent);
    mBuilder.addAction(action);
    mBuilder.setSmallIcon(R.drawable.ic_daitech_notification_logo);
    mBuilder.setContentTitle("Agent Tracking System");
    mBuilder.setContentText("Location is being Shared");
    mBuilder.setPriority(Notification.PRIORITY_MAX);
    mBuilder.setStyle(bigText);

    // Make head-up notification.
    mBuilder.setFullScreenIntent(pendingIntent, true);

    mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // === Removed some obsoletes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )// Android.OS.BuildVersionCodes.O)
    {
      String channelId = "agent_tracking_channel";
      NotificationChannel channel = new NotificationChannel(
              channelId,
              "Daitech Workforce Tracking System",
              NotificationManager.IMPORTANCE_DEFAULT);//Android.App.NotificationImportance.Default);
      mNotificationManager.createNotificationChannel(channel);
      mBuilder.setChannelId(channelId);
    }

    Notification notification = mBuilder.build();
    notification.flags |= Notification.FLAG_ONGOING_EVENT;
    mNotificationManager.notify(0, notification);

    try {
      // Set forground stuff
      int ONGOING_NOTIFICATION_ID = 1001;
      startForeground(ONGOING_NOTIFICATION_ID, notification);
    }catch(Exception ex){
      Log.e(TAG, ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void stopForegroundService()
  {
    Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

    agentTracking.setAvailable(false);
    drefAgentTracking.child(FIREBASE_TRACKING_NODE).setValue(agentTracking);

    // Stop Location Updates
    stopLocationUpdates();

    // Stop foreground service and remove the notification.
    stopForeground(true);

    // Stop the foreground service.
    stopSelf();
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {

    try {

      mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
      mFusedLocationClient.getLastLocation()
              .addOnSuccessListener(  new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                  if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    if(agentTracking != null){
                      agentTracking.setLatitude(currentLatitude);
                      agentTracking.setLongitude(currentLongitude);

                      drefAgentTracking.child(FIREBASE_TRACKING_NODE).setValue(agentTracking);
                    }
                  }
                }
              });


      /**
       * setting the frequency of
       * location requestor, calling
       * for every 500ms and at fastestinterval of
       * 300ms
       */
      mLocationRequest = new LocationRequest();
      mLocationRequest.setInterval(500); //5 seconds
      mLocationRequest.setFastestInterval(300); //3 seconds
      //mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
      mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

      mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

          for (Location location : locationResult.getLocations()) {
            // Update UI with location data
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            if(agentTracking != null){
              agentTracking.setLatitude(currentLatitude);
              agentTracking.setLongitude(currentLongitude);

              drefAgentTracking.child(FIREBASE_TRACKING_NODE).setValue(agentTracking);
            }
          }

        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
          super.onLocationAvailability(locationAvailability);
        }
      };

      startLocationUpdates();

    } catch (SecurityException ex) {
      ex.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onLocationChanged(Location location) {

  }

  @Override
  public void onCreate() {
    //super.onCreate();
    Log.d(TAG, "onCreate called: Service created");


  }


  @Override
  public void onDestroy() {

    Log.d(TAG, "onDestroy");


    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    agentTracking.setAvailable(false);
    drefAgentTracking.child(FIREBASE_TRACKING_NODE).setValue(agentTracking);

    nm.cancelAll();
    stopLocationUpdates();

    super.onDestroy();
  }

  protected void stopLocationUpdates() {
    //FusedLocationApi.removeLocationUpdates(
    //        mGoogleApiClient, this);
    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
  }

  private void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
  }
}
