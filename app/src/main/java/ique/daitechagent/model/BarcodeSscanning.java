package ique.daitechagent.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class BarcodeSscanning {
    private String barcodescanninguuid;
    private String customername;
    private String longDate;
    private String scandate;
    private ArrayList<Scannedproduct> scannedproducts;
    private long timestamp;

    public BarcodeSscanning() {
        String str = "";
        this.customername = str;
        this.scandate = str;
        this.timestamp = new Date().getTime() * -1;
        this.barcodescanninguuid = str;
        this.longDate = str;
        this.scannedproducts = new ArrayList<>();
    }

    public BarcodeSscanning(String customername2, String scandate2, long timestamp2, String barcodescanninguuid2, String longDate2, ArrayList<Scannedproduct> scannedproducts2) {
        this.customername = customername2;
        this.scandate = scandate2;
        this.timestamp = timestamp2;
        this.barcodescanninguuid = barcodescanninguuid2;
        this.longDate = longDate2;
        this.scannedproducts = scannedproducts2;
    }

    public String getCustomername() {
        return this.customername;
    }

    public void setCustomername(String customername2) {
        this.customername = customername2;
    }

    public String getScandate() {
        return this.scandate;
    }

    public void setScandate(String scandate2) {
        this.scandate = scandate2;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp2) {
        this.timestamp = timestamp2;
    }

    public String getBarcodescanninguuid() {
        return this.barcodescanninguuid;
    }

    public void setBarcodescanninguuid(String barcodescanninguuid2) {
        this.barcodescanninguuid = barcodescanninguuid2;
    }

    public ArrayList<Scannedproduct> getScannedproducts() {
        return this.scannedproducts;
    }

    public void setScannedproducts(ArrayList<Scannedproduct> scannedproducts2) {
        this.scannedproducts = scannedproducts2;
    }

    public int getScancount() {
        return this.scannedproducts.size();
    }

    public int getScannedProjectCount() {
        Iterator i = this.scannedproducts.iterator();
        int count = 0;
        while (i.hasNext()) {
            count += ((Scannedproduct) i.next()).getQuantity();
        }
        return count;
    }

    public String getLongDate() {
        return this.longDate;
    }

    public void setLongDate(String longDate2) {
        this.longDate = longDate2;
    }
}
