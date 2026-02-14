package ique.daitechagent.model;

import java.io.Serializable;

public class AgentTracking implements Serializable {
    private boolean available;
    private double latitude;
    private double longitude;
    private String userid;

    public AgentTracking() {
    }

    public AgentTracking(double latitude, double longitude, String userid, boolean available) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userid = userid;
        this.available = available;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude2) {
        this.latitude = latitude2;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
