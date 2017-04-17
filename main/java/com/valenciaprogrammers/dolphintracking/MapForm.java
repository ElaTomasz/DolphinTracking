package com.valenciaprogrammers.dolphintracking;

/**
 * Created by Isabel Tomaszewski on 2/10/17.
 */

public class MapForm {

    private float metersDistancePerCircle;        // the # of meters per circle on the map
    private float equiDistanceInPoints;           // # of points for the distance per circles
    private float firstCircleDistanceInPoints;    // # of points from center to first circle
    private float numberOfCircles;

    public MapForm(float metersDistancePerCircle, float equiDistanceInPoints,
                   float firstCircleDistanceInPoints, float numberOfCircles) {

        this.metersDistancePerCircle = metersDistancePerCircle;
        this.equiDistanceInPoints = equiDistanceInPoints;
        this.firstCircleDistanceInPoints = firstCircleDistanceInPoints;
        this.numberOfCircles = numberOfCircles;
    }

    public MapForm(){
        metersDistancePerCircle = 10.f;
        equiDistanceInPoints = 40.0f;
        firstCircleDistanceInPoints = 0.0f;
        numberOfCircles = 10.f;
    }

    public float getMetersDistancePerCircle() {
        return metersDistancePerCircle;
    }

    public void setMetersDistancePerCircle(float metersDistancePerCircle) {
        this.metersDistancePerCircle = metersDistancePerCircle;
    }

    public float getEquiDistanceInPoints() {
        return equiDistanceInPoints;
    }

    public void setEquiDistanceInPoints(float equiDistanceInPoints) {
        this.equiDistanceInPoints = equiDistanceInPoints;
    }

    public float getFirstCircleDistanceInPoints() {
        return firstCircleDistanceInPoints;
    }

    public void setFirstCircleDistanceInPoints(float firstCircleDistanceInPoints) {
        this.firstCircleDistanceInPoints = firstCircleDistanceInPoints;
    }

    public float getNumberOfCircles() {
        return numberOfCircles;
    }

    public void setNumberOfCircles(float numberOfCircles) {
        this.numberOfCircles = numberOfCircles;
    }
}
