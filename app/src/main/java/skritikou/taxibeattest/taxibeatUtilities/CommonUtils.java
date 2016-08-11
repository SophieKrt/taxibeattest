package skritikou.taxibeattest.taxibeatUtilities;


import android.content.Context;
import android.graphics.Typeface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import skritikou.taxibeattest.Models.Category;
import skritikou.taxibeattest.Models.Venue;

public class CommonUtils {

    public Venue jsonToVenue(JsonElement jsElement) {
        ArrayList<Category> venueCategories;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Venue venue = gson.fromJson(jsElement, Venue.class);

        venue.setAddress(venue.getLocation().has("address") ?
                venue.getLocation().get("address").getAsString()
                : "");
        venue.setLatitude(venue.getLocation().has("lat") ?
                venue.getLocation().get("lat").getAsDouble()
                : 0.0);
        venue.setLongitude(venue.getLocation().has("lng") ?
                venue.getLocation().get("lng").getAsDouble()
                : 0.0);
        venue.setDistance(venue.getLocation().has("distance") ?
                venue.getLocation().get("distance").getAsInt()
                : 0);
        venue.setCountryCode(venue.getLocation().has("cc") ?
                venue.getLocation().get("cc").getAsString()
                : "");
        venue.setCity(venue.getLocation().has("city") ?
                venue.getLocation().get("city").getAsString()
                : "");
        venue.setState(venue.getLocation().has("state") ?
                venue.getLocation().get("state").getAsString()
                : "");
        venue.setCountry(venue.getLocation().has("country") ?
                venue.getLocation().get("country").getAsString()
                : "");

        venueCategories = getVenueCategories(venue.getCategories());
        venue.setVenueCategories(venueCategories);
        venue.setPhotosArray(getVenuePhotos(venue.getPhotos()));
        return venue;
    }

    private ArrayList<Category> getVenueCategories(JsonArray categoriesArray) {
        ArrayList<Category> categories = new ArrayList<>();
        Category newCategory;
        JsonObject catObj;
        JsonObject iconObj;
        for (JsonElement categoryElement : categoriesArray) {
            catObj = categoryElement.getAsJsonObject();
            newCategory = new Category();
            newCategory.setCategoryID(catObj.has("id") ? catObj.get("id").getAsString() : "");
            newCategory.setCategoryName(catObj.has("name") ? catObj.get("name").getAsString() : "");
            newCategory.setPluralName(catObj.has("pluralName") ? catObj.get("pluralName").getAsString() : "");
            newCategory.setShortName(catObj.has("shortName") ? catObj.get("shortName").getAsString() : "");
            if (catObj.has("icon")) {
                iconObj = catObj.get("icon").getAsJsonObject();
                if (iconObj.has("prefix") && iconObj.has("suffix")) {
                    newCategory.setIconPath(iconObj.get("prefix").getAsString() + iconObj.get("suffix").getAsString());
                }else newCategory.setIconPath("");
            }
            newCategory.setPrimary(catObj.has("primary") && catObj.get("primary").getAsBoolean());

            categories.add(newCategory);
        }

        return categories;

    }


    private ArrayList<String> getVenuePhotos(JsonObject photosObj){
        JsonArray items;
        JsonObject photoObj;
        String photoUrl;
        ArrayList<String> photosArray = new ArrayList<>();

        if (photosObj != null){
            if (photosObj.has("groups")
                    && photosObj.get("groups").getAsJsonArray().size() > 0
                    && (photosObj.get("groups").getAsJsonArray().get(0).getAsJsonObject().get("count").getAsInt() > 0)){
                items = photosObj.get("groups").getAsJsonArray().get(0).getAsJsonObject().get("items").getAsJsonArray();

                for (JsonElement item : items){
                    photoObj = item.getAsJsonObject();
                    photoUrl = photoObj.get("prefix").getAsString()
                            + "width" + photoObj.get("width").getAsString()
                            + photoObj.get("suffix").getAsString();
                    photosArray.add(photoUrl);
                }
            }
        }
        return photosArray;
    }


    public Typeface getTypeface(int mode, Context con) {
        switch (mode) {
            case 0:
                return Typeface.createFromAsset(con.getAssets(), "Roboto-Regular.ttf");
            case 1:
                return Typeface.createFromAsset(con.getAssets(), "Roboto-Light.ttf");
            case 2:
                return Typeface.createFromAsset(con.getAssets(), "Roboto-Bold.ttf");
            default:
                return Typeface.createFromAsset(con.getAssets(), "Roboto-Regular.ttf");
        }
    }
}
