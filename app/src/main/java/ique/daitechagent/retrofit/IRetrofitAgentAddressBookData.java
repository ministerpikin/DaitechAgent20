package ique.daitechagent.retrofit;


import ique.daitechagent.model.AgentAddressBookResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRetrofitAgentAddressBookData {

    @GET("agent_contact_insert.php")
    Call<AgentAddressBookResult> insertAddressBookEntry(@Query("email") String email, @Query("contactperson") String contactperson, @Query("phoneno") String phoneno);


}
