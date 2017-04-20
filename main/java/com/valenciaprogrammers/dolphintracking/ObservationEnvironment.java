package com.valenciaprogrammers.dolphintracking;

import java.util.ArrayList;


/**
 * Created by Isabel Tomaszewski on 2/10/17.
 * <p>
 * This class is for the info on where the observations are taking place, but not the actual dolphin observations.
 * At this release,  this is not implemented.
 * The intent was to have a screen that would allow the user to enter information about
 * the location where the sightings are being recorded.
 * This information would be linked to each dolphin sighting data saved by inserting the id
 * of the environment record to each entry saved.
 */

public class ObservationEnvironment {
    private static ArrayList<ObservationEnvironment> allDolphinData;  // List of all the info in the database

    private long id;
    private String locationName;
    private String weather;
    private double waterDepth;
    private double waterTemperature;

    private String startDateTime;
    private String endDateTime;
    private String locationStartGPSLatitude;
    private String locationStartGPSLongitude;
    private String locationEndGPSLatitude;
    private String locationEndGPSLongitude;

    private ArrayList<DolphinSighting> dolphinList = new ArrayList<>();  // One group can contain multiple dolphins

    public ArrayList<DolphinSighting> getDolphinList() {
        return dolphinList;
    }

    public void setDolphinList(ArrayList<DolphinSighting> dolphinList) {
        this.dolphinList = dolphinList;
    }

    public double getWaterDepth() {
        return waterDepth;
    }

    public void setWaterDepth(double waterDepth) {
        this.waterDepth = waterDepth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public double getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(double waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getLocationStartGPSLatitude() {
        return locationStartGPSLatitude;
    }

    public void setLocationStartGPSLatitude(String locationStartGPSLatitude) {
        this.locationStartGPSLatitude = locationStartGPSLatitude;
    }

    public String getLocationStartGPSLongitude() {
        return locationStartGPSLongitude;
    }

    public void setLocationStartGPSLongitude(String locationStartGPSLongitude) {
        this.locationStartGPSLongitude = locationStartGPSLongitude;
    }

    public String getLocationEndGPSLatitude() {
        return locationEndGPSLatitude;
    }

    public void setLocationEndGPSLatitude(String locationEndGPSLatitude) {
        this.locationEndGPSLatitude = locationEndGPSLatitude;
    }

    public String getLocationEndGPSLongitude() {
        return locationEndGPSLongitude;
    }

    public void setLocationEndGPSLongitude(String locationEndGPSLongitude) {
        this.locationEndGPSLongitude = locationEndGPSLongitude;
    }
}
