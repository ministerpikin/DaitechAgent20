package ique.daitechagent.retrofit;

import java.util.List;

import ique.daitechagent.model.retrofitgraphs.PerformanceIndex;
import ique.daitechagent.model.retrofitgraphs.Revenue;
import ique.daitechagent.model.retrofitgraphs.Sales;
import ique.daitechagent.model.retrofitgraphs.SalesPie;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRetrofitGraphs {

    @GET("chart_sales_mobile.php")
    Call<List<Sales>> getSales(@Query("email") String email, @Query("stock") String stock, @Query("year") int year, @Query("interval") String interval);

    @GET("chart_paid_mobile.php")
    Call<List<Revenue>> getRevenue(@Query("email") String email, @Query("year") int year, @Query("interval") String interval);

    @GET("chart_pie_mobile.php")
    Call<List<SalesPie>> getSalesPie(@Query("email") String email, @Query("stock") String stock, @Query("year") int year, @Query("interval") String interval);

    @GET("chart_performanceindex.php")
    Call<PerformanceIndex> getPerformanceIndex(@Query("email") String email);

}
