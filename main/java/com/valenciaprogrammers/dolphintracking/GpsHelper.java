package com.valenciaprogrammers.dolphintracking;

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
    private final int TIMER_MILLISECONDS = 600000; // 10 minutes
    private final int TIMER_UPDATE = 1000;
    private CountDownTimer cdt;
    private CountDownTimer dialogTimer;
    private boolean isCounting = false;
    private boolean gpsIsConnected = false;

    private Context context;
    private LocationRequest mLocationRequest;
    private Location loc = null;
    ProgressDialog dialog;
    public static boolean isLocked = false;
    private static boolean firstTime = true;
    public static double[] info = {-1, -1, -1, -1};  // {latitude, longitude, accuracy,  altitude}  Needs to be static so the compass can access these numbers

    protected GoogleApiClient mGoogleApiClient;

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

    public void restartGPS()
    {
        isLocked = false;

        if (mGoogleApiClient != null)
        {
            if (!mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.connect();

                if (isCounting)  // if the timer is running
                {
                    cdt.cancel();
                }
            }
        }
        else // mGoogleApiClient has not yet been connected
        {
            buildGPS();
        }
    }

    /*
        This method tells the GPS to regularly update.  Without this, it would not send updates
     */
    private void createLocationRequest() {
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

        if (!isLocked && loc.getAccuracy() < 25) {  // The number 25 was randomly picked as a "good GPS lock".  This could be changed based on needs of the app
            isLocked = true;
            stopGPS();

            if (firstTime) { // Only show a toast the fist time the GPS locks within 25 feet
                Toast.makeText(context, "GPS location found", Toast.LENGTH_SHORT).show();
                firstTime = false;
                dialog.cancel();
                dialogTimer.cancel();
            }

            startTimer();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, "Connection to GPS failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        if(firstTime)
        {
            showProgressBar();
        }

        createLocationRequest();
        getLocation();
    }

    private void showProgressBar()
    {
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Searching for GPS. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogTimer = new CountDownTimer(120000, 1000)  // Gives the GPS 2 minutes to find GPS signal before showing poor GPS warning.
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                // Do nothing on each second tick of the timer
            }

            @Override
            public void onFinish() {
                dialog.cancel();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.build_poor_gps_title));
                builder.setMessage(context.getResources().getString(R.string.build_poor_gps));
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
    public void onConnectionSuspended(int i)
    {
        mGoogleApiClient.connect();
    }

    public double[] getLatestCoordinates()
    {
        if(loc != null)  //  App will crash if you try to get the values if loc is null
        {
            info[0] = loc.getLatitude();
            info[1] = loc.getLongitude();
            info[2] = loc.getAccuracy();
            info[3] = loc.getAltitude();
        }

        return info;
    }

    private void startTimer()
    {
        isCounting = true;

        cdt = new CountDownTimer(TIMER_MILLISECONDS, TIMER_UPDATE)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                // Use this method to do something every second, or however long TIMER_UPDATE is
            }

            @Override
            public void onFinish()
            {
                isLocked = false;
                restartGPS();
            }
        }.start();
    }

    public void stopGPS()
    {
        if(isLocked)
        {
            if (mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();

                if (isCounting)
                {
                    isCounting = false;
                    cdt.cancel();
                }
            }
        }
    }

    public void forceStop()
    {
        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();

            if (isCounting)
            {
                isCounting = false;
                cdt.cancel();
            }
        }
    }

    @Override
    protected void onStop()
    {
        if(gpsIsConnected)
        {
            super.onStop();
            if (mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
            }
        }
    }
}
