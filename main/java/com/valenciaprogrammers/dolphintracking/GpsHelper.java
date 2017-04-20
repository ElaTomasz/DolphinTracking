package com.valenciaprogrammers.dolphintracking;

/*

This class uses A Google API Client and Location Services to access the users location using GPS.

This class will not run in an emulator due to GPS and Google Services not working in emulators.

In order for this class to work, you must put the following line of code in the build.gradle(Module: app) file
        compile 'com.google.android.gms:play-services-location:8.4.0'
It should be the last line in the dependencies section.  The version number may be higher

To use this class in an app, create a new instance of this class.  Then call the restartGPS() method when you want to turn the GPS on.
All checks for turning the GPS on and off are handled by this class.  The GPS will run until it gets a "lock" within the distance defined by the ACCURACY variable.
To stop the GPS immediately (such as in the onPause or onStop methods of your MainActivity class for when the app closes), then call the forceStop() method.

The restart method will build the GPS if has not been built yet.  If it has, it will reconnect the GPS if it is not already connected.
It also starts a timer that will run for 10 minutes (this number can be changed by altering the TIMER_MILLISECONDS variable)
If the class the created the instance of the GPS, usually the MainActivity, turns the GPS on while the timer is running, the timer is canceled and restarted.
If the timer runs through to the end, the GPS is reconnected and runs until it gets a reading with an accuracy 25 feet or less.  This number can be changed by altering the ACCURACY variable.

The first time an instance of this class is created and the GPS tries to connect, a dialog with a spinning wheel will appear.  This can be canceled by touching anywhere on the screen.
If the user does not cancel the dialog, it will run for 2 minutes.  If the GPS does not find a location with an accuracy of less than the value of the ACCURACY variable, another dialog will appear.
This dialog says that the GPS could not find a good location and that the GPS will continue to run.

 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Brannon Martin and Kevin Neal on 3/2/2017.
 */

public class GpsHelper extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    public static boolean isLocked = false;
    public static double[] info = {-1, -1, -1, -1};  // {latitude, longitude, accuracy,  altitude}  Needs to be static so the compass can access these numbers
    private static boolean firstTime = true;
    private final int TIMER_MILLISECONDS = 600000; // 10 minutes
    private final int TIMER_UPDATE = 1000;
    private final int ACCURACY = 25;
    protected GoogleApiClient mGoogleApiClient;
    ProgressDialog dialog;
    private CountDownTimer cdt;
    private CountDownTimer dialogTimer;
    private boolean isCounting = false;
    private boolean gpsIsConnected = false;
    private Context context;
    private LocationRequest mLocationRequest;
    private Location loc = null;

    public GpsHelper(Context c) {
        this.context = c;
    }

    private void buildGPS() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (Exception e) {
            Toast.makeText(context, "Unable to connect to Google Services!", Toast.LENGTH_LONG).show();
            Log.d("From GpsHelper: ", e.toString());
        }
    }

    public void restartGPS() {
        isLocked = false;

        if (mGoogleApiClient != null) {
            if (!mGoogleApiClient.isConnected()) {
                if (isCounting)  // if the timer is running
                {
                    if(cdt!=null) {
                        cdt.cancel();
                    }
                }
                mGoogleApiClient.connect();
            }
        } else // mGoogleApiClient has not yet been connected
        {
            buildGPS();
        }
    }

    /*
        This method tells the GPS to regularly update.  Without this, it would not send updates
     */
    private void createLocationRequest() {
        if (mGoogleApiClient != null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //  Required security check
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void getLocation() {
        try {
            loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);  // Get last known location.  This may not be the actual current location
            getLatestCoordinates();

        } catch (SecurityException e) {
            Toast.makeText(context, "No location found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        loc = location;

        if (!isLocked && loc.getAccuracy() < ACCURACY) {  // The number for the variable ACCURACY was randomly picked as a "good GPS lock".  This could be changed based on needs of the app
            isLocked = true;
            forceStop();
            if (firstTime) { // Only show a toast the fist time the GPS locks within the distance of the ACCURACY variable
                Toast.makeText(context, "GPS location found", Toast.LENGTH_SHORT).show();
                firstTime = false;
                if (dialog != null) {
                    dialog.cancel();
                }
                if (dialogTimer != null) {
                    dialogTimer.cancel();
                }
            }
            startTimer();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, "Connection to GPS failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient != null) {
            if (firstTime) {
                showProgressBar();
            }
            createLocationRequest();
            getLocation();
        }
    }

    private void showProgressBar() {
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Searching for GPS. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogTimer = new CountDownTimer(120000, 1000)  // Gives the GPS 2 minutes to find GPS signal before showing poor GPS warning.
        {
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing on each second tick of the timer
            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.cancel();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Poor GPS Signal");
                builder.setMessage("Could not find accurate GPS location.  GPS will continue to run, but records saved before GPS lock will not have accurate GPS information.");

                builder.setCancelable(false);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing on buton click other than close the dialog.  GPS will continue to run
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }.start();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient!=null) {
            mGoogleApiClient.connect();
        }else{
           buildGPS();
        }
    }

    public double[] getLatestCoordinates() {
        if (loc != null)  //  App will crash if you try to get the values if loc is null
        {
            info[0] = loc.getLatitude();
            info[1] = loc.getLongitude();
            info[2] = loc.getAccuracy();
            info[3] = loc.getAltitude();
        }
        return info;
    }

    private void startTimer() {
        isCounting = true;

        cdt = new CountDownTimer(TIMER_MILLISECONDS, TIMER_UPDATE) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Use this method to do something every second, or however long TIMER_UPDATE is
            }
            @Override
            public void onFinish() {
                isLocked = false;
                restartGPS();
            }
        }.start();
    }

    public void stopGPS()  // Stops the GPS and restarts the timer
    {
        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
                isCounting = false;
                if (cdt != null) {
                    cdt.cancel();
                    cdt.start();
                }
            }
        }
    }

    public void forceStop()  // Stops the GPS and turns off the timer
    {
        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();

                if (isCounting) {
                    isCounting = false;
                    if (cdt != null) {
                        cdt.cancel();
                    }
                }
            }
        }
    }

    @Override
    protected void onStop() {
        if (gpsIsConnected) {
            super.onStop();
            if(mGoogleApiClient!=null) {
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
            }
        }
    }
}
