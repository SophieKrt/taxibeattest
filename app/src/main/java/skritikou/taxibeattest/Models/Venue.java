package skritikou.taxibeattest.Models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class Venue {

    @SerializedName("id")
    String id = "";
    @SerializedName("name")
    String name = "";
    @SerializedName("location")
    JsonObject location;
    @SerializedName("rating")
    String rating = "-";
    ArrayList<LabeledDisplay> labeledDisplays = new ArrayList<>();
    @SerializedName("distance")
    int distance = 0;
    @SerializedName("cc")
    String countryCode = "";
    @SerializedName("city")
    String city = "";
    @SerializedName("state")
    String state = "";
    @SerializedName("country")
    String country = "";
    ArrayList<String> formatedAddress = new ArrayList<>();
    @SerializedName("categories")
    JsonArray categories;
    @SerializedName("contact")
    JsonObject contact;
    @SerializedName("canonicalUrl")
    String venueLink;
    @SerializedName("stats")
    JsonObject statistics;
    @SerializedName("url")
    String url;
    @SerializedName("price")
    JsonObject price;
    @SerializedName("likes")
    JsonObject likes;
    @SerializedName("menu")
    JsonObject menu;
    @SerializedName("photos")
    JsonObject photos;


    private String address = "";
    private String venueImagePath = "";
    private double latitude = 0;
    private double longitude = 0;
    private int checkinsCount = 0;
    private int usersCount = 0;
    private int tipCount = 0;
    private int visitsCount = 0;
    private ArrayList<Category> venueCategories = new ArrayList<>();
    private ArrayList<String> photosArray = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonObject getContact() {
        return contact;
    }

    public void setContact(JsonObject contact) {
        this.contact = contact;
    }

    public JsonObject getLocation() {
        return location;
    }

    public void setLocation(JsonObject location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
//        latitude = this.location.get("lat").getAsDouble();
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
//        longitude = this.location.get("lng").getAsDouble();
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<LabeledDisplay> getLabeledDisplays() {
        return labeledDisplays;
    }

    public void setLabeledDisplays(ArrayList<LabeledDisplay> labeledDisplays) {
        this.labeledDisplays = labeledDisplays;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ArrayList<String> getFormatedAddress() {
        return formatedAddress;
    }

    public void setFormatedAddress(ArrayList<String> formatedAddress) {
        this.formatedAddress = formatedAddress;
    }

    public JsonArray getCategories() {
        return categories;
    }

    public void setCategories(JsonArray categories) {
        this.categories = categories;
    }

    public ArrayList<Category> getVenueCategories() {
        return venueCategories;
    }

    public void setVenueCategories(ArrayList<Category> venueCategories) {
        this.venueCategories = venueCategories;
    }

    public String getVenueImagePath() {
        return venueImagePath;
    }

    public void setVenueImagePath(String venueImagePath) {
        this.venueImagePath = venueImagePath;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getVenueLink() {
        return venueLink;
    }

    public void setVenueLink(String venueLink) {
        this.venueLink = venueLink;
    }

    public JsonObject getStatistics() {
        return statistics;
    }

    public void setStatistics(JsonObject statistics) {
        this.statistics = statistics;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JsonObject getPrice() {
        return price;
    }

    public void setPrice(JsonObject price) {
        this.price = price;
    }

    public JsonObject getLikes() {
        return likes;
    }

    public void setLikes(JsonObject likes) {
        this.likes = likes;
    }

    public JsonObject getMenu() {
        return menu;
    }

    public void setMenu(JsonObject menu) {
        this.menu = menu;
    }

    public JsonObject getPhotos() {
        return photos;
    }

    public void setPhotos(JsonObject photos) {
        this.photos = photos;
    }

    public int getCheckinsCount() {
        if (statistics != null && statistics.has("checkinsCount")){
            checkinsCount = statistics.get("checkinsCount").getAsInt();
        }
        return checkinsCount;
    }

    public void setCheckinsCount(int checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public int getUsersCount() {
        if (statistics != null && statistics.has("usersCount")){
            usersCount = statistics.get("usersCount").getAsInt();
        }
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public int getTipCount() {
        if (statistics != null && statistics.has("tipCount")){
            tipCount = statistics.get("tipCount").getAsInt();
        }
        return tipCount;
    }

    public void setTipCount(int tipCount) {
        this.tipCount = tipCount;
    }

    public int getVisitsCount() {
        if (statistics != null && statistics.has("visitsCount")){
            visitsCount = statistics.get("visitsCount").getAsInt();
        }
        return visitsCount;
    }

    public void setVisitsCount(int visitsCount) {
        this.visitsCount = visitsCount;
    }

    public ArrayList<String> getPhotosArray() {
        return photosArray;
    }

    public void setPhotosArray(ArrayList<String> photosArray) {
        this.photosArray = photosArray;
    }

}
