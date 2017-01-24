package com.example.justas.grabble;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class MarkerItemUnitTest {

    @Test
    public void equality_isCorrect() {
        MarkerItem markerItemA = new MarkerItem("A", 25.0, 1.000005);
        MarkerItem markerItemB = new MarkerItem("A", 25, 1.000005);

        assertTrue(markerItemA.equals(markerItemB));
    }

    @Test
    public void inequality_isCorrect() {
        MarkerItem markerItemA = new MarkerItem("A", 25.000001, 1.000005);
        MarkerItem markerItemB = new MarkerItem("A", 25.000001, 1.000006);

        assertFalse(markerItemA.equals(markerItemB));
    }

    @Test
    public void getPosition_isCorrect() {
        MarkerItem markerItem = new MarkerItem("A", 0, 0);

        assertEquals(new LatLng(0, 0), markerItem.getPosition());
    }


    @Test
    public void equalItems_haveEqualHashCodes() {
        MarkerItem markerItemA = new MarkerItem("A", 25.0, 5.55);
        MarkerItem markerItemB = new MarkerItem(null, 25.00000, 5.550000);
        markerItemB.letter = "A";

        assertEquals(markerItemA.hashCode(), markerItemB.hashCode());
    }


}
