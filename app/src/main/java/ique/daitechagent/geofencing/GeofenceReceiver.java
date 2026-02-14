package ique.daitechagent.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ique.daitechagent.common.Common;
import ique.daitechagent.utils.DateUtils;


public class GeofenceReceiver extends BroadcastReceiver {

    private final static String TAG = "GeofenceReceiver";

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference dbrefGeofence;
    DatabaseReference dbrefGeofenceNotification;

    @Override
    public void onReceive(Context context, Intent intent) {



        StringBuilder sb = new StringBuilder();
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event != null) {
            if (event.hasError()){
                sb.append("geoFenceEvent has error: " + GeofenceStatusCodes.getStatusCodeString(event.getErrorCode()));
                Log.v(TAG, "geoFenceEvent has error: " + GeofenceStatusCodes.getStatusCodeString(event.getErrorCode()));
            }else{

                int transition = event.getGeofenceTransition();
                if (transition ==
                        Geofence.GEOFENCE_TRANSITION_ENTER
                        || transition == Geofence.GEOFENCE_TRANSITION_DWELL
                        || transition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                    List<String> geofenceIds = new ArrayList<>();

                    for (Geofence geofence : event.getTriggeringGeofences()) {
                        geofenceIds.add(geofence.getRequestId());

                        updateNotification(geofence.getRequestId(), transition);
                    }
                }
            }
        }else{
            Log.v(TAG, "geoFenceEvent has error: value of event is null");
        }

        /*
        StringBuilder sb=new StringBuilder();
        if (event.hasError()) {

            sb.append("geoFenceEvent has error: " + GeofenceStatusCodes.getStatusCodeString(event.getErrorCode()));

        } else {

            sb.append("Location ");
            for (Geofence geofence : event.getTriggeringGeofences()) {
                sb.append(geofence.getRequestId()+" ");
            }
            if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_DWELL)
                sb.append("DWELLL");
            else if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER)
                sb.append("ENTER");
            else if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_EXIT)
                sb.append("EXIT");
            else if (event.getGeofenceTransition() == Geofence.NEVER_EXPIRE)
                sb.append("NEVER EXPIRE");
            else
                sb.append("UNKNOWN EVENT:" + Integer.toString(event.getGeofenceTransition()));

            //updateNotification(geofence.getRequestId(), )
        }

        final Toast toast;
        toast=Toast.makeText(context, sb.toString(),Toast.LENGTH_LONG);
        toast.show();

        new CountDownTimer(9000, 1000)
        {
            public void onTick(long millisUntilFinished) {toast.show();}
            public void onFinish() {toast.show();}

        }.start();
         */
    }


    private void updateNotification(String geoid, int status){

        Log.v("GeofenceReceiver", "GeofenceReceiver updateNotification");

        final String statusDescription;
        mAuth = FirebaseAuth.getInstance();
        Common.userID =  mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        dbrefGeofence = database.getReference(Common.GEOFENCE_NODE);
        dbrefGeofenceNotification = database.getReference(Common.GEOFENCE_NOTIFICATION_NODE);

        if (status == Geofence.GEOFENCE_TRANSITION_DWELL)
            statusDescription = "Agent is within  the customer area";//sb.append("DWELLL");
        else if (status == Geofence.GEOFENCE_TRANSITION_ENTER)
            statusDescription = "Agent just entered the customer area";//sb.append("ENTER");
        else if (status == Geofence.GEOFENCE_TRANSITION_EXIT)
            statusDescription = "Agent has left the customer area";//sb.append("EXIT");
        else if (status == Geofence.NEVER_EXPIRE)
            statusDescription = "Geofence would not expire";//sb.append("NEVER EXPIRE");
        else
            statusDescription = "Unknown status of agent with respect to the customer area";//sb.append("UNKNOWN EVENT:" + Integer.toString(event.getGeofenceTransition()));


        Query query = dbrefGeofence.orderByChild("keyid").equalTo(geoid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot geoSnapShot : dataSnapshot.getChildren()){

                    NamedGeofence namedGeofence = geoSnapShot.getValue(NamedGeofence.class);

                    NamedGeofenceNotification namedGeofenceNotification = new NamedGeofenceNotification();
                    namedGeofenceNotification.agentname = namedGeofence.agentName;
                    namedGeofenceNotification.agentuuid = namedGeofence.agentuuid;
                    namedGeofenceNotification.customername = namedGeofence.name;
                    namedGeofenceNotification.keyid = namedGeofence.keyid;
                    namedGeofenceNotification.status = statusDescription;
                    namedGeofenceNotification.agentregion = namedGeofence.agentregion;
                    namedGeofenceNotification.timestamp = DateUtils.formatLongDateTime(DateUtils.getTimeStamp());

                    String keyid = namedGeofence.keyid;

                    dbrefGeofenceNotification
                            .child(Common.REPORT_COMMENT_NOTIFICATION_MANAGER_NODE)
                            .child(keyid)
                            .setValue(namedGeofenceNotification);

                    String userRegion = namedGeofence.agentregion
                            .replace(" ", "")
                            .replace("[", "")
                            .replace("]","");

                    dbrefGeofenceNotification
                            .child(Common.REPORT_COMMENT_NOTIFICATION_SUPERVISOR_NODE)
                            .child(userRegion.toLowerCase())
                            .child(keyid)
                            .setValue(namedGeofenceNotification);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
