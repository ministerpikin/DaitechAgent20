package ique.daitechagent.geofencing;



import androidx.annotation.NonNull;

public class NamedGeofenceNotification implements Comparable {

  // notification Properties
  public String agentuuid;    // Agent UUID
  public String keyid;        // Node ID
  public String agentname;    // Agent Name
  public String customername; // Customer Name
  public String status;       // Entering,  Leaving, Within a geofence
  public String timestamp;    // Time of event

  public String agentregion;  // Region of the agent

  // region Comparable

  @Override
  public int compareTo(@NonNull Object another) {
    NamedGeofenceNotification other = (NamedGeofenceNotification) another;

     if (keyid.compareTo(other.keyid) == 0 &&  agentuuid.compareTo(other.agentuuid) == 0){
       return 0;
     }else{
       return 1;
     }
  }

  // endregion
}
