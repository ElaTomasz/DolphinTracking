package com.valenciaprogrammers.dolphintracking;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Isabel Tomaszewski on 2/20/17.
 * <p>
 * Activity to retrieve all the data from the database tables
 * and display them in a ListView
 * <p>
 * Will be used later to also update or delete a record
 */

public class DolphinHistoryActivity extends AppCompatActivity {
    private ArrayList<DolphinSighting> allDolphinSightings;
    private ListView dolphinSightingListView;
    private Bundle savedInstanceState;
    private Cursor dataCursor;
    private ADDatabaseHelper dolphinDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_dolphin_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.historyToolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        // Enable the Up/Home button
        actionBar.setDisplayHomeAsUpEnabled(true);
        dolphinDBHelper = new ADDatabaseHelper(getApplicationContext());
        dataCursor = dolphinDBHelper.readAllDataFromDB();
        fillDolphinArrayList();

        ListView listView = (ListView) findViewById(R.id.list);

        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(),
                R.layout.dolphin_data_row, allDolphinSightings);
        listView.setAdapter(adapter);
    }

    // *************************************************************
    // manage History MenuItem access
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // control actions of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // **************************************************************
    // method to read the database and fill the array list with the data for display
    private void fillDolphinArrayList() {
        allDolphinSightings = new ArrayList<DolphinSighting>();
        DolphinSighting dolphinSighting;
        SightingLocation sightingLocation;
        Distance distance;
        MapLocation mapLocation;
        MapLocation movingLocation;

        while (dataCursor.moveToNext()) {
            dolphinSighting = new DolphinSighting();
            sightingLocation = new SightingLocation();
            distance = new Distance();
            mapLocation = new MapLocation();
            movingLocation = new MapLocation();
            dolphinSighting.setSighting_id(dataCursor.getInt(DolphinContract.DOLPHIN_TABLE_ID_INDEX));
            dolphinSighting.setDateTimeOfObservation(dataCursor.getString(DolphinContract.OBSERVED_DATE_TIME_INDEX));
            dolphinSighting.setDolphinSpecies(dataCursor.getString(DolphinContract.SPECIES_INDEX));
            dolphinSighting.setDolphinSize(dataCursor.getString(DolphinContract.SIZE_INDEX));
            dolphinSighting.setDolphinColor(dataCursor.getString(DolphinContract.COLOR_INDEX));
            dolphinSighting.setDolphinAcoustics(dataCursor.getString(DolphinContract.DOLPHIN_ACOUSTICS_INDEX));
            dolphinSighting.setDolphinEnergy(dataCursor.getString(DolphinContract.ENERGY_LEVEL_INDEX));
            dolphinSighting.setDolphinSwimmingActivity(dataCursor.getString(DolphinContract.SWIMMING_ACTIVITY_INDEX));
            dolphinSighting.setDolphinBehavior(dataCursor.getString(DolphinContract.DOLPHIN_BEHAVIOR_INDEX));
            dolphinSighting.setDolphinSocialGrouping(dataCursor.getString(DolphinContract.SOCIAL_GROUPING_INDEX));
            dolphinSighting.setDolphinGroupCode(dataCursor.getString(DolphinContract.DOLPHIN_GROUP_CODE_INDEX));
            dolphinSighting.setDolphinAdditionalObservation(dataCursor.getString(DolphinContract.ADDITIONAL_BEHAVIOR_INDEX));

            distance.setDistanceFromBase(dataCursor.getInt(DolphinContract.DISTANCE_FROM_BOAT_INDEX));
            sightingLocation.setDistance(distance);
            sightingLocation.setBaseHeading(dataCursor.getInt(DolphinContract.BASE_HEADING_INDEX));
            sightingLocation.setBaseCompassHeading(dataCursor.getString(DolphinContract.BASE_COMPASS_HEADING_INDEX));
            sightingLocation.setBaseGPSLatitude(dataCursor.getDouble(DolphinContract.BASE_GPS_LATITUDE_INDEX));
            sightingLocation.setBaseGPSLongitude(dataCursor.getDouble(DolphinContract.BASE_GPS_LONGITUDE_INDEX));
            sightingLocation.setBaseGPSAccuracy(dataCursor.getInt(DolphinContract.BASE_GPS_ACCURACY_INDEX));

            mapLocation.setArcLocation(dataCursor.getInt(DolphinContract.LOCATION_FROM_BOAT_INDEX));
            mapLocation.setLocationHeading(dataCursor.getString(DolphinContract.LOCATION_HEADING_INDEX));
            sightingLocation.setMapLocation(mapLocation);
            movingLocation.setArcLocation(dataCursor.getInt(DolphinContract.SWIMMING_DIRECTION_INDEX));
            movingLocation.setLocationHeading(dataCursor.getString(DolphinContract.SWIMMING_HEADING_INDEX));
            sightingLocation.setDirectionMoving(movingLocation);
            dolphinSighting.setSightingLocation(sightingLocation);

            allDolphinSightings.add(dolphinSighting);
        }
        dataCursor.close();

    }
}