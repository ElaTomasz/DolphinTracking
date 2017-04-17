package com.valenciaprogrammers.dolphintracking;

import android.graphics.Point;
import java.text.DecimalFormat;


/**
 * Created by Isabel Tomaszewski on 2/10/17.
 *
 * Class for the sighting location,  ie.   distance from base, location from base and
 *                                         direction moving
 */

public class SightingLocation {
    private MapForm mapForm = new MapForm();
    private Distance distance = new Distance();
    private MapLocation mapLocation = new MapLocation();
    private MapLocation directionMoving = new MapLocation();
    private int baseHeading;
    private String baseCompassHeading;
    private double baseGPSLatitude;
    private double baseGPSLongitude;
    private int baseGPSAccuracy;

    public SightingLocation(MapForm mapForm, Distance distance, MapLocation mapLocation) {
        this.mapForm = mapForm;
        this.distance = distance;
        this.mapLocation = mapLocation;
        this.directionMoving = new MapLocation();
        this.baseHeading = -1;
        this.baseCompassHeading = "";
        this.baseGPSLatitude = -1;
        this.baseGPSLongitude = -1;
        this.baseGPSAccuracy = -1;
    }

    public SightingLocation() {
        mapForm = new MapForm();
        distance = new Distance();
        mapLocation = new MapLocation();
        directionMoving = new MapLocation();
        this.baseHeading = -1;
        this.baseCompassHeading = "";
        this.baseGPSLatitude = -1;
        this.baseGPSLongitude = -1;
        this.baseGPSAccuracy = -1;
    }

    public MapForm getMapForm() {
        return mapForm;
    }

    public void setMapForm(MapForm mapForm) {
        this.mapForm = mapForm;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public MapLocation getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(MapLocation mapLocation) {
        this.mapLocation = mapLocation;
    }

    public MapLocation getDirectionMoving() {
        return directionMoving;
    }

    public void setDirectionMoving(MapLocation directionMoving) {
        this.directionMoving = directionMoving;
    }

    public int getBaseHeading() {
        return baseHeading;
    }

    public void setBaseHeading(int baseHeading) {
        this.baseHeading = baseHeading;
    }

    public String getBaseCompassHeading() {
        return baseCompassHeading;
    }

    public void setBaseCompassHeading(String baseCompassHeading) {
        this.baseCompassHeading = baseCompassHeading;
    }

    public void setBaseGPSAccuracy(int baseGPSAccuracy) {
        this.baseGPSAccuracy = baseGPSAccuracy;
    }

    public int getBaseGPSAccuracy() {
        return baseGPSAccuracy;
    }

    public void setBaseGPSAccuracy(double baseGPSAccuracy) {
        this.baseGPSAccuracy = (int) baseGPSAccuracy;
    }

    public double getBaseGPSLatitude() {
        return baseGPSLatitude;
    }

    public void setBaseGPSLatitude(double baseGPSLatitude) {
        this.baseGPSLatitude = baseGPSLatitude;
    }

    public double getBaseGPSLongitude() {
        return baseGPSLongitude;
    }

    public void setBaseGPSLongitude(double baseGPSLongitude) {
        this.baseGPSLongitude = baseGPSLongitude;
    }

    @Override
    public String toString() {
        String result = "Spotted " + this.distance.getDistanceFromBase() + " meters from boat "
                + " at " + (int)this.mapLocation.getArcLocation()
                + " (" + this.mapLocation.getLocationHeading()
                + ") moving " + (int) this.directionMoving.getArcLocation()
                + " (" + this.directionMoving.getLocationHeading() + ")";
        if (baseHeading == -1) {
            result += "\n";
        }else {
            result += "\nBase Heading " + baseHeading + " (" + baseCompassHeading + ")";
        }
        if (baseGPSAccuracy != -1) {
            result += "  GPS (Lat, Lon) (" + String.format("%.5f", baseGPSLatitude)
                    + " , " + String.format("%.5f", baseGPSLongitude)
                    + ") Accuracy: " + baseGPSAccuracy;
        }
        return result;
    }
}
