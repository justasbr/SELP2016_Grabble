package com.example.justas.grabble;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerItem implements ClusterItem {
    public String letter;
    public Double lat;
    public Double lng;

    public MarkerItem(String letter, double lat, double lng) {
        this.letter = letter;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public LatLng getPosition() {
        if (lat != null && lng != null) {
            return new LatLng(lat, lng);
        } else {
            return null;
        }
    }

    public Location getLocation() {
        if (lat != null && lng != null) {
            Location location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(lng);
            return location;
        } else {
            return null;
        }
    }

    public String getLabel() {
        return letter;
    }
}
