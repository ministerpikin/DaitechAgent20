package ique.daitechagent.model;

import com.google.gson.annotations.SerializedName;

public class AuthenticationQueryResult {

    @SerializedName("result")
    private boolean result;

    @SerializedName("errormsg")
    private String errormsg;

    public AuthenticationQueryResult(boolean result, String errormsg) {
        this.result = result;
        this.errormsg = errormsg;
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
}
