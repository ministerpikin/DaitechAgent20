package ique.daitechagent.model;

/**
 * Created by NIMCPC on 20-Feb-18.
 */
public class Dailyschedule {
    private String visitlocation;
    private String visitDate;
    private String visitPeriod;
    private String dailyPlanID;

    private String description;
    private String customername;

    // Sorting Issues
    private long timestamp;

    // Checking In and Checking Out
    private long checkin;
    private long checkout;

    // add region
    private String userregion;

    public Dailyschedule(){}

    public Dailyschedule(String visitlocation, String visitDate, String visitPeriod, String dailyPlanID, String description, String customername, long timestamp, long checkin, long checkout, String userregion) {
        this.visitlocation = visitlocation;
        this.visitDate = visitDate;
        this.visitPeriod = visitPeriod;
        this.dailyPlanID = dailyPlanID;
        this.description = description;
        this.customername = customername;
        this.timestamp = timestamp;
        this.checkin = checkin;
        this.checkout = checkout;
        this.userregion = userregion;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public String getVisitlocation() {
        return visitlocation;
    }

    public String getVisitPeriod() {
        return visitPeriod;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public void setVisitlocation(String visitlocation) {
        this.visitlocation = visitlocation;
    }

    public void setVisitPeriod(String visitPeriod) {
        this.visitPeriod = visitPeriod;
    }

    public String getDailyPlanID() {
        return dailyPlanID;
    }

    public void setDailyPlanID(String dailyPlanID) {
        this.dailyPlanID = dailyPlanID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getCheckin() {
        return checkin;
    }

    public void setCheckin(long checkin) {
        this.checkin = checkin;
    }

    public long getCheckout() {
        return checkout;
    }

    public void setCheckout(long checkout) {
        this.checkout = checkout;
    }

    public String getUserregion() {
        return userregion;
    }

    public void setUserregion(String userregion) {
        this.userregion = userregion;
    }
}
