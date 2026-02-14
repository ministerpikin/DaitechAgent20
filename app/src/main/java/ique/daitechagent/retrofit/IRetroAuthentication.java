package ique.daitechagent.retrofit;


import ique.daitechagent.model.AuthenticationQueryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRetroAuthentication {

    @GET("userprofileexist.php")
    Call<AuthenticationQueryResult> doesUserExist(@Query("email") String email);

}
