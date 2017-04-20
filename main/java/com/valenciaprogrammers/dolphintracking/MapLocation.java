package com.valenciaprogrammers.dolphintracking;

import android.graphics.PointF;

/**
 * Created by Isabel Tomaszewski on 2/10/17.
 * <p>
 * Class to determine the location (arc) of the point on the map and its heading.
 * This class can be used to return the location of the dolphin 'around' the boat by
 * getting the arc of the spot relative to the center point.
 * When the center of the polar map is passed for the center,  the arc determines the
 * location dolphin around the boat.
 * When the point touched is passed for the center,  the arc determines the direction
 * the dolphin is moving.
 */

public class MapLocation {

    private PointF locationPoint;
    private PointF centerPoint;
    private float arcLocation;
    private String locationHeading;

    public MapLocation(PointF centerPoint, PointF locationPoint) {
        this.centerPoint = centerPoint;
        this.locationPoint = locationPoint;
        this.arcLocation = this.calcArcLocation();
        this.locationHeading = this.calcLocationHeading();
    }

    public MapLocation() {
        this.locationPoint = new PointF(0, 0);
        this.centerPoint = new PointF(0, 0);
        this.arcLocation = 0;
        this.locationHeading = "";
    }

    public float getArcLocation() {
        return arcLocation;
    }

    public void setArcLocation(float arcLocation) {
        this.arcLocation = arcLocation;
    }

    //
//  method to calculate the angle to the x-axis.  use the x and y points relative
//  to the center x and y to determine the arc of the point from 0 degrees - 359 degrees.
    public float calcArcLocation() {
        double radius = this.calcRadius();
        double sinAngle = Math.abs(locationPoint.x - centerPoint.x) / radius;
        float angle = (float) Math.toDegrees(Math.asin(sinAngle));
        float arc = 0;

        if ((locationPoint.x > centerPoint.x) & (locationPoint.y < centerPoint.y)) {
            arc = angle;
        } else {
            if ((locationPoint.y > centerPoint.y) & (locationPoint.x > centerPoint.x)) {
                arc = 90 + (90 - angle);
            } else {
                if ((locationPoint.y > centerPoint.y) & (locationPoint.x < centerPoint.x)) {
                    arc = 180 + angle;
                } else {
                    if ((locationPoint.y < centerPoint.y) & (locationPoint.x < centerPoint.x)) {
                        arc = 270 + (90 - angle);
                    }
                }
            }
        }
        return arc;
    }

    public String getLocationHeading() {
        return locationHeading;
    }

    public void setLocationHeading(String heading) {
        this.locationHeading = heading;
    }

    public String calcLocationHeading() {
        return CompassHeading.getBearing((int) this.arcLocation);
    }

    private double calcRadius() {
        float xPoint = Math.abs(locationPoint.x - centerPoint.x);
        float yPoint = Math.abs(locationPoint.y - centerPoint.y);
        return Math.sqrt((xPoint * xPoint) + (yPoint * yPoint));
    }

    @Override
    public String toString() {
        return " Location : " + this.arcLocation
                + " " + this.locationHeading;
    }
}
