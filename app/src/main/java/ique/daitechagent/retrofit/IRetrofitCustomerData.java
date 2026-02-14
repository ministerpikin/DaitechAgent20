package ique.daitechagent.retrofit;


import ique.daitechagent.model.CustomerResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRetrofitCustomerData {

    @GET("customers_get_list.php")
    Call<CustomerResult> getCutomers(@Query("email") String email);

    @GET("customers_create.php")
    Call<CustomerResult> createCustomer(@Query("email") String email, @Query("name") String name, @Query("contactperson") String contactperson, @Query("address") String address, @Query("phoneno") String phoneno);

    @GET("customers_get_list_search.php")
    Call<CustomerResult> searchCustomers(@Query("search") String search);

    @GET("customers_update_coordinates.php")
    Call<CustomerResult> updateCustomerCoordinates(@Query("email") String email, @Query("customernumber") String customernumber, @Query("lat") Double lat, @Query("lng") Double lng);

    //@GET("chart_performanceindex.php")
    //Call<PerformanceIndex> getPerformanceIndex(@Query("email") String email);
}
