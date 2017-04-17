package com.valenciaprogrammers.dolphintracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by Isabel Tomaszewski on 4/14/17.
 */

public class Messages extends Activity{
    public enum TYPE {NEED_LOCATION, NO_EMAIL_SETUP, SAVE_ERROR, EXPORT_DENIED,
                      NO_FILE, WRITE_ERROR, POOR_GPS, GPS_FAILED, AUDIO_DENIED}

    private Context context;
    public Messages(Context c)
    {
        this.context = c;
    }

    // display error message
    public void displayErrorMessage(TYPE errorMessage) {
        String messageTitle = "";
        String messageText = "";
        switch (errorMessage) {
            case NEED_LOCATION:
                Log.d("Need Location", "Need Location Message");
                messageTitle = context.getResources().getString(R.string.build_need_location_title);
                messageText = context.getResources().getString(R.string.build_need_location);
                break;
            case NO_EMAIL_SETUP:
                Log.d("No Email Setup", "No Email Setup Message");
                messageTitle = context.getResources().getString(R.string.build_no_email_title);
                messageText = context.getResources().getString(R.string.build_no_email);
                break;
            case SAVE_ERROR:
                Log.d("Save Error", "Error saving data.");
                messageTitle = context.getResources().getString(R.string.build_error_saving_title);
                messageText = context.getResources().getString(R.string.build_error_saving);
                break;
            case EXPORT_DENIED:
                Log.d("Export Failed", "Error export permission denied.");
                messageTitle = context.getResources().getString(R.string.build_export_denied_title);
                messageText = context.getResources().getString(R.string.build_export_denied);
                break;
            case NO_FILE:
                Log.d("Export Failed", "Error file not found exception.");
                messageTitle = context.getResources().getString(R.string.build_no_file_title);
                messageText = context.getResources().getString(R.string.build_no_file);
                break;
            case WRITE_ERROR:
                Log.d("Export Failed", "Error IO when writing csv file.");
                messageTitle = context.getResources().getString(R.string.build_write_error_title);
                messageText = context.getResources().getString(R.string.build_write_error);
                break;
            case POOR_GPS:
                Log.d("GPS Poor Signal", "Poor signal on GPS.");
                messageTitle = context.getResources().getString(R.string.build_poor_gps_title);
                messageText = context.getResources().getString(R.string.build_poor_gps);
                break;
            case GPS_FAILED:
                Log.d("GPS Failed", "GPS Connection Failed.");
                messageTitle = context.getResources().getString(R.string.build_poor_gps_title);
                messageText = context.getResources().getString(R.string.build_poor_gps);
                break;
            case AUDIO_DENIED:
                Log.d("Audio Denied", "Microphone and Storage permission denied.");
                messageTitle = context.getResources().getString(R.string.build_audio_denied_title);
                messageText = context.getResources().getString(R.string.build_audio_denied);
                break;
        }
        AlertDialog.Builder message = new AlertDialog.Builder(context);
        message.setMessage(messageText);
        message.setTitle(messageTitle);
        message.setPositiveButton("OK", null);
        message.setCancelable(true);
        message.create().show();
        message.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

}
