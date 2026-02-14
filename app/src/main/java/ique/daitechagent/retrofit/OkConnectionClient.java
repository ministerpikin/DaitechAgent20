package ique.daitechagent.retrofit;

import java.util.concurrent.TimeUnit;

import ique.daitechagent.common.Common;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OkConnectionClient {

    private static OkHttpClient httpClient;

    public static OkHttpClient getOkHttpClientInstance() {
        if (httpClient == null) {

            httpClient = new OkHttpClient.Builder()
                    /*.callTimeout(2, TimeUnit.MINUTES)*/
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();


        }
        return httpClient;
    }
}
