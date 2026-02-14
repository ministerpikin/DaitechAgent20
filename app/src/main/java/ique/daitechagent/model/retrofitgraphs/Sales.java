package ique.daitechagent.model.retrofitgraphs;

import com.google.gson.annotations.SerializedName;

public class Sales {

    @SerializedName("displaynamexvalue")
    private String displaynamexvalue;

    @SerializedName("monthindex")
    private int monthindex;

    @SerializedName("product")
    private String product;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("salesmonth")
    private String salesmonth;

    @SerializedName("salesyear")
    private String salesyear;

    public Sales(String displaynamexvalue2, String product2, int quantity2, String salesmonth2, String salesyear2, int monthindex2) {
        this.displaynamexvalue = displaynamexvalue2;
        this.product = product2;
        this.quantity = quantity2;
        this.salesmonth = salesmonth2;
        this.salesyear = salesyear2;
        this.monthindex = monthindex2;
    }

    public String getDisplaynamexvalue() {
        return this.displaynamexvalue;
    }

    public void setDisplaynamexvalue(String displaynamexvalue2) {
        this.displaynamexvalue = displaynamexvalue2;
    }

    public String getProduct() {
        return this.product;
    }

    public void setProduct(String product2) {
        this.product = product2;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity2) {
        this.quantity = quantity2;
    }

    public String getSalesmonth() {
        return this.salesmonth;
    }

    public void setSalesmonth(String salesmonth2) {
        this.salesmonth = salesmonth2;
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
