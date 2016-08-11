package skritikou.taxibeattest.Presenters;

import android.content.Context;
import android.provider.SyncStateContract;
import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;
import retrofit2.http.Query;
import skritikou.taxibeattest.Models.SingleVenueResponse;
import skritikou.taxibeattest.Models.Venue;
import skritikou.taxibeattest.Models.VenuesResponse;
import skritikou.taxibeattest.REST.ApiClient;
import skritikou.taxibeattest.Views.MapVenuesFragment;
import skritikou.taxibeattest.taxibeatUtilities.Constants;

public class MapPresenterImpl implements MapPresenter {
    private Context mContext;
    private ArrayList<Venue> venuesReturned;


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


    public void getCandyVenues(final MapVenuesFragment frag, String location, String category, String client_id, String client_secret, String vNumber){

        MapPresenter apiService =
                ApiClient.getClient().create(MapPresenter.class);

        Call<VenuesResponse> venuesCall =  apiService.getVenueSpots(location, category, client_id, client_secret, vNumber);

        venuesCall.enqueue(new Callback<VenuesResponse>() {
            @Override
            public void onResponse(Call<VenuesResponse> call, Response<VenuesResponse> response) {
                frag.setVenuesArray(response.body().getResults());
            }

            @Override
            public void onFailure(Call<VenuesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.d("Retrofit", "error on getting venues : " + t.getMessage());

            }
        });
    }

    public void getVenueDetailed(final MapVenuesFragment frag, String venueID, String client_id, String client_secret, String vNumber){

        MapPresenter apiService =
                ApiClient.getClient().create(MapPresenter.class);

        Call<SingleVenueResponse> venueDetails = apiService.getVenueDetails(
                venueID,
                Constants.CLIENT_ID,
                Constants.SECRET,
                Constants.VERSION);

        venueDetails.enqueue(new Callback<SingleVenueResponse>() {
            @Override
            public void onResponse(Call<SingleVenueResponse> call, Response<SingleVenueResponse> response) {
               frag.setmVenueShown(response.body().getVenueReturned());
            }

            @Override
            public void onFailure(Call<SingleVenueResponse> call, Throwable t) {
                // Log error here since request failed
                Log.d("Retrofit", "error on getting venue : " + t.getMessage());

            }
        });
    }



}
