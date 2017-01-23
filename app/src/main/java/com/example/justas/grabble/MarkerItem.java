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

    private boolean hasMissingInformation() {
        return this.letter == null || this.lat == null || this.lng == null;
    }

    public String getLabel() {
        return letter;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof MarkerItem) {
            MarkerItem other = (MarkerItem) object;
            if (this.hasMissingInformation() || other.hasMissingInformation()) {
                return false;
            }
            return this.letter.equals(other.letter) && getPosition().equals(other.getPosition());
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        if (this.hasMissingInformation()) {
            return super.hashCode();
        }

        //Not the best hash function, but works well enough for our use case
        return this.letter.hashCode() + this.getPosition().hashCode();
    }


}
