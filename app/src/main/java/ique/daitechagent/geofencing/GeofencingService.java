package ique.daitechagent.geofencing;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ique.daitechagent.common.Common;
import ique.daitechagent.utils.Utility;

public class GeofencingService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private static final String TAG = "GeofencingService";

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference dbrefGeofence;

    protected ArrayList<NamedGeofence> namedGeofenceArrayList = null;
    protected ArrayList<Geofence> mGeofenceList = null;
    protected GoogleApiClient mGoogleApiClient = null;
    protected PendingIntent mGeofencePendingIntent = null;

    public GeofencingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        namedGeofenceArrayList = new ArrayList<>();
        mGeofenceList = new ArrayList<>();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "Started Geofencing Service");

        mAuth = FirebaseAuth.getInstance();
        Common.userID =  mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        dbrefGeofence = database.getReference(Common.GEOFENCE_NODE);
        dbrefGeofence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot geofenceSnapShot: dataSnapshot.getChildren()){

                    NamedGeofence namedGeofence = geofenceSnapShot.getValue(NamedGeofence.class);
                    if (namedGeofence.agentuuid.equals(Common.userID)){

                        if (!namedGeofenceArrayList.contains(namedGeofence)) {
                            namedGeofenceArrayList.add(namedGeofence);
                        }
                    }

                }

                if (namedGeofenceArrayList.size() > 0){
                    populateGeofenceList();
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();

                    AddGeofences();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.v(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Bundle bundle = new Bundle();
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
            bundle.putString("status", "onConnected addedGeofences");
        }
        catch (SecurityException e) {
            Utility.ExceptionHandler(this,e);
            bundle.putString("status", "addGeofences Exception: "+e.getMessage());
        }
        catch (Exception e) {
            Utility.ExceptionHandler(this, e);
            bundle.putString("status", "addGeofences Exception: " + e.getMessage());
        }
        //if (receiver!=null)
        //    receiver.send(Activity.RESULT_OK, bundle);
        Log.v(TAG, "onConnected addedGeofences");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        /*
        if (receiver!=null) {
            StringBuilder sb=new StringBuilder("onConnectionSuspended: ");
            Bundle bundle = new Bundle();
            if (cause==CAUSE_SERVICE_DISCONNECTED)
                sb.append("Service Disconnected");
            else if (cause==CAUSE_NETWORK_LOST)
                sb.append("Network Lost");
            else
                sb.append(" cause: "+Integer.toString(cause));
            bundle.putString("status", sb.toString());
            receiver.send(Activity.RESULT_OK, bundle);
        }
         */

        StringBuilder sb=new StringBuilder("onConnectionSuspended: ");
        Bundle bundle = new Bundle();
        if (cause==CAUSE_SERVICE_DISCONNECTED)
            sb.append("Service Disconnected");
        else if (cause==CAUSE_NETWORK_LOST)
            sb.append("Network Lost");
        else
            sb.append(" cause: "+Integer.toString(cause));

        Log.v(TAG, sb.toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //if (receiver!=null) {
        //    Bundle bundle = new Bundle();
        //    bundle.putString("status", "onConnectionFailed " + result.getErrorMessage());
        //    receiver.send(Activity.RESULT_CANCELED, bundle);
        //}
        Log.v(TAG, "onConnectionFailed " + connectionResult.getErrorMessage());
    }


    private void populateGeofenceList(){
        //.setRequestId( namedGeofence.name.replace(" ", "_") + "_"+ namedGeofence.keyid)
        for ( NamedGeofence namedGeofence : namedGeofenceArrayList) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(namedGeofence.keyid)
                    .setCircularRegion(
                            namedGeofence.latitude,
                            namedGeofence.longitude,
                            namedGeofence.radius
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness (0)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |  Geofence.GEOFENCE_TRANSITION_EXIT )
                    .build());
        }

        /*
        if (receiver!=null) {
            Bundle bundle = new Bundle();
            bundle.putString("status", "populateGeofenceList");
            receiver.send(Activity.RESULT_OK, bundle);
        }
         */

        Log.v(TAG, "populateGeofenceList");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void AddGeofences(){
        if (mGoogleApiClient.isConnected()) {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            } catch (SecurityException e) {
                Utility.ExceptionHandler(this,e);
            }
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder =new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent()
    {
        Bundle bundle = new Bundle();
        String status;
        if (mGeofencePendingIntent == null) {
            mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, GeofenceReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

            bundle.putString("status", "getGeofencePendingIntent Created");
            status = "getGeofencePendingIntent Created";
        }
        else {
            bundle.putString("status", "getGeofencePendingIntent Exists");
            status = "getGeofencePendingIntent Exists";
        }

        //if (receiver!=null)
        //    receiver.send(Activity.RESULT_OK, bundle);

        Log.v(TAG, status);
        return mGeofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, status.getStatusCode());
        }
    }
}
