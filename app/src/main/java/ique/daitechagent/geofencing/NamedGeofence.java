package ique.daitechagent.geofencing;



import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;

import java.util.UUID;

public class NamedGeofence implements Comparable {

  // region Properties

  public String id; // Geofencing creating ID
  public String name; // Customer Name
  public double latitude;
  public double longitude;
  public float radius;

  // additional attributes
  public String keyid; // Node ID
  public String agentuuid; // Agent UUID

  public String agentName;
  public String agentregion;



  // end region

  // region Public

  public Geofence geofence() {
    id = UUID.randomUUID().toString();
    return new Geofence.Builder()
            .setRequestId(id)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build();
  }

  // endregion

  // region Comparable

  @Override
  public int compareTo(@NonNull Object another) {
    NamedGeofence other = (NamedGeofence) another;

     if (name.compareTo(other.name) == 0 &&  agentuuid.compareTo(other.agentuuid) == 0){
       return 0;
     }else{
       return 1;
     }
  }

  // endregion
}
