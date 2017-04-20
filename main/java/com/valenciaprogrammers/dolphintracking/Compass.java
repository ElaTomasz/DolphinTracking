package com.valenciaprogrammers.dolphintracking;

import android.app.Activity;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;


/**
 * Created by Brannon Martin on 2/12/2017.
 * <p>
 * This reading is not perfect yet.  It is showing magnetic north, not true north.  Will need input from GPS to compute true north.
 * Also may do something to slow the readings down.  Maybe a timer where it takes 1 reading per second?
 */

public class Compass extends Activity {
    private static float[] mGravity;
    private static float[] mGeomagnetic;
    float declination;
    float magHeading = -1;
    long time = -1;
    int trueNorth = -1;
    int[] result = {-1, -1};
    float lat;
    float lon;
    float alt;
    private GeomagneticField geoField;

    public int[] GetHeading(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                // orientation contains azimuth, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                float rotation = orientation[0] * 360 / (2 * 3.14159f);
                rotation = (rotation + 360) % 360;

                magHeading = rotation;
            }
        }
        result[0] = (int) magHeading;
        result[1] = GetTrueNorth();

        return result;
    }

    private int GetTrueNorth() {
        if (magHeading != 1)  // if magHeading == 1, then the compass is not working yet
        {
            time = System.currentTimeMillis();

            lat = (float) GpsHelper.info[0];
            lon = (float) GpsHelper.info[1];
            alt = (float) GpsHelper.info[3];

            if (lat != -1) {
                geoField = new GeomagneticField(lat, lon, alt, time);
                declination = geoField.getDeclination();
            }

            trueNorth = (int) (magHeading + declination);
        }

        return trueNorth;
    }
}
