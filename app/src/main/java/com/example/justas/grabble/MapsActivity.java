package com.example.justas.grabble;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ClusterManager<MarkerItem> mClusterManager;

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


    }

    MarkerItem edinburgh_a = new MarkerItem("A", 55.9533, -3.1883);
    MarkerItem edinburgh_b = new MarkerItem("B", 55.9523, -3.1873);
    MarkerItem edinburgh_c = new MarkerItem("C", 55.9513, -3.1893);

    MarkerItem[] markerItems = new MarkerItem[]{edinburgh_a, edinburgh_b, edinburgh_c};

    private void addItems() {

        for (MarkerItem marker : this.markerItems) {
            mClusterManager.addItem(marker);
        }

        double lat = 55.9543;
        double lng = -3.1893;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 40; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MarkerItem offsetItem = new MarkerItem("Z", lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.9533, -3.1883), 10.0f));

        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new MarkerItemRenderer());

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        addItems();
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
