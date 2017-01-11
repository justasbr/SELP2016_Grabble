package com.example.justas.grabble;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ServiceConfigurationError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnMyLocationButtonClickListener, LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final double PICKUP_DISTANCE_IN_METERS = 10;
    private boolean mPermissionDenied = false;
    protected static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private String mLastUpdateTime;
    private ClusterManager<MarkerItem> mClusterManager;
    private Location mCurrentLocation;
    private List<MarkerItem> mMarkerItems;
    private SharedPreferences sharedPrefs;

    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;

    private boolean mRequestingLocationUpdates = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton inventoryFab = (FloatingActionButton) findViewById(R.id.inventory_button);
        FloatingActionButton leaderboardFab = (FloatingActionButton) findViewById(R.id.leaderboard_button);
        FloatingActionButton settingsFab = (FloatingActionButton) findViewById(R.id.settings_button);

        inventoryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
            }
        });

        leaderboardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LeaderboardActivity.class));
            }
        });

        settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });

        Utility.showFirstTimePlayerAlert(MapsActivity.this);

        buildGoogleApiClient();

        Context context = getApplicationContext();
        sharedPrefs = context.getSharedPreferences(
                getString(R.string.inventory_file), Context.MODE_PRIVATE);
    }

    private void fetchAllPlacemarks() {
        try {
            ServerService.getAllPlacemarks(new Callback<List<MarkerItem>>() {
                @Override
                public void onResponse(Call<List<MarkerItem>> call, Response<List<MarkerItem>> response) {
                    mMarkerItems = response.body();
                    if (mMarkerItems != null) {
                        mClusterManager.addItems(mMarkerItems);
                        mClusterManager.cluster();
                    }
                }

                @Override
                public void onFailure(Call<List<MarkerItem>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "We could not parse the markers", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "We could not parse the markers", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.9533, -3.1883), 10.0f));

        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new MarkerItemRenderer());

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mMap.setOnMyLocationButtonClickListener(this);

        enableMyLocation();
        fetchAllPlacemarks();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mRequestingLocationUpdates = true;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            mCurrentLocation = location;

            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();

            if (mMarkerItems != null) {
                ArrayList<MarkerItem> itemsToRemove = new ArrayList<>();
                for (MarkerItem markerItem : mMarkerItems) {
                    if (location.distanceTo(markerItem.getLocation()) <= PICKUP_DISTANCE_IN_METERS) {
                        itemsToRemove.add(markerItem);
                    }
                }

                boolean reclusteringNeeded = false;

                for (MarkerItem itemToRemove : itemsToRemove) {
                    mMarkerItems.remove(itemToRemove);
                    mClusterManager.removeItem(itemToRemove);
                    reclusteringNeeded = true;
                    incrementLetterCount(itemToRemove.getLabel());
                }

                if (reclusteringNeeded) {
                    mClusterManager.cluster();
                }
            }
        }
    }

    private void incrementLetterCount(String letterLabel) {
        int oldValue = sharedPrefs.getInt(letterLabel, 0);
        int updatedValue = oldValue + 1;

//        String letterCollectedMessage = letterLabel + " " + String.valueOf(updatedValue);
//        Log.d("LETTER_COLLECTED", letterCollectedMessage);

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(letterLabel, updatedValue).apply();
    }

    private void updateUI() {
        if (mCurrentLocation != null) {
            String updateText = String.valueOf(mCurrentLocation.getLatitude()) + " " + String.valueOf(mCurrentLocation.getLongitude());
            Toast.makeText(this, updateText, Toast.LENGTH_LONG).show();
        }

        // mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
        // mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
        // mLastUpdateTimeTextView.setText(mLastUpdateTime);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException e) {
                Log.d(TAG, e.toString());
            }
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.d(TAG, e.toString());
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    private class MarkerItemRenderer extends DefaultClusterRenderer<MarkerItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());

        public MarkerItemRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerItem markerItem, MarkerOptions markerOptions) {
            mIconGenerator.setStyle(IconGenerator.STYLE_PURPLE); //doRandom
            Bitmap icon = mIconGenerator.makeIcon(markerItem.getLabel());
            markerOptions.anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    //Based on an example from Stack Overflow
    @Override
    public void onBackPressed() {
        Utility.confirmExitDialog(MapsActivity.this);
    }
}
