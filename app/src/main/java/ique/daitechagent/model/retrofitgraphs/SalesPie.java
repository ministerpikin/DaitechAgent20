package ique.daitechagent.model.retrofitgraphs;

import com.google.gson.annotations.SerializedName;

public class SalesPie {

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("stockname")
    private String stockname;

    @SerializedName("stocknumber")
    private String stocknumber;

    public SalesPie(String stockname2, String stocknumber2, int quantity2) {
        this.stockname = stockname2;
        this.stocknumber = stocknumber2;
        this.quantity = quantity2;
    }

    public String getStockname() {
        return this.stockname;
    }

    public void setStockname(String stockname2) {
        this.stockname = stockname2;
    }

    public String getStocknumber() {
        return this.stocknumber;
    }

    public void setStocknumber(String stocknumber2) {
        this.stocknumber = stocknumber2;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity2) {
        this.quantity = quantity2;
    }
}
