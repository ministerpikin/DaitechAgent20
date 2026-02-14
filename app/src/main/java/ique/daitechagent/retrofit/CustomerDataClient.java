package ique.daitechagent.retrofit;

import java.util.concurrent.TimeUnit;

import ique.daitechagent.common.Common;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomerDataClient {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitCustomerInstance() {
        if (retrofit == null) {

            /*
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Common.customerDataBaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

             */

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Common.customerDataBaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkConnectionClient.getOkHttpClientInstance())
                    .build();
        }
        return retrofit;
    }
}
