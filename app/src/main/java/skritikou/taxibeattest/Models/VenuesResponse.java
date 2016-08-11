package skritikou.taxibeattest.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import skritikou.taxibeattest.taxibeatUtilities.CommonUtils;


public class VenuesResponse {

    @SerializedName("response")
    private JsonObject response;
    @SerializedName("meta")
    private JsonObject meta;

    public ArrayList<Venue> getResults() {
        JsonArray venuesArray = response.get("venues").getAsJsonArray();
        ArrayList<Venue> venuesReturned = new ArrayList<>();

        Venue newVenue;
        CommonUtils cUtils = new CommonUtils();
        for (JsonElement venueElement : venuesArray) {
            newVenue = cUtils.jsonToVenue(venueElement);
            venuesReturned.add(newVenue);
        }
        return venuesReturned;
    }





    public void setResults(JsonObject results) {
        this.response = results;
    }

    public JsonObject getMeta() {
        return meta;
    }

    public void setMeta(JsonObject meta) {
        this.meta = meta;
    }


}
