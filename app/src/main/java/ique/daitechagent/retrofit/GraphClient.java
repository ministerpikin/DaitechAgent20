package ique.daitechagent.retrofit;

import ique.daitechagent.common.Common;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GraphClient {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitGraphInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Common.graphDataBaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkConnectionClient.getOkHttpClientInstance())
                    .build();
        }
        return retrofit;
    }
}
