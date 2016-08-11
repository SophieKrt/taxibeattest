package skritikou.taxibeattest.Views;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import skritikou.taxibeattest.Models.SingleVenueResponse;
import skritikou.taxibeattest.Models.Venue;
import skritikou.taxibeattest.Models.VenuesResponse;
import skritikou.taxibeattest.Presenters.MapPresenter;
import skritikou.taxibeattest.R;
import skritikou.taxibeattest.REST.ApiClient;
import skritikou.taxibeattest.taxibeatUtilities.CommonUtils;
import skritikou.taxibeattest.taxibeatUtilities.Constants;


public class MapVenuesFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {
    private static final int RC_LOCATION_PERM = 100;
    private static final String LONGITUDE = "lng";
    private static final String LATITUDE = "lat";


    private ActionBar toolbar;
    private Context mContext;
    private GoogleMap mMap;
    private MainActivity mActivity;
    private MapFragment mapFragment;
    private MaterialDialog mMaterialDialog;
    private Marker previousMarkerClicked, mCurrentUserMarker;
    private Venue mVenueShown;
    private double mCurrentLatitude = 0.0;
    private double mCurrentLongitude = 0.0;
    private boolean customDialogShown = false;
    private boolean mCameraMoved = false;
    private HashMap<String, Venue> markerIdsToVenues;
    private ArrayList<String> mVenueIds;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.user_currentaddress_tv)
    TextView currentAddress_tv;
    @BindView(R.id.current_loctag_tv)
    TextView mCurrentLocTag;
    @BindView(R.id.info_window)
    LinearLayout infoWindow;
    private View mMainView;
    private TextView mVenueName_tv;
    private TextView mVenueAddress_tv;
    private TextView mVenueCategory_tv;
    private TextView mVenueRating_tv;
    private ImageView mVenue_iv;


    private BroadcastReceiver mNetworkStateChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (intent.getExtras() != null) {
                if (isConnected) {
                    if (customDialogShown) {
                        interactWithActivity(2, null);
                        customDialogShown = false;
                    }

                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    if (!customDialogShown) {
                        interactWithActivity(1, null);
                        customDialogShown = true;
                    }

                } else {
                    if (!customDialogShown) {
                        interactWithActivity(1, null);
                        customDialogShown = true;
                    }
                }
            }
        }
    };


    public MapVenuesFragment() {
        // Required empty public constructor
    }

    public static MapVenuesFragment newInstance(double currentLat, double currentLng) {
        Bundle extras = new Bundle();
        extras.putDouble(LATITUDE, currentLat);
        extras.putDouble(LONGITUDE, currentLng);
        MapVenuesFragment fragment = new MapVenuesFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mContext = mActivity.getApplicationContext();
        mContext.registerReceiver(mNetworkStateChange, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        toolbar = mActivity.getSupportActionBar();
        toolbar.setTitle("");
        toolbar.setDisplayHomeAsUpEnabled(false);

        if (getArguments() != null) {
            mCurrentLatitude = getArguments().getDouble(LATITUDE);
            mCurrentLongitude = getArguments().getDouble(LONGITUDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        try {
        mMainView = (View) inflater.inflate(R.layout.fragment_map, container, false);
//        } catch(InflateException e) {
//            e.printStackTrace();
//            System.out.println("Predicted Time Fragment");
//        }
        return mMainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, mMainView);
        currentAddress_tv.setText(getAddressFromLocation());


        /*
        * define the elements of info window
        */
        mVenue_iv = (ImageView) infoWindow.findViewById(R.id.info_venue_iv);
        mVenueAddress_tv = (TextView) infoWindow.findViewById(R.id.info_storeaddress_tv);
        mVenueName_tv = (TextView) infoWindow.findViewById(R.id.info_storename_tv);
        mVenueCategory_tv = (TextView) infoWindow.findViewById(R.id.info_storecategory_tv);
        mVenueRating_tv = (TextView) infoWindow.findViewById(R.id.info_storerating_tv);

         /* Set typeface */
        CommonUtils utils = new CommonUtils();

        mVenueName_tv.setTypeface(utils.getTypeface(2, mContext));
        mVenueRating_tv.setTypeface(utils.getTypeface(2, mContext));
        mVenueCategory_tv.setTypeface(utils.getTypeface(0, mContext));
        mVenueAddress_tv.setTypeface(utils.getTypeface(0, mContext));
        mCurrentLocTag.setTypeface(utils.getTypeface(2, mContext));
        currentAddress_tv.setTypeface(utils.getTypeface(0, mContext));

        infoWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactWithActivity(0, mVenueShown);
            }
        });


        setUpMapIfNeeded();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_close:
                infoWindow.setVisibility(View.GONE);
                previousMarkerClicked.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin));
                previousMarkerClicked = null;
                toolbar.setTitle("");
                setHasOptionsMenu(false);
                // User chose the "Settings" item, show the app settings UI...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void setUpMapIfNeeded() {
        /* begin getting the map */
        if (mMap == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
    }

    private String getAddressFromLocation() {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(mCurrentLatitude, mCurrentLongitude, 1);
            if (!addresses.isEmpty())
                return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showMessageForRequestingPerms(getString(R.string.access_prompt), getResources().getString(R.string.location_perms));
            return;
        }

        mCurrentUserMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mCurrentLatitude, mCurrentLongitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location))
                .title(getString(R.string.here_prompt)));
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 12.0f));

        getVenues();

    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            /*The user gestured on the map*/
            mCameraMoved = true;
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
          /*The user tapped something on the map*/
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
           /*The app moved the camera*/
        }
    }

    @Override
    public void onCameraMove() {
        /*The camera is moving*/
    }

    @Override
    public void onCameraMoveCanceled() {
        /*Camera movement canceled*/
    }

    @Override
    public void onCameraIdle() {
       /*The camera has stopped moving*/
        if (mCameraMoved) {
            mCameraMoved = false;
            CameraPosition camPos = mMap.getCameraPosition();
            LatLng latLng = camPos.target;
            mCurrentLatitude = latLng.latitude;
            mCurrentLongitude = latLng.longitude;
            getVenues();
        }

    }

    private void getVenues() {
        MapPresenter apiService =
                ApiClient.getClient().create(MapPresenter.class);

        Call<VenuesResponse> venuesCall = apiService.getVenueSpots(getLocationAsString(),
                Constants.CATEGORY,
                Constants.CLIENT_ID,
                Constants.SECRET,
                Constants.VERSION);

        venuesCall.enqueue(new Callback<VenuesResponse>() {
            @Override
            public void onResponse(Call<VenuesResponse> call, Response<VenuesResponse> response) {
                ArrayList<Venue> venuesReturned = response.body().getResults();
                addMarkersToMap(venuesReturned);
            }

            @Override
            public void onFailure(Call<VenuesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.d("Retrofit", "error on getting venues : " + t.getMessage());

            }
        });
    }

    private void getVenueDetails(Venue venue) {
        MapPresenter apiService =
                ApiClient.getClient().create(MapPresenter.class);

        Call<SingleVenueResponse> venueDetails = apiService.getVenueDetails(
                venue.getId(),
                Constants.CLIENT_ID,
                Constants.SECRET,
                Constants.VERSION);

        venueDetails.enqueue(new Callback<SingleVenueResponse>() {
            @Override
            public void onResponse(Call<SingleVenueResponse> call, Response<SingleVenueResponse> response) {
                mVenueShown = response.body().getVenueReturned();
                adjustInfoWindow(mVenueShown);
            }

            @Override
            public void onFailure(Call<SingleVenueResponse> call, Throwable t) {
                // Log error here since request failed
                Log.d("Retrofit", "error on getting venue : " + t.getMessage());

            }
        });
    }

    private void addMarkersToMap(ArrayList<Venue> venues) {
        Marker newMarker = null;
        if (mMap != null) {
            if (markerIdsToVenues == null) {
                markerIdsToVenues = new HashMap<>();
                mVenueIds = new ArrayList<>();
            }
            for (Venue v : venues) {
                if (!mVenueIds.contains(v.getId())) {
                    newMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(v.getLatitude(), v.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin))
                            .title(v.getName()));
                    mVenueIds.add(v.getId());
                    markerIdsToVenues.put(newMarker.getId(), v);
                }
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                /* for all markers except the user's marker*/
                    if (!mCurrentUserMarker.getId().equals(marker.getId())) {
                        if (previousMarkerClicked == null || !previousMarkerClicked.getId().equals(marker.getId())) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin_selected));
                            if (previousMarkerClicked != null)
                                previousMarkerClicked.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin));
                            previousMarkerClicked = marker;
                            getVenueDetails(markerIdsToVenues.get(marker.getId()));
                        } else {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin));
                            infoWindow.setVisibility(View.GONE);
                            setHasOptionsMenu(false);
                        }
                    }
                    return true;
                }
            });
        }


    }

    private void adjustInfoWindow(Venue clickedVenue) {
        infoWindow.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
        toolbar.setTitle("CandyStore");
        infoWindow.requestFocus();
        mVenueName_tv.setText(clickedVenue.getName());
        mVenueAddress_tv.setText(clickedVenue.getAddress());
        if (clickedVenue.getRating().equals("-"))
            mVenueRating_tv.setVisibility(View.GONE);
        else {
            mVenueRating_tv.setVisibility(View.VISIBLE);
            mVenueRating_tv.setText(clickedVenue.getRating());
        }
        if (clickedVenue.getVenueImagePath().isEmpty()) {
            if (clickedVenue.getPhotosArray().size() > 0) {
                clickedVenue.setVenueImagePath(clickedVenue.getPhotosArray().get(0));
            } else
                clickedVenue.setVenueImagePath("https://www.dropbox.com/s/569c3o8cktyh8un/candyshop.png?dl=0");
        }

        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.e("Picasso", "error in downloading");
                exception.printStackTrace();
            }
        });
        builder.build()
                .load(clickedVenue.getVenueImagePath())
                .placeholder(R.drawable.candyshopdefault)
                .resize(100, 100)
                .centerCrop()
                .into(mVenue_iv);

    }

    private String getLocationAsString() {
        return (String.valueOf(mCurrentLatitude) + "," + String.valueOf(mCurrentLongitude));
    }

    private void showMessageForRequestingPerms(String caseofPerms, String prompt) {
        mMaterialDialog = new MaterialDialog(mContext)
                .setTitle(prompt)
                .setMessage(caseofPerms)
                .setPositiveButton(getResources().getString(R.string.Accept), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        EasyPermissions.requestPermissions(this, getString(R.string.location_perms),
                                RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.Decline), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //destroyFragment();
                        getActivity().moveTaskToBack(true);
                        getActivity().finish();
                    }
                });

        mMaterialDialog.show();
    }


    /*
    * there are 3 cases of interaction:
    * 0 -> user has clicked on an info window of a candy store, so we inform the activity to show the details
    * 1 -> internet connection seems to be lost, so we inform the activity to show the error dialog (venueShown = null)
    * 2 -> internet connection is back, so we inform the activity to remove the error dialog (venueShown = null)
    */
    public void interactWithActivity(int caseOfInteraction, Venue venueShown) {
        if (mListener != null) {
            mListener.onFragmentInteraction(caseOfInteraction, venueShown);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mapFragment != null && getActivity() != null) {
//            getFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mActivity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) mActivity;
        } else {
            throw new RuntimeException(mActivity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int mode, Venue venueShown);
    }

   
}
