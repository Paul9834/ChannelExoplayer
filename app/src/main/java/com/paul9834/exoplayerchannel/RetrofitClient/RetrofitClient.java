package com.paul9834.exoplayerchannel.RetrofitClient;

import com.paul9834.exoplayerchannel.Controllers.Canales;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://headendev.badala.software";
    private static RetrofitClient mInstance;
    private final Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public Canales getCanal() {
        return retrofit.create(Canales.class);
    }




}