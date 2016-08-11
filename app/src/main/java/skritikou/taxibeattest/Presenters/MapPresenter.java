package skritikou.taxibeattest.Presenters;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import skritikou.taxibeattest.Models.SingleVenueResponse;
import skritikou.taxibeattest.Models.Venue;
import skritikou.taxibeattest.Models.VenuesResponse;
import skritikou.taxibeattest.Views.MapVenuesFragment;


public interface MapPresenter {
    @GET("venues/search")
    Call<VenuesResponse> getVenueSpots(@Query("ll") String latlng,
                                           @Query("categoryId") String category,
                                           @Query("client_id") String client_id,
                                           @Query("client_secret") String client_secret,
                                           @Query("v") String vNumber);

    @GET("venues/{venueID}")
    Call<SingleVenueResponse> getVenueDetails(@Path("venueID") String venueID,
                                              @Query("client_id") String client_id,
                                              @Query("client_secret") String client_secret,
                                              @Query("v") String vNumber);


}
