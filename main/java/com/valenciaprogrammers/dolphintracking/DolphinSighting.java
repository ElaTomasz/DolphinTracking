package com.valenciaprogrammers.dolphintracking;

/**
 * Created by Isabel Tomaszewski on 2/16/17.
 *
 * Class for all the data of dalphin sighting data
 */

public class DolphinSighting {
    private int sighting_id;
    private String dateTimeOfObservation;
    private String dateTimeEntered;
    private String dolphinSpecies;
    private String dolphinSize;
    private String dolphinColor;
    private String dolphinEnergy;
    private String dolphinSwimmingActivity;
    private String dolphinAcoustics;
    private String dolphinBehavior;
    private String dolphinSocialGrouping;
    private String dolphinAdditionalObservation;
    private String dolphinGroupCode;
    private String dolphinAudioFileName;
    private SightingLocation sightingLocation;


    public DolphinSighting(){
        this.sighting_id = -1;
        this.dateTimeEntered = Time.getFullDateTime();
        this.dateTimeOfObservation = Time.getFullDateTime();
        this.dolphinSpecies = "";
        this.dolphinSize = "";
        this.dolphinColor = "";
        this.dolphinEnergy = "";
        this.dolphinSwimmingActivity = "";
        this.dolphinAcoustics = "";
        this.dolphinBehavior = "";
        this.dolphinSocialGrouping = "";
        this.dolphinGroupCode = "";
        this.dolphinAdditionalObservation = "";
        this.sightingLocation = new SightingLocation();
    }

    public int getSighting_id() {
        return sighting_id;
    }

    public void setSighting_id(int sighting_id) {
        this.sighting_id = sighting_id;
    }

    public String getDolphinSize() {
        return dolphinSize;
    }

    public void setDolphinSize(String dolphinSize) {
        this.dolphinSize = dolphinSize;
    }

    public String getDolphinSpecies() {
        return dolphinSpecies;
    }

    public void setDolphinSpecies(String dolphinSpecies) {
        this.dolphinSpecies = dolphinSpecies;
    }

    public String getDolphinColor() {
        return dolphinColor;
    }

    public void setDolphinColor(String dolphinColor) {
        this.dolphinColor = dolphinColor;
    }

    public String getDolphinEnergy() {
        return dolphinEnergy;
    }

    public void setDolphinEnergy(String dolphinEnergy) {
        this.dolphinEnergy = dolphinEnergy;
    }

    public String getDolphinBehavior() {
        return dolphinBehavior;
    }

    public void setDolphinBehavior(String dolphinBehavior) {
        this.dolphinBehavior = dolphinBehavior;
    }

    public SightingLocation getSightingLocation() {
        return sightingLocation;
    }

    public void setSightingLocation(SightingLocation sightingLocation) {
        this.sightingLocation = sightingLocation;
    }

    public String getDateTimeOfObservation() {
        return dateTimeOfObservation;
    }

    public void setDateTimeOfObservation(String dateTimeOfObservation) {
        this.dateTimeOfObservation = dateTimeOfObservation;
    }

    public String getDateTimeEntered() {
        return dateTimeEntered;
    }

    public void setDateTimeEntered(String dateTimeEntered) {
        this.dateTimeEntered = dateTimeEntered;
    }

    public String getDolphinSwimmingActivity() {
        return dolphinSwimmingActivity;
    }

    public void setDolphinSwimmingActivity(String dolphinSwimmingActivity) {
        this.dolphinSwimmingActivity = dolphinSwimmingActivity;
    }

    public String getDolphinAcoustics() {
        return dolphinAcoustics;
    }

    public void setDolphinAcoustics(String dolphinAcoustics) {
        this.dolphinAcoustics = dolphinAcoustics;
    }

    public String getDolphinSocialGrouping() {
        return dolphinSocialGrouping;
    }

    public void setDolphinSocialGrouping(String dolphinSocialGrouping) {
        this.dolphinSocialGrouping = dolphinSocialGrouping;
    }

    public String getDolphinAdditionalObservation() {
        return dolphinAdditionalObservation;
    }

    public void setDolphinAdditionalObservation(String dolphinAdditionalObservation) {
        this.dolphinAdditionalObservation = dolphinAdditionalObservation;
    }

    public String getDolphinGroupCode() {
        return dolphinGroupCode;
    }

    public void setDolphinGroupCode(String dolphinGroupCode) {
        this.dolphinGroupCode = dolphinGroupCode;
    }

    public String getDolphinAudioFileName() {
        return dolphinAudioFileName;
    }

    public void setDolphinAudioFileName(String dolphinAudioFileName) {
        this.dolphinAudioFileName = dolphinAudioFileName;
    }

    private String addSlash(String text){
        return (text.length() == 0) ? "" : " / ";
    }

    public String shortToString() {
        String line1, line2, line4, result;
        line1 = dolphinSpecies;
        line1 += (dolphinSize.length() == 0) ? "" : (addSlash(line1)+"Size: "+ dolphinSize);
        line1 += (dolphinColor.length() == 0) ? "" : (addSlash(line1)+"Color: "+ dolphinColor);
        line1 += (dolphinEnergy.length() == 0) ? "" : (addSlash(line1)+"Energy: "+ dolphinEnergy);
        line1 += (dolphinSwimmingActivity.length() == 0) ? "" : (addSlash(line1)+"Swimming: " + dolphinSwimmingActivity);
        line2 = (dolphinAcoustics.length() == 0) ? "" : ("Acoustics: "+ dolphinAcoustics);
        line2 += (dolphinBehavior.length() == 0) ? "" : (addSlash(line1)+"Behavior: "+ dolphinBehavior);
        line2 += (dolphinSocialGrouping.length() == 0) ? "" : (addSlash(line2)+"Social Grouping: "+ dolphinSocialGrouping);
        line2 += (dolphinGroupCode.length() == 0) ? "" : (addSlash(line2)+"Group Code: "+ dolphinGroupCode);
        line4 = dateTimeOfObservation+ " " + sightingLocation.toString();
        result = line1 + (line2.length() == 0 ? "" : "\n"+line2)+(line4.length() == 0 ? "" : "\n"+line4);
        return result;
    }

    @Override
    public String toString() {
        String line12, line3, result;
        line12 = shortToString();
        line3 = (dolphinAdditionalObservation.length() == 0) ? "" : ("Additional Observation: "
                        + dolphinAdditionalObservation.substring(0,Math.min(120,dolphinAdditionalObservation.length())));
        result = line12 + (line3.length() == 0 ? "" : "\n"+line3);
        return result;
    }
}
