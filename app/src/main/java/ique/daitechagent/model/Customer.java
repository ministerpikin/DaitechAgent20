package ique.daitechagent.model;

public class Customer {

    private String customernumber;
    private String customername;
    private String address;
    private String phoneno;
    private String customerid;
    private double lng;
    private double lat;
    private long timestamp;

    private long reportcount;
    private int customerrating;

    private String contactperson;
    private int customer_auto_number;
    private String email;

    public Customer() {

        this.customernumber = "";
        this.customername = "";
        this.address = "";
        this.phoneno = "";
        this.customerid = "";
        this.lng = 0;
        this.lat = 0;
        this.timestamp = 0;
        this.reportcount = 0;
        this.customerrating = 0;
        this.contactperson = "";
        this.customer_auto_number = 0;
        this.email = "";
    }

    public Customer(String customernumber, String customername, String address, String phoneno, String customerid, double lng, double lat, long timestamp, long reportcount, int customerrating, String contactperson, int customer_auto_number, String email) {
        this.customernumber = customernumber;
        this.customername = customername;
        this.address = address;
        this.phoneno = phoneno;
        this.customerid = customerid;
        this.lng = lng;
        this.lat = lat;
        this.timestamp = timestamp;
        this.reportcount = reportcount;
        this.customerrating = customerrating;
        this.contactperson = contactperson;
        this.customer_auto_number = customer_auto_number;
        this.email = email;
    }

    public String getCustomernumber() {
        return customernumber;
    }

    public void setCustomernumber(String customernumber) {
        this.customernumber = customernumber;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCustomerrating() {
        return customerrating;
    }

    public void setCustomerrating(int customerrating) {
        this.customerrating = customerrating;
    }

    public long getReportcount() {
        return reportcount;
    }

    public void setReportcount(long reportcount) {
        this.reportcount = reportcount;
    }

    public String getContactperson() {
        return contactperson;
    }

    public void setContactperson(String contactperson) {
        this.contactperson = contactperson;
    }

    public int getCustomer_auto_number() {
        return customer_auto_number;
    }

    public void setCustomer_auto_number(int customer_auto_number) {
        this.customer_auto_number = customer_auto_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
