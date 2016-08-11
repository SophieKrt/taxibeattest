package skritikou.taxibeattest.REST;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import skritikou.taxibeattest.taxibeatUtilities.Constants;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.URLVenues)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
