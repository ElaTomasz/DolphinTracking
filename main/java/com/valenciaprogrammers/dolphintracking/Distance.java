package com.valenciaprogrammers.dolphintracking;
/**
 * Created by Isabel Tomaszewski on 2/10/17.
 * This class is used to calculate the distance of a point touched from the center point
 */

import android.graphics.PointF;

public class Distance {

    private MapForm mapForm;
    private PointF centerPoint;
    private PointF touchedPoint;
    private int distanceFromBase = 0;

    public Distance(MapForm mapForm, PointF centerPoint, PointF touchedPoint) {
        this.mapForm = mapForm;
        this.centerPoint = centerPoint;
        this.touchedPoint = touchedPoint;
        this.distanceFromBase = this.calcDistanceFromBase();
    }

    //  initialize the distance object
    public Distance() {
        mapForm = new MapForm();
        centerPoint = new PointF(0, 0);
        touchedPoint = new PointF(0.0f, 0.0f);
        distanceFromBase = 0;
    }

    public MapForm getMapForm() {
        return mapForm;
    }

    public void setMapForm(MapForm mapForm) {
        this.mapForm = mapForm;
    }

    public PointF getTouchedPoint() {
        return touchedPoint;
    }

    public void setTouchedPoint(PointF touchedPoint) {
        this.touchedPoint = touchedPoint;
    }

    public int getDistanceFromBase() {
        return distanceFromBase;
    }

    public void setDistanceFromBase(int distance) {
        this.distanceFromBase = distance;
    }

    public int calcDistanceFromBase() {
        float xPoint = Math.abs(touchedPoint.x - centerPoint.x);
        float yPoint = Math.abs(touchedPoint.y - centerPoint.y);
        double radius = Math.sqrt((xPoint * xPoint) + (yPoint * yPoint));

        double rad1 = radius + mapForm.getFirstCircleDistanceInPoints();
        double rad2 = (((rad1) / mapForm.getEquiDistanceInPoints()) * mapForm.getMetersDistancePerCircle());
        // adjust the distance to account for the unequal distance betweeen the circles in the polat map image
        // if the image is replaced with one that has equidistance between the circles,
        //                                                              the logic below is not necessary
        int circlesOut = (int) (rad2 / mapForm.getMetersDistancePerCircle());
        int [] adjust = {1,2,1,1,2,3,2,3,4,3,3,3,3};
        int ret = (int) rad2 + adjust[circlesOut];
        return (ret < 0 ? 0 : ret);
    }

    @Override
    public String toString() {
        return "Distance From Base : " + this.distanceFromBase;
    }
}

