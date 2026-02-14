package ique.daitechagent.model.retrofitgraphs;

import com.google.gson.annotations.SerializedName;

public class PerformanceIndex {

    @SerializedName("pi")
    private String pi;

    public PerformanceIndex(String pi) {
        this.pi = pi;
    }

    public String getPi() {
        return this.pi;
    }

    public void setPi(String pi) {
        this.pi = pi;
    }
}
