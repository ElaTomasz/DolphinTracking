package com.valenciaprogrammers.dolphintracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by Isabel Tomaszewski on 3/11/17.  GUI and data capture logic
 * <p>
 * Brannon Martin - added audio recording logic
 */

public class AdditionalDataActivity extends AppCompatActivity {

    String behavior = "";
    String socialGrouping = "";
    String additionalObservation = "";
    boolean isGrouping;
    boolean canRecordAudio = false;
    String audioFileName = null;
    String parentFolderName = "/Dolphin Tracker";
    String subFolderName = "/Audio Files";
    String folders = parentFolderName + subFolderName;
    String folderPath = Environment.getExternalStorageDirectory() + folders;
    String fullPath;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    // arrays to manage button images
    int[] imageIds = new int[22];          // resource ids of images for the unselected button
    int[] buttonIds = new int[22];         // resource ids of ImageButtons
    int[] selectedIds = new int[22];       // resource ids of images for the selected button
    // array of prefix names for images and ImageButtons
    String[] prefixes = {"cooperation", "fishing", "splashing", "jumping", "fighting",
            "sexual", "tossing", "milling", "traveling", "object",
            "number1", "number2", "number3", "number4", "number5", "number6", "number7",
            "mothercalf", "babysitting", "multipleyoung", "adults", "mixedgroup"};
    // text for the dolphin attribute saved in the database record
    String[] dolphinAttributes = {"Cooperation", "Fishing", "Splashing", "Jumping",
            "Fighting", "Sexual", "Tossing", "Milling", "Traveling", "Object",
            "Num 1", "Num 2", "Num 3", "Num 4", "Num 5", "Num 6", "Num 7",
            "Mother/Calf ", "Babysitting", "MultipleYoung", "Adults", "MixedGrouping"};
    private String drawablePath;
    private String idPath;
    private TextView headingTextView;
    private ImageButton imageButton;
    private EditText additionalObservationEditText;
    private boolean saveOnReturn;
    private Messages msg = null;
    private boolean hasRecorded = false;
    private Button recordButton;
    private Button playButton;
    private boolean isRecording = false;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_data);
        drawablePath = getResources().getString(R.string.drawablePath);
        idPath = getResources().getString(R.string.idPath);

        Toolbar toolbar =
                (Toolbar) findViewById(R.id.additionalToolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);

        //      instantiate the messages class
        msg = new Messages(this);

        Bundle bundleData = getIntent().getExtras();
        behavior = bundleData.getString("behavior");
        socialGrouping = bundleData.getString("socialGrouping");
        additionalObservation = bundleData.getString("additionalObservation");
        isGrouping = bundleData.getBoolean("isGrouping");
        audioFileName = bundleData.getString("audioFile");
        saveOnReturn = false;

        // if this sighting is part of a group put a message in each heading
        String sightingText = isGrouping ? getResources().getString(R.string.group_sighting_message) : "";
        headingTextView = (TextView) findViewById(R.id.behaviorTextView);
        headingTextView.setText(getResources().getString(R.string.heading_behavior));
        headingTextView = (TextView) findViewById(R.id.socialGroupingTextView);
        headingTextView.setText(getResources().getString(R.string.heading_grouping) + sightingText);

        // if write to storage not permitted,  set the record button off as you can't write a file
        // otherwise create the directpry for audio files
        int permissionAudio = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        int permissionWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionWrite == PackageManager.PERMISSION_GRANTED &
                permissionAudio == PackageManager.PERMISSION_GRANTED) {
            canRecordAudio = true;
            CreateDirectory();
        } else {
            canRecordAudio = false;
        }
        recordButton = (Button) findViewById(R.id.recordAudioButton);
        playButton = (Button) findViewById(R.id.playAudioButton);
        if (audioFileName == null) {
            audioFileName = "/AudioRecording_" + Time.getFullDateTimeCondensed() + ".3gp";
        } else {
            if (canRecordAudio) {
                recordButton.setText(getResources().getString(R.string.action_re_record));
            }
        }
        fullPath = folderPath + audioFileName;

        // get resource ids for the images and the image buttons
        for (int i = 0; i < dolphinAttributes.length; i++) {
            String path = idPath + prefixes[i] + "ImageButton";
            buttonIds[i] = getResources().getIdentifier(path, null, null);
            String imageFile = drawablePath + prefixes[i];
            imageIds[i] = getResources().getIdentifier(imageFile, null, null);
            imageFile += "selected";
            selectedIds[i] = getResources().getIdentifier(imageFile, null, null);
            if (dolphinAttributes[i].equals(behavior)
                    || dolphinAttributes[i].equals(socialGrouping)) {
                changeImageOnButton(buttonIds[i], selectedIds[i]);
            }
        }
        additionalObservationEditText = (EditText) findViewById(R.id.editText);
        additionalObservationEditText.setText(additionalObservation);

    }

    //  method to test if image is to be set to selected state or reset to unselected
