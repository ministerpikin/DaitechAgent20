package ique.daitechagent.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CustomerResult {

    @SerializedName("result")
    private boolean result;

    @SerializedName("customers")
    private ArrayList<Customer> customers;

    @SerializedName("errormsg")
    private String errormsg;

    @SerializedName("recommenddelete")
    private boolean recommenddelete;

    public CustomerResult(boolean result, ArrayList<Customer> customers, String errormsg, boolean recommenddelete) {
        this.result = result;
        this.customers = customers;
        this.errormsg = errormsg;
        this.recommenddelete = recommenddelete;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public boolean isRecommenddelete() {
        return recommenddelete;
    }

    public void setRecommenddelete(boolean recommenddelete) {
        this.recommenddelete = recommenddelete;
    }
}
