package com.valenciaprogrammers.dolphintracking;

/*
    Created by Brannon Martin

    The purpose of this class is to interact with the database.  This class does not do sort any information, that is done in other classes.
    This class simply handles the select, insert, delete, and update commands with the tables in the database.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class ADDatabaseHelper extends SQLiteOpenHelper
{

    private SQLiteDatabase dolphinDB = null;
    private Context context;

    public ADDatabaseHelper(Context context)
    {
        super(context, DolphinContract.DB_NAME, null, DolphinContract.DB_VERSION);
        Log.d("Debug","Enter databasehelper");
        dolphinDB = this.getWritableDatabase();  // This line may get deleted later.  This is just to create a database on startup for now
        Log.d("database info","**** "+dolphinDB.toString());
        this.context = context;
        this.onCreate(dolphinDB);
    }

    @Override
    public void onCreate(SQLiteDatabase db)  // Creates the database with the tables if it does not already exist
    {
        Log.d("Debug","Enter on create");

        db.execSQL(DolphinContract.ObservationTable.sqlObservationStatement);
        db.execSQL(DolphinContract.DolphinTable.sqlDolphinStatement);
        Log.d("Success", "Tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)  //  Not currently used
    {

    }

    public Cursor readAllDataFromDB() // This method will read all the info from the Dolphin Table and return the cursor object
    {
        String tableSelection = DolphinContract.DolphinTable.DOLPHIN_TABLE_NAME + " LEFT OUTER JOIN " + DolphinContract.ObservationTable.OBSERVATION_TABLE_NAME
                + " ON  " + DolphinContract.DolphinTable.DOLPHIN_TABLE_ID + " = " + DolphinContract.ObservationTable.OBSERVATION_TABLE_ID;

        //  The columns that will be selected in the query.  All of them in this case.
        String[] projection = {
                DolphinContract.DolphinTable.DOLPHIN_TABLE_ID,
                DolphinContract.DolphinTable.OBSERVED_DATE_TIME,
                DolphinContract.DolphinTable.ENTERED_DATE_TIME,
                DolphinContract.DolphinTable.DISTANCE_FROM_BOAT,
                DolphinContract.DolphinTable.LOCATION_FROM_BOAT,
                DolphinContract.DolphinTable.LOCATION_HEADING,
                DolphinContract.DolphinTable.SWIMMING_DIRECTION,
                DolphinContract.DolphinTable.SWIMMING_HEADING,
                DolphinContract.DolphinTable.BASE_GPS_LATITUDE,
                DolphinContract.DolphinTable.BASE_GPS_LONGITUDE,
                DolphinContract.DolphinTable.BASE_GPS_ACCURACY,
                DolphinContract.DolphinTable.BASE_HEADING,
                DolphinContract.DolphinTable.BASE_COMPASS_HEADING,
                DolphinContract.DolphinTable.SPECIES,
                DolphinContract.DolphinTable.SIZE,
                DolphinContract.DolphinTable.COLOR,
                DolphinContract.DolphinTable.ENERGY_LEVEL,
                DolphinContract.DolphinTable.SWIMMING_ACTIVITY,
                DolphinContract.DolphinTable.DOLPHIN_ACOUSTICS,
                DolphinContract.DolphinTable.DOLPHIN_BEHAVIOR,
                DolphinContract.DolphinTable.SOCIAL_GROUPING,
                DolphinContract.DolphinTable.DOLPHIN_GROUP_CODE,
                DolphinContract.DolphinTable.ADDITIONAL_BEHAVIOR,
                DolphinContract.DolphinTable.AUDIO_FILE_NAME,
                DolphinContract.DolphinTable.OBSERVATION_LOCATION_ID,
                DolphinContract.ObservationTable.OBSERVATION_TABLE_ID,
                DolphinContract.ObservationTable.LOCATION_NAME,
                DolphinContract.ObservationTable.WEATHER,
                DolphinContract.ObservationTable.START_DATE_TIME,
                DolphinContract.ObservationTable.END_DATE_TIME,
                DolphinContract.ObservationTable.WATER_DEPTH,
                DolphinContract.ObservationTable.WATER_TEMPERATURE,
                DolphinContract.ObservationTable.START_GPS_LATITUDE,
                DolphinContract.ObservationTable.START_GPS_LONGITUDE,
                DolphinContract.ObservationTable.END_GPS_LATITUDE,
                DolphinContract.ObservationTable.END_GPS_LONGITUDE
        };

        // Used to sort the entries by the date/time they were saved starting with the newest at the top.  This can be changed later if need be
        String sortOrder = DolphinContract.DolphinTable.OBSERVED_DATE_TIME + " DESC";

        Cursor dolphinCursor = dolphinDB.query(tableSelection, projection, null, null, null, null,sortOrder);

        return dolphinCursor;
    }

    public long SaveDolphinData(DolphinSighting dd) // This method adds a DolphinData object to the database and returns the ID that is auto created for that entry
    {
        long id = -1;
        assert dd != null;  // app will crash here if null object is passed

        try
        {
            ContentValues cv = new ContentValues();
            cv.put(DolphinContract.DolphinTable.ENTERED_DATE_TIME, dd.getDateTimeEntered());
            cv.put(DolphinContract.DolphinTable.OBSERVED_DATE_TIME, dd.getDateTimeOfObservation());
            cv.put(DolphinContract.DolphinTable.DISTANCE_FROM_BOAT, dd.getSightingLocation().getDistance().getDistanceFromBase());
            cv.put(DolphinContract.DolphinTable.LOCATION_FROM_BOAT, dd.getSightingLocation().getMapLocation().getArcLocation());
            cv.put(DolphinContract.DolphinTable.LOCATION_HEADING, dd.getSightingLocation().getMapLocation().getLocationHeading());
            cv.put(DolphinContract.DolphinTable.SWIMMING_DIRECTION, dd.getSightingLocation().getDirectionMoving().getArcLocation());
            cv.put(DolphinContract.DolphinTable.SWIMMING_HEADING, dd.getSightingLocation().getDirectionMoving().getLocationHeading());
            cv.put(DolphinContract.DolphinTable.BASE_HEADING, dd.getSightingLocation().getBaseHeading());
            cv.put(DolphinContract.DolphinTable.BASE_COMPASS_HEADING, dd.getSightingLocation().getBaseCompassHeading());
            cv.put(DolphinContract.DolphinTable.BASE_GPS_LATITUDE, dd.getSightingLocation().getBaseGPSLatitude());
            cv.put(DolphinContract.DolphinTable.BASE_GPS_LONGITUDE, dd.getSightingLocation().getBaseGPSLongitude());
            cv.put(DolphinContract.DolphinTable.BASE_GPS_ACCURACY, dd.getSightingLocation().getBaseGPSAccuracy());
            cv.put(DolphinContract.DolphinTable.SPECIES, dd.getDolphinSpecies());
            cv.put(DolphinContract.DolphinTable.SIZE, dd.getDolphinSize());
            cv.put(DolphinContract.DolphinTable.COLOR, dd.getDolphinColor());
            cv.put(DolphinContract.DolphinTable.ENERGY_LEVEL, dd.getDolphinEnergy());
            cv.put(DolphinContract.DolphinTable.SWIMMING_ACTIVITY, dd.getDolphinSwimmingActivity());
            cv.put(DolphinContract.DolphinTable.DOLPHIN_ACOUSTICS, dd.getDolphinAcoustics());
            cv.put(DolphinContract.DolphinTable.DOLPHIN_BEHAVIOR, dd.getDolphinBehavior());
            cv.put(DolphinContract.DolphinTable.SOCIAL_GROUPING, dd.getDolphinSocialGrouping());
            cv.put(DolphinContract.DolphinTable.DOLPHIN_GROUP_CODE, dd.getDolphinGroupCode());
            cv.put(DolphinContract.DolphinTable.ADDITIONAL_BEHAVIOR, dd.getDolphinAdditionalObservation());
            cv.put(DolphinContract.DolphinTable.AUDIO_FILE_NAME, dd.getDolphinAudioFileName());
            cv.put(DolphinContract.DolphinTable.OBSERVATION_LOCATION_ID,0);
            Log.d("Write to DB","*********************   Inserting data");
            id = dolphinDB.insert(DolphinContract.DolphinTable.DOLPHIN_TABLE_NAME, null, cv);
            Log.d("Write to DB","*********************   Inserting data.   Record id "+id);
        }

        catch (SQLiteException e)
        {
            Log.d("Error Encountered! ", "*******   Error writing to DolphinData table \n" + e.toString());
            return -1;
        }

        finally
        {
            return id;
        }
    }

    public long SaveObservationEnvironment(ObservationEnvironment oe)  // This method adds an ObservationEnvironment object to the database and returns the ID
    {
        long id = -1;
        assert oe != null;

        try
        {
            ContentValues cv = new ContentValues();
            cv.put(DolphinContract.ObservationTable.LOCATION_NAME, oe.getLocationName());
            cv.put(DolphinContract.ObservationTable.WEATHER, oe.getWeather());
            cv.put(DolphinContract.ObservationTable.START_DATE_TIME, oe.getStartDateTime());
            cv.put(DolphinContract.ObservationTable.END_DATE_TIME, oe.getEndDateTime());
            cv.put(DolphinContract.ObservationTable.START_GPS_LATITUDE, oe.getLocationStartGPSLatitude());
            cv.put(DolphinContract.ObservationTable.START_GPS_LONGITUDE, oe.getLocationStartGPSLongitude());
            cv.put(DolphinContract.ObservationTable.END_GPS_LATITUDE, oe.getLocationEndGPSLatitude());
            cv.put(DolphinContract.ObservationTable.END_GPS_LONGITUDE, oe.getLocationEndGPSLongitude());
            cv.put(DolphinContract.ObservationTable.WATER_DEPTH, oe.getWaterDepth());
            cv.put(DolphinContract.ObservationTable.WATER_DEPTH, oe.getWaterTemperature());

            id = dolphinDB.insert(DolphinContract.ObservationTable.OBSERVATION_TABLE_NAME, null, cv);
        }

        catch (SQLiteException e)
        {
            Log.d("Error! ", e.toString());
        }

        finally
        {
            //     dolphinDB.close();
            return id;
        }
    }

    public void DeleteAllInfo()
    {
        dolphinDB.execSQL(DolphinContract.ObservationTable.sqlObservationDropTableStatement);
        dolphinDB.execSQL(DolphinContract.ObservationTable.sqlObservationStatement);

        dolphinDB.execSQL(DolphinContract.DolphinTable.sqlDolphinDropTableStatement);
        dolphinDB.execSQL(DolphinContract.DolphinTable.sqlDolphinStatement);
        Toast.makeText(context, "Database has been cleared!", Toast.LENGTH_LONG).show();
    }
}