package com.example.justas.grabble;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerItem implements ClusterItem {
    private LatLng latLng;
    private String label;

    public MarkerItem(String label, double lat, double lng) {
        this.label = label;
        this.latLng = new LatLng(lat, lng);
    }

    public MarkerItem(String label, LatLng latLng) {
        this.label = label;
        this.latLng = latLng;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public String getLabel() {
        return label;
    }
}
