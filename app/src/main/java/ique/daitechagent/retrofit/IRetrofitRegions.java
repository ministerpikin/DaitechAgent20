package ique.daitechagent.retrofit;


import ique.daitechagent.model.RegionQueryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRetrofitRegions {

    @GET("sync_region.php")
    Call<RegionQueryResult> getRegion(@Query("email") String email, @Query("mode") int mode);


}
