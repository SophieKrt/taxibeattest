package skritikou.taxibeattest.Presenters;

import android.content.Context;
import android.provider.SyncStateContract;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;
import retrofit2.http.Query;
import skritikou.taxibeattest.Models.SingleVenueResponse;
import skritikou.taxibeattest.Models.Venue;
import skritikou.taxibeattest.Models.VenuesResponse;
import skritikou.taxibeattest.taxibeatUtilities.Constants;

public class MapPresenterImpl implements MapPresenter {
    private Context mContext;


    public MapPresenterImpl(Context con){
        this.mContext = con;
    }


    @Override
    public Call<VenuesResponse> getVenueSpots(@Query("ll") String latlng,
                                              @Query("categoryId") String category,
                                              @Query("client_id") String client_id,
                                              @Query("client_secret") String client_secret,
                                              @Query("v") String vNumber) {
        return null;
    }

    @Override
    public Call<SingleVenueResponse> getVenueDetails(@Path("venueID") String venueID, @Query("client_id") String client_id, @Query("client_secret") String client_secret, @Query("v") String vNumber) {
        return null;
    }

}
