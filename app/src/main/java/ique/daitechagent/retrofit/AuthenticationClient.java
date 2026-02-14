package ique.daitechagent.retrofit;

import ique.daitechagent.common.Common;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticationClient {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitAuthenticationInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Common.authenticationDataBaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkConnectionClient.getOkHttpClientInstance())
                    .build();
        }
        return retrofit;
    }
}
