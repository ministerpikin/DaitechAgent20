package ique.daitechagent.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NIMCPC on 16-Feb-18.
 */

public class Report {
    private String address;
    private String customerDisplayName;
    private String customerName;
    private String customerNumber;
    private String customerReport;
    private String customerState;
    private double lat;
    private double lng;
    private String ownerid;
    private String reportDate;
    private String reportId;

    // This is the ultimate final data search is ama try
    String searchDate;
    private long timestamp;

	/*
    //For Ordering Stuff when viewing data for agents from admin app
    private int mDate; // eg 1 - 30
    private String mDay; // eg Su - Sa
    private int mMonth; // 1 - 12
    private int mYear; // eg 2018

    String[] strDays = new String[] { "Su", "Mo", "Tu", "We", "Th", "Fri", "Sa" };
    */


    public Report() {
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String customerName2) {
        this.customerName = customerName2;
    }

    public String getCustomerState() {
        return this.customerState;
    }

    public void setCustomerState(String customerState2) {
        this.customerState = customerState2;
    }

    public String getCustomerReport() {
        return this.customerReport;
    }

    public void setCustomerReport(String customerReport2) {
        this.customerReport = customerReport2;
    }

    public double getLng() {
        return this.lng;
    }

    public void setLng(double lng2) {
        this.lng = lng2;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat2) {
        this.lat = lat2;
    }

    public String getReportId() {
        return this.reportId;
    }

    public void setReportId(String reportId2) {
        this.reportId = reportId2;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address2) {
        this.address = address2;
    }

    public String getReportDate() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a");
        if (this.reportDate == null) {
            this.reportDate = ft.format(dNow);
        }
        return this.reportDate;
    }

    public void setReportDate(String reportDate2) {
        this.reportDate = reportDate2;
    }

    public String getSearchDate() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        if (this.searchDate == null) {
            this.searchDate = ft.format(dNow);
        }
        return this.searchDate;
    }

    public void setSearchDate(String searchDate2) {
        this.searchDate = searchDate2;
    }

    public String getOwnerid() {
        return this.ownerid;
    }

    public void setOwnerid(String ownerid2) {
        this.ownerid = ownerid2;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp2) {
        this.timestamp = timestamp2;
    }

    public String getCustomerNumber() {
        return this.customerNumber;
    }

    public void setCustomerNumber(String customerNumber2) {
        this.customerNumber = customerNumber2;
    }

    public String getCustomerDisplayName() {
        return this.customerDisplayName;
    }

    public void setCustomerDisplayName(String customerDisplayName2) {
        this.customerDisplayName = customerDisplayName2;
    }
}
