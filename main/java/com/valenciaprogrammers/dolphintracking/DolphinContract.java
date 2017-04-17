package com.valenciaprogrammers.dolphintracking;

import android.provider.BaseColumns;

/**
 * Created by Brannon Martin on 2/8/2017.
 *
 * The purpose of the contract class is to define the constants of the database.  No methods go here.
 *
 * There is one class created per table to define the constants in each table, while the constants for the database are defined in the outer class
 */

public final class DolphinContract
{
    public static final String DB_NAME = "DolphinData.SQLite";
    public static final int DB_VERSION = 1;

    // index to the field in a read cursor dolphin data unioned with obervation table
    public static final int DOLPHIN_TABLE_ID_INDEX = 0;
    public static final int OBSERVED_DATE_TIME_INDEX = 1;
    public static final int ENTERED_DATE_TIME_INDEX = 2;
    public static final int DISTANCE_FROM_BOAT_INDEX = 3;
    public static final int LOCATION_FROM_BOAT_INDEX = 4;
    public static final int LOCATION_HEADING_INDEX = 5;
    public static final int SWIMMING_DIRECTION_INDEX = 6;
    public static final int SWIMMING_HEADING_INDEX = 7;
    public static final int BASE_GPS_LATITUDE_INDEX = 8;
    public static final int BASE_GPS_LONGITUDE_INDEX = 9;
    public static final int BASE_GPS_ACCURACY_INDEX = 10;
    public static final int BASE_HEADING_INDEX = 11;
    public static final int BASE_COMPASS_HEADING_INDEX = 12;
    public static final int SPECIES_INDEX = 13;
    public static final int SIZE_INDEX = 14;
    public static final int COLOR_INDEX = 15;
    public static final int ENERGY_LEVEL_INDEX = 16;
    public static final int SWIMMING_ACTIVITY_INDEX = 17;
    public static final int DOLPHIN_ACOUSTICS_INDEX = 18;
    public static final int DOLPHIN_BEHAVIOR_INDEX = 19;
    public static final int SOCIAL_GROUPING_INDEX = 20;
    public static final int DOLPHIN_GROUP_CODE_INDEX = 21;
    public static final int ADDITIONAL_BEHAVIOR_INDEX = 22;
    public static final int AUDIO_FILE_NAME_INDEX = 23;
    public static final int OBSERVATION_LOCATION_ID_INDEX = 24;
    public static final int LOCATION_NAME_INDEX = 25;
    public static final int WEATHER_INDEX = 26;
    public static final int START_DATE_TIME_INDEX = 27;
    public static final int END_DATE_TIME_INDEX = 28;
    public static final int WATER_DEPTH_INDEX = 29;
    public static final int WATER_TEMPERATURE_INDEX = 30;
    public static final int START_GPS_LATITUDE_INDEX = 31;
    public static final int START_GPS_LONGITUDE_INDEX = 32;
    public static final int END_GPS_LATITUDE_INDEX = 33;
    public static final int END_GPS_LONGITUDE_INDEX = 34;

    public static class ObservationTable implements BaseColumns
    {
        public static final String OBSERVATION_TABLE_NAME = "ObservationTable";
        public static final String OBSERVATION_TABLE_ID = "Observation_ID";
        public static final String LOCATION_NAME = "Location_Name";
        public static final String WEATHER = "Weather";
        public static final String START_DATE_TIME = "Start_DateTime";
        public static final String END_DATE_TIME = "End_DateTime";
        public static final String WATER_DEPTH = "Water_Depth";
        public static final String WATER_TEMPERATURE = "Water_Temp";
        public static final String START_GPS_LATITUDE = "GPS_Start_Latitude";
        public static final String START_GPS_LONGITUDE = "GPS_Start_Longitude";
        public static final String END_GPS_LATITUDE = "GPS_End_Latitude";
        public static final String END_GPS_LONGITUDE = "GPS_End_Longitude";

        public static final String sqlObservationDropTableStatement = "DROP TABLE "+ OBSERVATION_TABLE_NAME+";";