//  start and end of resource id for group of attributes, ie. behavior, social groups
//  id of attributes to select/unselect
//  text for the attribute to select/unselect
//
    public String manageButtonImage(int idStart, int idEnd, int idAttribute, String attribute) {
        String attributeToSet = "";
        manageResetButton(idStart, idEnd, idAttribute);
        if (attribute.equals(dolphinAttributes[idAttribute])) {
            changeImageOnButton(buttonIds[idAttribute], imageIds[idAttribute]);
        } else {
            attributeToSet = dolphinAttributes[idAttribute];
            changeImageOnButton(buttonIds[idAttribute], selectedIds[idAttribute]);
        }
        return attributeToSet;
    }

    //
//   Method to set the image for the ImageButton selected or deselected.
//   The input parameter integer refers to the button selected.
//   The input parameter integer refers to the image resource.
//
    public void changeImageOnButton(int idButton, int idResource) {
        imageButton = (ImageButton) findViewById(idButton);
        imageButton.setImageResource(idResource);
    }

    //  reset all images to unselected image
//  id to skip is set to -1 so no id is skipped
//
    public void resetAllImage() {
        manageResetButton(0, 21, -1);
    }

    //  method to reset the requested range of buttons
//  start of resources to reset to unselected image
//  end of resources to reset to unselected image
//  id to skip if needed
//
    public void manageResetButton(int idStart, int idEnd, int idSet) {
        for (int i = idStart; i <= idEnd; i++) {
            if (i != idSet) {
                changeImageOnButton(buttonIds[i], imageIds[i]);
            }
        }
    }

    //    Behaviors Button clicks :
//            "cooperation", "fishing", "splashing", "jumping",
//            "fighting", "sexual","tossing", "milling","traveling","object",
//            "number1", "number2", "number3", "number4", "number5", "number6", "number7"
//
    public void onClickCooperationBehavior(View v) {
        behavior = manageButtonImage(0, 16, 0, behavior);
    }

    public void onClickFishingBehavior(View v) {
        behavior = manageButtonImage(0, 16, 1, behavior);
    }

    public void onClickSplashingBehavior(View v) {
        behavior = manageButtonImage(0, 16, 2, behavior);
    }

    public void onClickJumpingBehavior(View v) {
        behavior = manageButtonImage(0, 16, 3, behavior);
    }

    public void onClickFightingBehavior(View v) {
        behavior = manageButtonImage(0, 16, 4, behavior);
    }

    public void onClickSexualBehavior(View v) {
        behavior = manageButtonImage(0, 16, 5, behavior);
    }

    public void onClickTossingBehavior(View v) {
        behavior = manageButtonImage(0, 16, 6, behavior);
    }

    public void onClickMillingBehavior(View v) {
        behavior = manageButtonImage(0, 16, 7, behavior);
    }

    public void onClickTravelingBehavior(View v) {
        behavior = manageButtonImage(0, 16, 8, behavior);
    }

    public void onClickObjectInMouthBehavior(View v) {
        behavior = manageButtonImage(0, 16, 9, behavior);
    }

    public void onClickNumber1Behavior(View v) {
        behavior = manageButtonImage(0, 16, 10, behavior);
    }

    public void onClickNumber2Behavior(View v) {
        behavior = manageButtonImage(0, 16, 11, behavior);
    }

    public void onClickNumber3Behavior(View v) {
        behavior = manageButtonImage(0, 16, 12, behavior);
    }

    public void onClickNumber4Behavior(View v) {
        behavior = manageButtonImage(0, 16, 13, behavior);
    }

    public void onClickNumber5Behavior(View v) {
        behavior = manageButtonImage(0, 16, 14, behavior);
    }

    public void onClickNumber6Behavior(View v) {
        behavior = manageButtonImage(0, 16, 15, behavior);
    }

    public void onClickNumber7Behavior(View v) {
        behavior = manageButtonImage(0, 16, 16, behavior);
    }

    //    Social Groupings Button Clicks :
