package com.example.justas.grabble;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.justas.grabble.data.CollectedMarkersContract.MarkerEntry;
import com.example.justas.grabble.data.CollectedMarkersOpenHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.justas.grabble.Utility.getDate;
import static com.example.justas.grabble.Utility.getDateTime;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnMyLocationButtonClickListener, LocationListener,
        ConnectionCallbacks, OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS_BATTERY_SAVER = 6000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS_BATTERY_SAVER =
            UPDATE_INTERVAL_IN_MILLISECONDS_BATTERY_SAVER / 2;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final double PICKUP_DISTANCE_IN_METERS = 10;

    private static final int MARKERS_TO_SHOW = 100;
    public static final String SHOW_BEGINNER_POPUP = "show_beginner_popup";

    private boolean mPermissionDenied = false;
    protected static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private String mLastUpdateTime;
    private ClusterManager<MarkerItem> mClusterManager;
    private Location mCurrentLocation;
    private List<MarkerItem> mMarkerItems;
    private SharedPreferences sharedPrefs;

    private SharedPreferences applicationPrefs;
    private boolean mBatterySaverMode;
    private boolean mShowOnlyClosest;
    private boolean forceMapRecluster = true;

    private CollectedMarkersOpenHelper mDbHelper;
    private SQLiteDatabase db;

    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;

    private boolean mRequestingLocationUpdates = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApplicationPrefs();


        mDbHelper = new CollectedMarkersOpenHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUpButtons();

        boolean showBeginnerPopup = applicationPrefs.getBoolean(SHOW_BEGINNER_POPUP, true);
        if (showBeginnerPopup) {
            Utility.showFirstTimePlayerAlert(MapsActivity.this);
        }

        buildGoogleApiClient();

        Context context = getApplicationContext();
        sharedPrefs = context.getSharedPreferences(
                getString(R.string.inventory_file), Context.MODE_PRIVATE);
    }

    private void setUpButtons() {
        FloatingActionButton inventoryFab = (FloatingActionButton) findViewById(R.id.inventory_button);
        FloatingActionButton leaderboardFab = (FloatingActionButton) findViewById(R.id.leaderboard_button);
        FloatingActionButton statsFab = (FloatingActionButton) findViewById(R.id.stats_button);
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

        statsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HistoryStatsActivity.class));
            }
        });

        settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
    }

    private void initApplicationPrefs() {
        applicationPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        applicationPrefs.registerOnSharedPreferenceChangeListener(this);
        updateApplicationPrefs();
    }

    private void updateApplicationPrefs() {
        mBatterySaverMode = applicationPrefs.getBoolean(getString(R.string.pref_battery_saver), false);
        mShowOnlyClosest = applicationPrefs.getBoolean(getString(R.string.pref_only_show_closest), false);
    }

    private void fetchAllPlacemarks() {
        try {
            ServerService.getAllPlacemarks(new Callback<List<MarkerItem>>() {
                @Override
                public void onResponse(Call<List<MarkerItem>> call, Response<List<MarkerItem>> response) {
                    if (response.body() == null) {
                        return;
                    }

                    List<MarkerItem> parsedItems = response.body();

                    HashSet<MarkerItem> collectedMarkers = fetchCollectedMarkers();
                    mMarkerItems = filterOut(parsedItems, collectedMarkers);

                    Log.d("DONT SHOW COLLECTED", "SIZE1 " + String.valueOf(mMarkerItems.size()));
                    Log.d("DONT SHOW COLLECTED", "SIZE2 " + collectedMarkers.size());

                    mClusterManager.addItems(mMarkerItems);
                    mClusterManager.cluster();
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

    private HashSet<MarkerItem> fetchCollectedMarkers() {
        HashSet<MarkerItem> result = new HashSet<>();

        String today = getDate();
        String selectionToday = MarkerEntry.COLUMN_NAME_DATETIME + " >= \"" + today + "\"";

        Cursor cursor = db.query(MarkerEntry.TABLE_NAME, null, selectionToday, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String letter = cursor.getString(cursor.getColumnIndex(MarkerEntry.COLUMN_NAME_LETTER));
                Double lat = cursor.getDouble(cursor.getColumnIndex(MarkerEntry.COLUMN_NAME_LAT));
                Double lng = cursor.getDouble(cursor.getColumnIndex(MarkerEntry.COLUMN_NAME_LNG));
                result.add(new MarkerItem(letter, lat, lng));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    private <T> List<T> filterOut(List<T> origList, HashSet<T> origSet) {
        List<T> resultList = new ArrayList<>();

        for (T origItem : origList) {
            if (!origSet.contains(origItem)) {
                resultList.add(origItem);
            }
        }

        return resultList;
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

        if (mBatterySaverMode) {
            Log.d("BATTERY_SAVER", "ON");
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS_BATTERY_SAVER);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS_BATTERY_SAVER);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else {
            Log.d("BATTERY_SAVER", "OFF");
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.9533, -3.1883), 10.0f));

        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new MarkerItemRenderer());

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mMap.getUiSettings().setMapToolbarEnabled(false);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mCurrentLocation = location;

        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();


        if (mMarkerItems != null) {
            ArrayList<MarkerItem> pickedUpItems = new ArrayList<>();
            for (MarkerItem markerItem : mMarkerItems) {
                if (location.distanceTo(markerItem.getLocation()) <= PICKUP_DISTANCE_IN_METERS) {
                    pickedUpItems.add(markerItem);
                }
            }

            boolean itemsCollected = false;

            for (MarkerItem pickedUpItem : pickedUpItems) {
                mMarkerItems.remove(pickedUpItem);
                mClusterManager.removeItem(pickedUpItem);
                storeMarker(pickedUpItem);
                incrementLetterCount(pickedUpItem.getLabel());
                itemsCollected = true;
            }

            boolean markerResetNeeded = forceMapRecluster || mShowOnlyClosest;
            boolean reclusteringNeeded = markerResetNeeded || itemsCollected;
            forceMapRecluster = false;

            if (markerResetNeeded) {
                mClusterManager.clearItems();
                if (mShowOnlyClosest) {
                    PriorityQueue<MarkerItem> closestItems = new PriorityQueue<>(MARKERS_TO_SHOW, distanceToUserLocation);
                    for (MarkerItem markerItem : mMarkerItems) {
                        closestItems.add(markerItem);
                        //Trim heap to contain MARKERS_TO_SHOW closest markers
                        if (closestItems.size() > MARKERS_TO_SHOW) {
                            closestItems.poll();
                        }
                    }
                    mClusterManager.addItems(closestItems);
                } else {
                    mClusterManager.addItems(mMarkerItems);
                }
            }
            if (reclusteringNeeded) {
                mClusterManager.cluster();
            }
        }
    }

    private void storeMarker(MarkerItem marker) {
        String letter = marker.getLabel();
        String lat = String.valueOf(marker.getPosition().latitude);
        String lng = String.valueOf(marker.getPosition().longitude);

        ContentValues values = new ContentValues();

        values.put(MarkerEntry.COLUMN_NAME_LETTER, letter);
        values.put(MarkerEntry.COLUMN_NAME_LAT, lat);
        values.put(MarkerEntry.COLUMN_NAME_LNG, lng);
        values.put(MarkerEntry.COLUMN_NAME_DATETIME, getDateTime());

        Log.d("MARKER COLLECTED SQLite", letter + " " + String.valueOf(lat) + " " + String.valueOf(lng) + " " + getDateTime());
        db.insert(MarkerEntry.TABLE_NAME, null, values);
    }

    private Comparator<MarkerItem> distanceToUserLocation = new Comparator<MarkerItem>() {
        @Override
        public int compare(MarkerItem itemA, MarkerItem itemB) {
            double distA = mCurrentLocation.distanceTo(itemA.getLocation());
            double distB = mCurrentLocation.distanceTo(itemB.getLocation());
            if (distB > distA) {
                return 1;
            } else if (distA > distB) {
                return -1;
            }
            return 0;
        }
    };

    private void incrementLetterCount(String letterLabel) {
        int oldValue = sharedPrefs.getInt(letterLabel, 0);
        int updatedValue = oldValue + 1;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(letterLabel, updatedValue).apply();
    }

    private void updateUI() {
        if (mCurrentLocation != null) {
//            String updateText = String.valueOf(mCurrentLocation.getLatitude()) + " " +
//                    String.valueOf(mCurrentLocation.getLongitude());
//            Toast.makeText(this, updateText, Toast.LENGTH_LONG).show();
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
            Log.d("BATTERY SAVER", "START LOCATION UPDATES");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.d(TAG, e.toString());
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void restartLocationUpdates() {
        stopLocationUpdates();
        startLocationUpdates();
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String pref) {
        updateApplicationPrefs();

        if (pref.equals(getString(R.string.pref_only_show_closest))) {
            forceMapRecluster = true;
        }

        if (pref.equals(getString(R.string.pref_battery_saver))) {
            createLocationRequest();
            restartLocationUpdates();
        }

    }

    private class MarkerItemRenderer extends DefaultClusterRenderer<MarkerItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());

        public MarkerItemRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerItem markerItem, MarkerOptions markerOptions) {
            //TODO Randomize color
            mIconGenerator.setStyle(IconGenerator.STYLE_PURPLE);

            Bitmap icon = mIconGenerator.makeIcon(markerItem.getLabel());
            markerOptions.anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

    }

    //Based on an example from Stack Overflow
    @Override
    public void onBackPressed() {
        Utility.confirmExitDialog(MapsActivity.this);
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}