package ique.daitechagent.model;

import com.google.gson.annotations.SerializedName;


public class RegionQueryResult {

    @SerializedName("result")
    private boolean result;

    @SerializedName("region")
    private String region;

    @SerializedName("regionid")
    private int regionid;

    @SerializedName("errormsg")
    private String errormsg;

    public RegionQueryResult() {
        this.result = false;
        this.region = "";
        this.regionid = 0;
        this.errormsg = "";
    }

    public RegionQueryResult(boolean result, String region, int regionid, String errormsg) {
        this.result = result;
        this.region = region;
        this.regionid = regionid;
        this.errormsg = errormsg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getRegionid() {
        return regionid;
    }

    public void setRegionid(int regionid) {
        this.regionid = regionid;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }
}
