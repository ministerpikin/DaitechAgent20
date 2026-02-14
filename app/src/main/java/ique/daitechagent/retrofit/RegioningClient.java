package ique.daitechagent.retrofit;

import ique.daitechagent.common.Common;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegioningClient {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitRegioningInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Common.regionBaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkConnectionClient.getOkHttpClientInstance())
                    .build();
        }
        return retrofit;
    }
}