//          "mothercalf", "babysitting", "multipleyoung", "adults","mixedgroup"};
//
    public void onClickMotherCalfGrouping(View v) {
        socialGrouping = manageButtonImage(17, 21, 17, socialGrouping);
    }

    public void onClickBabysittingGrouping(View v) {
        socialGrouping = manageButtonImage(17, 21, 18, socialGrouping);
    }

    public void onClickMultipleYoungGrouping(View v) {
        socialGrouping = manageButtonImage(17, 21, 19, socialGrouping);
    }

    public void onClickAdultsGrouping(View v) {
        socialGrouping = manageButtonImage(17, 21, 20, socialGrouping);
    }

    public void onClickMixedGrouping(View v) {
        socialGrouping = manageButtonImage(17, 21, 21, socialGrouping);
    }

    //  method to
    private void returnData() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        if (isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        String audio = null;
        if (hasRecorded) {
            if (audioFileName != null) {
                String[] temp = audioFileName.split("/");  // takes the "/" off the file name just to make it look cleaner in the CSV file if it has it
                audio = (temp.length > 1) ? temp[1] : temp[0];  // If this is the first time coming into this screen, temp length will be 2 because of cutting the "/" off the end.  Any time after that it will not have the / to take off
            }
        }
        additionalObservationEditText = (EditText) findViewById(R.id.editText);
        additionalObservation = additionalObservationEditText.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("behavior", behavior);
        returnIntent.putExtra("socialGrouping", socialGrouping);
        returnIntent.putExtra("additionalObservation", additionalObservation);
        returnIntent.putExtra("saveOnReturn", saveOnReturn);
        returnIntent.putExtra("audioFile", audio);

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    //  *******************************************************************************************************
//  manage Additional MenuItem access
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_additional, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //  method to perform the menu item touched
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // return to the main activity, pass the data and set not to save to db on return
            case android.R.id.home:
                saveOnReturn = false;
                returnData();
                return true;

            // return to the main activity, pass the data and set to save to database on return
            case R.id.action_save:
                saveOnReturn = true;
                returnData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void recordAudioButtonPush(View v) {

        if (canRecordAudio) {
            startRecording();
        } else {
            msg.displayErrorMessage(Messages.TYPE.AUDIO_DENIED);
        }
    }

    private void startRecording() {

        hasRecorded = true;

        if (isRecording == false)  // The user just hit record
        {
            isRecording = true;

            playButton.setEnabled(false);
            recordButton.setText(getResources().getString(R.string.action_stop_record));

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_start_recording),
                    Toast.LENGTH_SHORT).show();
        } else {    // The person hit the "stop" button
            hasRecorded = true;
            isRecording = false;
            MainTrackingActivity.saveAudio = true;
            mediaRecorder.stop();
            playButton.setEnabled(true);
            recordButton.setText(getResources().getString(R.string.action_re_record));

            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_end_recording),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void playAudioButtonPush(View v) {

        if (canRecordAudio) {
            playRecording();
        } else {
            msg.displayErrorMessage(Messages.TYPE.AUDIO_DENIED);
        }
    }

    private void playRecording() {
        if (isPlaying == false)  // The user just hit the play button
        {
            recordButton.setEnabled(false);
            playButton.setText(getResources().getString(R.string.action_stop_play));
            isPlaying = true;

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(fullPath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_start_play),
                    Toast.LENGTH_SHORT).show();
        } else // Resets the buttons if the user hits the stop button
        {
            recordButton.setEnabled(true);
            playButton.setText(getResources().getString(R.string.action_play_recording));
            isPlaying = false;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                MediaRecorderReady();
            }
        }

        /*
            Called when the audio files quits playing on its own by reaching the end of the file
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                recordButton.setEnabled(true);
                playButton.setText(getResources().getString(R.string.action_play_recording));
                isPlaying = false;
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(fullPath);
    }

    private void CreateDirectory() // Creates the Dolphin Tracker and/or audio folders if they don't exist yet
    {
        File newFile = new File(Environment.getExternalStorageDirectory(), folders);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (hasRecorded == false) {
                audioFileName = null; // Set name to null so it will not save the file

            }
            saveOnReturn = false;
            returnData();
            return true;
        }
        return false;
    }
}
