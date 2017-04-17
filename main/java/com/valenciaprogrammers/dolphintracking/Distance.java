package com.valenciaprogrammers.dolphintracking;
/**
 * Created by Isabel Tomaszewski on 2/10/17.
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

    public Distance(){
        mapForm = new MapForm();
        centerPoint = new PointF(0,0);
        touchedPoint = new PointF(0.0f,0.0f);
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

    public void setDistanceFromBase( int distance){
        this.distanceFromBase = distance;
    }

    public int calcDistanceFromBase() {

        float xPoint = Math.abs(touchedPoint.x - centerPoint.x);
        float yPoint = Math.abs(touchedPoint.y - centerPoint.y);
        double radius = Math.sqrt((xPoint * xPoint) + (yPoint * yPoint));

        double rad1 = radius + mapForm.getFirstCircleDistanceInPoints();
        double rad2 = (((rad1)/ mapForm.getEquiDistanceInPoints()) * mapForm.getMetersDistancePerCircle());
        int ret = (int) (rad2 + rad2/mapForm.getMetersDistancePerCircle() );
        return (ret<0 ? 0 : ret);
    }

    @Override
    public String toString() {
        return "Distance From Base : " + this.distanceFromBase;
    }
}

