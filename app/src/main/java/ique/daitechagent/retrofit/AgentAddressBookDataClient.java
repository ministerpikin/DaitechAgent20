package ique.daitechagent.retrofit;

import ique.daitechagent.common.Common;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgentAddressBookDataClient {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitAgentAddressBookInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Common.agentAddressBookURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkConnectionClient.getOkHttpClientInstance())
                    .build();
        }
        return retrofit;
    }
}
