package skritikou.taxibeattest.Views;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import butterknife.BindView;
import me.drakeet.materialdialog.MaterialDialog;
import pub.devrel.easypermissions.EasyPermissions;
import skritikou.taxibeattest.Models.Venue;
import skritikou.taxibeattest.Presenters.MapPresenterImpl;
import skritikou.taxibeattest.R;

public class MainActivity extends AppCompatActivity implements MainActivityInterface, MapVenuesFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks, LocationListener {


    private final static int RC_LOCATION_PERM = 100;
    private final static int RC_NETWORK_PERM = 101;
    private final static String TAG = MainActivity.class.getSimpleName();

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private boolean gpsDialogShown = false, mapStarted = false;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;

    private MapPresenterImpl mMapPresenter;
    private Venue mVenueSelected;
    private MaterialDialog mMaterialDialog;

    @BindView(R.id.spin_kit)
    SpinKitView spinkit;

    private BroadcastReceiver mGPSStateChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (gpsDialogShown) {
                    gpsDialogShown = false;
                    removeGpsDialog();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerReceiver(mGPSStateChange, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        spinkit = (SpinKitView)findViewById(R.id.spin_kit);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMapPresenter = new MapPresenterImpl(this);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();

                }
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            showMessageForRequestingPerms(getString(R.string.internet_access_prompt), getString(R.string.internet_access_title));
        }else{
            if (checkPlayServices()) {
                // Building the GoogleApi client
                buildGoogleApiClient();
                mRequestingLocationUpdates = true;
            }
        }



    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mGPSStateChange);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showMessageForRequestingPerms(getString(R.string.access_prompt), getResources().getString(R.string.location_perms));
            return;
        } else {
           if (mLocationRequest == null){
               createLocationRequest();
           }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showPromptForGps();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (!mapStarted){
            startMap();
        }
    }

    @Override
    public void startMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showMessageForRequestingPerms(getString(R.string.access_prompt), getResources().getString(R.string.location_perms));
            return;
        } else {
            if (mLastLocation == null)
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null){
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                MapVenuesFragment mapVenuesFragment = MapVenuesFragment.newInstance(latitude, longitude);
                String tag = "map_frag";
                addNewFragment(mapVenuesFragment, tag);
                mRequestingLocationUpdates = false;
                mapStarted = true;
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }else{
                Toast.makeText(this, "We couldn't retrieve your gps location. Restart your GPS and try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void showPromptForGps() {
        CustomDialogFragment dialog = CustomDialogFragment.newInstance(getString(R.string.gps_not_enabled), getString(R.string.gps_prompt));
        addNewFragment(dialog, "gps_frag");
        mMaterialDialog = new MaterialDialog(this)
                .setTitle("GPS not enabled")
                .setMessage("The application needs access to gps, in order to retrieve all candy stores near you. Select Accept, to enable GPS in Settings, or Decline to close the app.")
                .setPositiveButton(getResources().getString(R.string.Accept), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        gpsDialogShown = true;
                        spinkit.setVisibility(View.GONE);
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getResources().getString(R.string.Decline), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        mMaterialDialog.show();
    }

    @Override
    public void removeGpsDialog() {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                if (getFragmentManager().findFragmentByTag("gps_frag") != null) {
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("gps_frag")).commitAllowingStateLoss();
                    spinkit.setVisibility(View.VISIBLE);
                }
            }
        startLocationUpdates();
    }


    @Override
    public void showMessageForRequestingPerms(String caseofPerms, final String prompt) {
        mMaterialDialog = new MaterialDialog(this)
                .setTitle(prompt)
                .setMessage(caseofPerms)
                .setPositiveButton(getResources().getString(R.string.Accept), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();

                        if (prompt.toLowerCase().contains("location"))
                        EasyPermissions.requestPermissions(this, getString(R.string.location_perms),
                                RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);
                        else
                            EasyPermissions.requestPermissions(this, getString(R.string.internet_access_title),
                                    RC_NETWORK_PERM, Manifest.permission.ACCESS_NETWORK_STATE);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.Decline), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        mMaterialDialog.show();
    }

    @Override
    public void addNewFragment(Fragment frag, String tag) {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, frag, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    @Override
    public MapPresenterImpl getMapPresenter() {
        return mMapPresenter;
    }


    @Override
    public void onFragmentInteraction(int mode, Venue venueShown) {
        switch (mode) {
            case 0: /* User clicked on a Venue info window, so we have to show the details of it  */
                if (venueShown != null) {
                    mVenueSelected = venueShown;
                    DetailedVenueFragment detailedVenueFrag = DetailedVenueFragment.newInstance();
                    addNewFragment(detailedVenueFrag, "detailed_frag");
                }
                break;
            case 1:
                /* Error dialog to be shown, no internet*/
                CustomDialogFragment dialog = CustomDialogFragment.newInstance(null, null);
                addNewFragment(dialog, "error_frag");
                break;
            case 2:
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("error_frag")).commit();
                break;
            default:
        }
    }

    @Override
    public Venue getmVenueSelected() {
        return mVenueSelected;
    }

    @Override
    public void setmVenueSelected(Venue mVenueSelected) {
        this.mVenueSelected = mVenueSelected;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case RC_LOCATION_PERM:
                startLocationUpdates();
                break;
            case RC_NETWORK_PERM:
                if (checkPlayServices()) {
                    // Building the GoogleApi client
                    buildGoogleApiClient();
                    mRequestingLocationUpdates = true;
                }
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case RC_LOCATION_PERM:
                finish();
                break;
            default:
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        String tag;
        FragmentManager frMan = getFragmentManager();
        if (frMan.getBackStackEntryCount() > 0) {
            tag = frMan.getBackStackEntryAt(frMan.getBackStackEntryCount() - 1).getName();
            if (!(tag.equals("error_frag") || tag.equals("gps_frag"))) {
                super.onBackPressed();
            }
        }
    }



}
