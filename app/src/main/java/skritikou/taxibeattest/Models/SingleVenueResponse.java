package skritikou.taxibeattest.Models;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import skritikou.taxibeattest.taxibeatUtilities.CommonUtils;

public class SingleVenueResponse {

    @SerializedName("response")
    JsonObject response;

    public Venue getVenueReturned(){
        Venue venueReturned = new Venue();
        JsonObject venueObj;
        CommonUtils cmUtils = new CommonUtils();

        if (response != null && !response.isJsonNull()){
            venueObj = response.has("venue")? response.get("venue").getAsJsonObject() : null;
            if (venueObj != null){
                venueReturned = cmUtils.jsonToVenue(venueObj);
            }
        }

        return venueReturned;
    }




    public JsonObject getResponse() {
        return response;
    }

    public void setResponse(JsonObject response) {
        this.response = response;
    }
}
