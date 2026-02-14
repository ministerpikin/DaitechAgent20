package ique.daitechagent.model.retrofitgraphs;

import com.google.gson.annotations.SerializedName;

public class Revenue {

    @SerializedName("displaynamexvalue")
    private String displaynamexvalue;

    @SerializedName("monthindex")
    private int monthindex;

    @SerializedName("payment")
    private int payment;

    @SerializedName("salesyear")
    private String salesyear;

    public Revenue(String displaynamexvalue2, int payment2, String salesyear2, int monthindex2) {
        this.displaynamexvalue = displaynamexvalue2;
        this.payment = payment2;
        this.salesyear = salesyear2;
        this.monthindex = monthindex2;
    }

    public String getDisplaynamexvalue() {
        return this.displaynamexvalue;
    }

    public void setDisplaynamexvalue(String displaynamexvalue2) {
        this.displaynamexvalue = displaynamexvalue2;
    }

    public int getPayment() {
        return this.payment;
    }

    public void setPayment(int payment2) {
        this.payment = payment2;
    }

    public String getSalesyear() {
        return this.salesyear;
    }

    public void setSalesyear(String salesyear2) {
        this.salesyear = salesyear2;
    }

    public int getMonthindex() {
        return this.monthindex;
    }

    public void setMonthindex(int monthindex2) {
        this.monthindex = monthindex2;
    }
}