        public static final String sqlObservationStatement =
                "CREATE TABLE IF NOT EXISTS "
                + OBSERVATION_TABLE_NAME + " ("
                + OBSERVATION_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + LOCATION_NAME + " TEXT,"
                + WEATHER + " TEXT,"
                + START_DATE_TIME + " TEXT,"
                + END_DATE_TIME + " TEXT,"
                + WATER_DEPTH + " REAL,"
                + WATER_TEMPERATURE + " REAL,"
                + START_GPS_LATITUDE + " TEXT,"
                + START_GPS_LONGITUDE + " TEXT,"
                + END_GPS_LATITUDE + " TEXT,"
                + END_GPS_LONGITUDE + " TEXT"
                + ")";
    }


    public static class DolphinTable implements BaseColumns
    {
        public static final String DOLPHIN_TABLE_NAME = "DolphinTable";
        public static final String DOLPHIN_TABLE_ID = "Dolphin_ID";
        public static final String OBSERVED_DATE_TIME = "Observed_DateTime";
        public static final String ENTERED_DATE_TIME = "Entered_DateTime";
        public static final String DISTANCE_FROM_BOAT = "Distance_From_Boat";
        public static final String LOCATION_FROM_BOAT = "Location_From_Boat";
        public static final String LOCATION_HEADING = "Location_Heading";
        public static final String SWIMMING_DIRECTION = "Swimming_Direction";
        public static final String SWIMMING_HEADING = "Swimming_Heading";
        public static final String BASE_GPS_LATITUDE = "Base_GPS_Latitude";
        public static final String BASE_GPS_LONGITUDE = "Base_GPS_Longitude";
        public static final String BASE_GPS_ACCURACY = "Base_GPS_Accuracy";
        public static final String BASE_HEADING = "Base_Heading";
        public static final String BASE_COMPASS_HEADING = "Base_Compass_Heading";
        public static final String SPECIES = "Species";
        public static final String SIZE = "Size";
        public static final String COLOR = "Color";
        public static final String ENERGY_LEVEL = "Energy_Level";
        public static final String SWIMMING_ACTIVITY = "Swimming_Activity";
        public static final String DOLPHIN_ACOUSTICS = "Dolphin_Acoustics";
        public static final String DOLPHIN_BEHAVIOR = "Dolphin_Behavior";
        public static final String SOCIAL_GROUPING = "Social_Grouping";
        public static final String DOLPHIN_GROUP_CODE = "Dolphin_Group_Code";
        public static final String ADDITIONAL_BEHAVIOR = "Additional_Behavior";
        public static final String AUDIO_FILE_NAME = "Audio_File_Name";
        public static final String OBSERVATION_LOCATION_ID = "Observation_Location_ID";

        public static final String sqlDolphinDropTableStatement = "DROP TABLE "+ DOLPHIN_TABLE_NAME+";";

        public static final String sqlDolphinStatement =
                "CREATE TABLE IF NOT EXISTS " + DOLPHIN_TABLE_NAME
                        + " ("
                        + DOLPHIN_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                        + OBSERVED_DATE_TIME + " TEXT,"
                        + ENTERED_DATE_TIME + " TEXT,"
                        + DISTANCE_FROM_BOAT + " INTEGER,"
                        + LOCATION_FROM_BOAT + " INTEGER,"
                        + LOCATION_HEADING + " TEXT,"
                        + SWIMMING_DIRECTION + " INTEGER,"
                        + SWIMMING_HEADING + " TEXT,"
                        + BASE_HEADING + " INTEGER,"
                        + BASE_COMPASS_HEADING + " TEXT,"
                        + BASE_GPS_LATITUDE + " REAL,"
                        + BASE_GPS_LONGITUDE + " REAL,"
                        + BASE_GPS_ACCURACY + " INTEGER,"
                        + SPECIES + " TEXT,"
                        + SIZE + " TEXT,"
                        + COLOR + " TEXT,"
                        + ENERGY_LEVEL + " TEXT,"
                        + SWIMMING_ACTIVITY + " TEXT,"
                        + DOLPHIN_ACOUSTICS + " TEXT,"
                        + DOLPHIN_BEHAVIOR + " TEXT,"
                        + SOCIAL_GROUPING + " TEXT,"
                        + DOLPHIN_GROUP_CODE + " TEXT,"
                        + ADDITIONAL_BEHAVIOR + " TEXT,"
                        + AUDIO_FILE_NAME + " TEXT,"
                        + OBSERVATION_LOCATION_ID + " INTEGER"
                        + ")";
    }
}
