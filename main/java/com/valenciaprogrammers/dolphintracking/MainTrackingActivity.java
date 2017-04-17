package com.valenciaprogrammers.dolphintracking;
/**
 * Created by Isabel Tomaszewski on 2/10/17.
 * Main activity to capture and save dolphin sighting data.
 * <p>
 * Modified by Brannon Martin - added GPS, Compass and CSV writer
 */
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Color;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.location.LocationManager;
import java.io.File;

public class MainTrackingActivity extends AppCompatActivity implements SensorEventListener {

    private ImageButton imageButton;
    private MapForm mapForm;
    private PointF touchDown;
    private PointF touchUp;
    private PointF touchDownDraw;
    private PointF touchUpDraw;
    private PointF screenSize;
    private float density;
    private float imageXOffset;
    private float imageYOffset;

    private boolean isAvailableCompass = false;
    private boolean haveAskedPermission = false;
    private boolean canWrite = false;
    public static boolean saveAudio = false;
    public static boolean isAvailableGPS = false;
    public static boolean canRecordAudio = false;
    private CountDownTimer cdt;

    private String drawablePath = "com.valenciaprogrammers.dolphintracking:drawable/";
    private String idPath = "com.valenciaprogrammers.dolphintracking:id/";
    private int mapResource;

    private PointF imageCenterPoint;
    private int imageEndBoundary;
    private float maxDistance;
    private DolphinSighting dolphinSighting = null;
    private SightingLocation sightingLocation = null;
    private GpsHelper gpsHelper = null;
    private CSVWriter wrt = null;
    private Messages msg = null;

    private ADDatabaseHelper dbHelp;
    private ObservationEnvironment oe = null;
    private DolphinSighting dd = null;
    private String dolphinSpecies = "";
    private String groupCode = "";
    private String socialGrouping = "";
    private boolean saveOnReturn = true;

    private Compass compass;
    private SensorManager AccelerometerManager;
    private SensorManager MagneticSensorManager;
    private Sensor GPS;
    private Sensor accelerometer;

    private int baseHeading = -1;
    private double[] gpsInfo = new double[3];
    private int fromWhere = 0; // Used to tell which method is asking for write permission so the correct screen can be opened after a user gives permission

    // arrays to manage button images
    int[] imageIds = new int[21];          // resource ids of images for the unselected button
    int[] buttonIds = new int[21];         // resource ids of ImageButtons
    int[] selectedIds = new int[21];       // resource ids of images for the selected button

    // array of prefix names for images and ImageButtons
    String[] prefixes = {"small", "medium", "large", "grey", "pinkgrey", "pink",
            "low", "medium", "high", "floating", "movingslow", "movingregular", "jumping",
            "popcorn", "whistles", "onaxis", "bubbles", "chuff",
            "inia", "iniasotalia", "sotalia"};

    // button name and index of attributes set
    int[] prefixButton = {0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 5};
    String[] buttonName = {"dolphin", "color", "energy", "activity", "acoustics", "species"};

    // text for the dolphin attribute saved in the database record
    String[] dolphinAttributes = {"Small", "Medium", "Large", "Grey", "Mixed", "Pink",
            "Low", "Medium", "High", "Floating", "Slow", "Regular", "Jumping",
            "Popcorn", "Whistles", "On-Axis", "Bubbles", "Chuff",
            "Inia", "Inia-Sotalia", "Sotalia"};

    // Used when asking the user for permissions to write to storage
    private static String[] PERMISSIONS_GPS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_AUDIO = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      display the end user agreement.
//      if the user refuses,  the app terminates
//      once the user accepts,  the message will not appear unless the app is uninstalled and reisntaalled
        Eula.show(this);

//      bind to main activity
        setContentView(R.layout.activity_main_tracking);

//      create the toolbar and set up actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//      instantiate the database helper class
        dbHelp = new ADDatabaseHelper(getApplicationContext());

//      instantiate the messages class
        msg = new Messages(this);

//      get and save the screen size and density
        WindowManager window = getWindowManager();
        Point size = new Point();
        window.getDefaultDisplay().getSize(size);
        screenSize = new PointF(size);
        density = getResources().getDisplayMetrics().density;
        System.out.println("*********  screen size  height : " + screenSize.y + "   width : " + screenSize.x + "    Density " + density);
//      get the resource id for the map per screen size
//
        if (screenSize.x == 800 || screenSize.x == 1200) {
            mapResource = getResources().getIdentifier(drawablePath + "polarmap9", null, null);
        } else if (screenSize.x >= 1600) {
            mapResource = getResources().getIdentifier(drawablePath + "polarmap9_16", null, null);
        } else if (screenSize.x == 1536) {
            mapResource = getResources().getIdentifier(drawablePath + "polarmap1536", null, null);
        } else {
            mapResource = getResources().getIdentifier(drawablePath + "polarmap9_7", null, null);
        }
//
//      instantiate the sighting objects
        mapForm = new MapForm();
        sightingLocation = new SightingLocation();
        dolphinSighting = new DolphinSighting();
        dolphinSighting.setSightingLocation(sightingLocation);

//      initialize the compass,audio and GPS available booleans
        isAvailableCompass = false;
        isAvailableGPS = false;
        canRecordAudio = false;
        saveAudio = false;

//      set up compass and GPS helper,  and verify permission
        gpsHelper = new GpsHelper(this);
        compass = new Compass();
        verifyWritePermission();
        register();

//      get resource ids for all the imagebuttons
        for (int i = 0; i < dolphinAttributes.length; i++) {
            String path = idPath + prefixes[i] + buttonName[prefixButton[i]] + "ImageButton";
            buttonIds[i] = getResources().getIdentifier(path, null, null);
            String imageFile = drawablePath + prefixes[i] + buttonName[prefixButton[i]];
            imageIds[i] = getResources().getIdentifier(imageFile, null, null);
            imageFile += "selected";
            selectedIds[i] = getResources().getIdentifier(imageFile, null, null);
        }
        initSightingData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Resume Activity", "************   Group Code " + groupCode
                + "  *********   Social Grouping  " + socialGrouping);

        reCheckPermissions();
    }

    //    method when Trash button is pressed to
//    resets all the attributes and settings to default values
    public void onClickReset(View v) {
        dolphinSighting = new DolphinSighting();
        sightingLocation = new SightingLocation();
        initSightingData();
    }

    private void initSightingData() {
        dolphinSighting = new DolphinSighting();
        sightingLocation = new SightingLocation();

        //      Set species to Inia - most common one being observed
        dolphinSpecies = manageAttribute(18, 20, 18, "");
        dolphinSighting.setDolphinSpecies(dolphinSpecies);

        //      set group button to off - sighting is for a single dolphin, not in a group
        resetGroupCode();

        // set all image buttons default unselected state
        resetAllImage();
        saveOnReturn = false;
        displayMessage("Sighting\nDolphins\nData");
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {

        // get the height and width of the image on screen along with it's location in the window

        ImageView imageView = (ImageView) findViewById(R.id.polarMapImageView);
        int imageLocationInWindow[] = new int[2];
        imageView.getLocationInWindow(imageLocationInWindow);

        // calculate the center point in the image
        // imageLocationInWindow [0] contains the image's offset on the left
        // imageLocationInWindow [1] contains the image's offset from the top

        imageXOffset = (float) imageLocationInWindow[0];
        imageYOffset = (float) imageLocationInWindow[1];

        int xCenter = (imageView.getWidth() / 2) + imageLocationInWindow[0];
        int yCenter = (imageView.getHeight() / 2) + imageLocationInWindow[1];
        imageCenterPoint = new PointF(xCenter, yCenter);

        float equidistance = (xCenter - imageView.getLeft()) / mapForm.getNumberOfCircles();
        maxDistance = (xCenter - imageView.getLeft()) + 10.f;
        System.out.println("/n  equidistant    " + equidistance + " \nLeft " + imageView.getLeft());
        mapForm.setEquiDistanceInPoints(equidistance);

        // calculate the boundary of the map area on the screen
        // this will be used to ignore screen touches beyond the map area

        imageEndBoundary = imageView.getHeight() + imageLocationInWindow[1] + 5;
        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        // If the spot touched is beyond the boundaries of the map,  ignore it
        if ((int) ev.getY() < imageEndBoundary) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                if(recheckGPS()){
                    gpsHelper.restartGPS();  // Start the GPS when the user touches the screen and the GPS is currently not running
                }
                touchDown = new PointF(ev.getX(), ev.getY());
                System.out.println("/n  Down : (X,Y)   ( " + touchDown.x + "," + touchDown.y + " )");
                System.out.println("/n  Center : (X,Y)   ( " + imageCenterPoint.x + "," + imageCenterPoint.y + " )");
                Distance distance = new Distance(mapForm, imageCenterPoint, touchDown);
                MapLocation mapLocation = new MapLocation(imageCenterPoint, touchDown);
                sightingLocation.setMapForm(mapForm);
                sightingLocation.setDistance(distance);
                sightingLocation.setMapLocation(mapLocation);
                dolphinSighting.setSightingLocation(sightingLocation);
                touchDownDraw = adjustDrawPoint(touchDown);
                drawAMark(touchDownDraw, touchDownDraw);
                System.out.println("/n  Dolphin Sighting   ( " + sightingLocation.toString() + " )");
                displayMessage(dolphinSighting.shortToString());
            } else {
                if (ev.getAction() == MotionEvent.ACTION_UP) {
                    touchUp = new PointF(ev.getX(), ev.getY());
                    System.out.println("/n  Up    : (X,Y)   ( " + touchUp.x + "," + touchUp.y + " )");
                    MapLocation directionLocation = new MapLocation();
                    if (touchDown != touchUp) {
                        directionLocation = new MapLocation(touchDown, touchUp);
                        touchUpDraw = adjustDrawPoint(touchUp);
                        drawAMark(touchDownDraw, touchUpDraw);
                    }
                    dolphinSighting.getSightingLocation().setDirectionMoving(directionLocation);
                    System.out.println("/n  Dolphin Sighting Moving ( " + sightingLocation.toString() + " )");
                    displayMessage(dolphinSighting.shortToString());
                }
            }
        }
        return true;
    }

    // adjust the point to draw the circle on the imageview

    public PointF adjustDrawPoint(PointF pt) {
        PointF adjustedPt = new PointF();
        adjustedPt.x = pt.x - imageXOffset;
        adjustedPt.y = pt.y - imageYOffset;
        return adjustedPt;
    }
    // Draw the circle on the touch spot and the line showing direction

    public void drawAMark(PointF startPt, PointF endPt) {
        System.out.println("/n  Starting Point ( " + startPt.x + ", " + startPt.y + " )");
        System.out.println("/n  Ending Point ( " + endPt.x + ", " + endPt.y + " )");

        Bitmap bitmap;
        int circleSize = 12;
        float lineSize = 4.F;

        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inMutable = true;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeResource(getResources(), mapResource, myOptions);
        if (screenSize.x < 800) {
            circleSize = 7;
            lineSize = 2.f;
        }
        Canvas canvas = new Canvas(bitmap);                 //draw a canvas in defined bmp

        Paint paint = new Paint();                          //define paint and paint color
        paint.setColor(Color.RED);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);                           //smooth edges

        canvas.drawCircle(startPt.x, startPt.y, circleSize * (int) density, paint);
        paint.setStrokeWidth(lineSize * density);
        canvas.drawLine(startPt.x, startPt.y, endPt.x, endPt.y, paint);

        ImageView imageView = (ImageView) findViewById(R.id.polarMapImageView);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(bitmap);
    }
//
//    dolphin attribute button clicks
//
    // Size Buttons   **********************************

    public void onClickSmallDolphin(View v) {
        changeSize(0);
    }

    public void onClickMediumDolphin(View v) {
        changeSize(1);
    }

    public void onClickLargeDolphin(View v) {
        changeSize(2);
    }

    private void changeSize(int index) {
        dolphinSighting.setDolphinSize(manageAttribute(0, 2, index, dolphinSighting.getDolphinSize()));
        displayMessage(dolphinSighting.shortToString());
    }
    // Color Buttons  *******************************

    public void onClickGreyDolphin(View v) {
        changeColor(3);
    }

    public void onClickPinkGreyDolphin(View v) {
        changeColor(4);
    }

    public void onClickPinkDolphin(View v) {
        changeColor(5);
    }

    private void changeColor(int index) {
        dolphinSighting.setDolphinColor(manageAttribute(3, 5, index, dolphinSighting.getDolphinColor()));
        displayMessage(dolphinSighting.shortToString());
    }

    // Energy Buttons ********************************

    public void onClickLowEnergy(View v) {
        changeEnergy(6);
    }

    public void onClickMediumEnergy(View v) {
        changeEnergy(7);
    }

    public void onClickHighEnergy(View v) {
        changeEnergy(8);
    }

    private void changeEnergy(int index) {
        dolphinSighting.setDolphinEnergy(manageAttribute(6, 8, index, dolphinSighting.getDolphinEnergy()));
        displayMessage(dolphinSighting.shortToString());
    }

    //  Activity Buttons  *********************

    public void onClickFloating(View v) {
        changeActivity(9);
    }

    public void onClickSlowSwimming(View v) {
        changeActivity(10);
    }

    public void onClickRegularSwimming(View v) {
        changeActivity(11);
    }

    public void onClickJumpingSwimming(View v) {
        changeActivity(12);
    }

    private void changeActivity(int index) {
        dolphinSighting.setDolphinSwimmingActivity(manageAttribute(9, 12, index,
                dolphinSighting.getDolphinSwimmingActivity()));
        displayMessage(dolphinSighting.shortToString());
    }
    //  Acoustics Buttons   *******************************

    public void onClickPopcornAcousticsImageButton(View v) {
        changeAcoustics(13);
    }

    public void onClickWhistlesAcousticsImageButton(View v) {
        changeAcoustics(14);
    }

    public void onClickOnAxisAcousticsImageButton(View v) {
        changeAcoustics(15);
    }

    public void onClickBubblesAcousticsImageButton(View v) {
        changeAcoustics(16);
    }

    public void onClickChuffAcousticsImageButton(View v) {
        changeAcoustics(17);
    }

    private void changeAcoustics(int index) {
        dolphinSighting.setDolphinAcoustics(manageAttribute(13, 17, index,
                dolphinSighting.getDolphinAcoustics()));
        displayMessage(dolphinSighting.shortToString());
    }
    //  Species buttons   **********************

    public void onClickIniaSpeciesImageButton(View v) {
        changeSpecies(18);
    }

    public void onClickIniaSotaliaSpeciesImageButton(View v) {
        changeSpecies(19);
    }

    public void onClickSotaliaSpeciesImageButton(View v) {
        changeSpecies(20);
    }

    private void changeSpecies(int index) {
        dolphinSighting.setDolphinSpecies(manageAttribute(18, 20, index,
                dolphinSighting.getDolphinSpecies()));
        dolphinSpecies = dolphinSighting.getDolphinSpecies();
        displayMessage(dolphinSighting.shortToString());
    }

    // **********************************************************
    //
//  save the dolphin sighting data if a distance is present
//  reset for a new sighting
//
    public void onClickSaveButton(View V) {
        callSave();
    }

    private void callSave() {
        saveOnReturn = true;
        if (dolphinSighting.getSightingLocation().getDistance().getDistanceFromBase() == 0) {
            Log.d("NO SAVE", "NO data to save");
//            displayErrorMessage(MESSAGE_TYPE.NEED_LOCATION);
            msg.displayErrorMessage(Messages.TYPE.NEED_LOCATION);
        } else {
            if (saveDolphinSighting()) {
                saveOnReturn = false;
                if (isAvailableGPS) {
                    gpsHelper.stopGPS();  // Turn the GPS off
                }
            }
        }
    }

    // method to save the data
    // get the gps data and save the sighting data
    public boolean saveDolphinSighting() {
        if (groupCode.length() > 0) {
            dolphinSighting.setDolphinGroupCode(groupCode);
            dolphinSighting.setDolphinSocialGrouping(socialGrouping);
        }
        if (isAvailableGPS) {
            gpsInfo = gpsHelper.getLatestCoordinates();
            sightingLocation.setBaseGPSLatitude(gpsInfo[0]);
            sightingLocation.setBaseGPSLongitude(gpsInfo[1]);
            sightingLocation.setBaseGPSAccuracy(gpsInfo[2]);
        }
        if (isAvailableCompass) {
            sightingLocation.setBaseHeading(baseHeading);
            sightingLocation.setBaseCompassHeading(CompassHeading.getBearing(baseHeading));
        }
        long id = dbHelp.SaveDolphinData(dolphinSighting);
        if (id == -1) {
            Log.d("Save Error", "Display error save message");
            msg.displayErrorMessage(Messages.TYPE.SAVE_ERROR);
            return false;
        } else {
            saveOnReturn = false;
            dolphinSighting = new DolphinSighting();
            if (groupCode == "") {
                sightingLocation = new SightingLocation();
            }
            dolphinSighting.setDolphinSpecies(dolphinSpecies);
            dolphinSighting.setDolphinSocialGrouping(socialGrouping);
            dolphinSighting.setDolphinGroupCode(groupCode);
            dolphinSighting.setSightingLocation(sightingLocation);
            resetAllImage();
            if (groupCode == "") {
                displayMessage("Sighting\nDolphins\nData");
            } else {
                drawAMark(touchDownDraw, touchUpDraw);
                displayMessage(dolphinSighting.shortToString());
            }
            Toast.makeText(this, "Dolphin Sighting Data Saved!", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    //  method to pass the data to the additional observations activity
    //  behavior,  social grouping and additional observations
    public void onClickAdditionalButton(View v) {
        callAdditional();
    }

    private void callAdditional() {
        Intent intent = new Intent(this, AdditionalDataActivity.class);
        intent.putExtra("behavior", dolphinSighting.getDolphinBehavior());
        intent.putExtra("socialGrouping", dolphinSighting.getDolphinSocialGrouping());
        intent.putExtra("additionalObservation", dolphinSighting.getDolphinAdditionalObservation());
        intent.putExtra("saveOnReturn", saveOnReturn);
        intent.putExtra("isGrouping", groupCode.length() != 0);
        intent.putExtra("audioFile", dolphinSighting.getDolphinAudioFileName());
        startActivityForResult(intent, 3);
    }

    // Call Back method to get data from the Additional Data Activity:
    //                behavior,  social grouping and additional observations
    //  Save the data if return is set to save
    //  Otherwise display the sighting data collected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed,  ie  = 3
        if (requestCode == 3) {
            Log.d("Return Additional", "*******   Returned  from additional  ****** **** ");
            if (resultCode == RESULT_OK) {
                saveOnReturn = data.getBooleanExtra("saveOnReturn", true);
                dolphinSighting.setDolphinBehavior(data.getStringExtra("behavior"));
                dolphinSighting.setDolphinSocialGrouping(data.getStringExtra("socialGrouping"));
                dolphinSighting.setDolphinAdditionalObservation(data.getStringExtra("additionalObservation"));
                dolphinSighting.setDolphinAudioFileName(data.getStringExtra("audioFile"));
                System.out.println("\n  Dolphin returned from additonal\n" + dolphinSighting.shortToString());
                if (groupCode.length() > 0) {
                    socialGrouping = dolphinSighting.getDolphinSocialGrouping();
                }
                if (saveOnReturn) {
                    callSave();
                } else {
                    displayMessage(dolphinSighting.shortToString());
                }
                System.out.println("/n  Group and grouping  " + groupCode + "    " + socialGrouping);
            }
        }
    }
//
//  method to test if image is to be set to selected state or reset to unselected
//  start of resource id for group of attributes, ie. size, color, energy, swimming, acoustics
//  end of resource id for group of attributes, ie. size, color, energy, swimming, acoustics
//  id of attributes to select/unselect
//  text for the attribute to select/unselect
//

    public String manageAttribute(int idStart, int idEnd, int idAttribute, String attribute) {
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

    //  reset all images to unselected image
//  id to skip is set to -1 so no id is skipped
//
    public void resetAllImage() {
        resetMapImage();
        manageResetButton(0, 17, -1);
    }

    public void resetMapImage() {
        ImageView imageView = (ImageView) findViewById(R.id.polarMapImageView);
        imageView.setImageResource(mapResource);
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

    //   Method to set the image for the ImageButton selected or deselected.
//   The input parameter integer refers to the button selected.
//   The input parameter integer refers to the image resource.
//
    public void changeImageOnButton(int idButton, int idResource) {
        imageButton = (ImageButton) findViewById(idButton);
        imageButton.setImageResource(idResource);
    }

    //  Display the current contents of the dolphin sighting object
//
    public void displayMessage(String message) {
        TextView sightingTextView = (TextView) findViewById(R.id.sightingTextView);
        sightingTextView.setText(message);
    }

    // toggle the grouping.  if on,  turn off;  if off, turn on.
    public void onClickGroupButton(View V) {
        Button groupButton = (Button) findViewById(R.id.groupButton);
        if (groupCode.length() > 0) {
            resetGroupCode();
        } else {
            groupCode = Time.getFullDateTimeCondensed();
            socialGrouping = dolphinSighting.getDolphinSocialGrouping();
            groupButton.setText(getResources().getString(R.string.action_group_on));
            groupButton.setTextColor(Color.WHITE);
            groupButton.setBackgroundColor(Color.RED);
        }
    }

    //    Reset the Group code.
    private void resetGroupCode() {
        Button groupButton = (Button) findViewById(R.id.groupButton);
        groupCode = "";
        socialGrouping = "";
        dolphinSighting.setDolphinGroupCode(groupCode);
        dolphinSighting.setDolphinSocialGrouping(socialGrouping);
        groupButton.setText(getResources().getString(R.string.action_group_off));
        groupButton.setTextColor(Color.WHITE);
        groupButton.setBackgroundColor(Color.BLUE);
    }
    // *****************************************************************************
    // manage MenuItem access
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_additional:
                callAdditional();
                break;

            case R.id.action_save:
                callSave();
                break;

            case R.id.action_create_csv:
                if (canWrite) {
                    Export();
                } else {
//                    Toast.makeText(this, "Cannot export due to write permission being denied. Please change in app settings.",
//                            Toast.LENGTH_LONG).show();
                    msg.displayErrorMessage(Messages.TYPE.EXPORT_DENIED);
                }
                break;

            case R.id.action_send_email:
                SendEmail();
                break;

            case R.id.action_delete_db:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.build_delete_title));
                builder.setMessage(getResources().getString(R.string.build_delete_message));
                builder.setCancelable(true);
                builder.setPositiveButton(getResources().getString(R.string.build_delete_positive),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelp.DeleteAllInfo();
                            }
                        });
                builder.setNegativeButton(getResources().getString(R.string.build_delete_negative),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;

            case R.id.action_show_all_data:
                Intent intent = new Intent(this, DolphinHistoryActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    // ***********************************************************************************************
    //  export data to local csv file
    private void Export() {
        wrt = new CSVWriter(this);
        wrt.WriteCSVFile();
        Toast.makeText(this, "Data Exported to CSV File!", Toast.LENGTH_LONG).show();
    }

    private void SendEmail() {
        wrt = new CSVWriter(this);
        File attachment = wrt.WriteCSVFile();

        if (attachment != null) {
            Uri uri = Uri.fromFile(attachment);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/html");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"DolphinTrackers@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Dolphin Tracker app CSV file");
            i.putExtra(Intent.EXTRA_TEXT, "The CSV file created on " + Time.getDate() + " at " + Time.getTime() + ".");
            i.putExtra(Intent.EXTRA_STREAM, uri);

            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
                Toast.makeText(this, "CSV Data File Emailed!", Toast.LENGTH_LONG).show();
            } catch (android.content.ActivityNotFoundException ex) {
//                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
                msg.displayErrorMessage(Messages.TYPE.NO_EMAIL_SETUP);
            }
        }
    }
    // **********************************************************************************************
    // Request permission for read and write local storage and location access and audio recording
    //
    //  verify permission was granted for write to local storage
    private boolean verifyWritePermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);  // Checks if the permission has already been granted

        if (permission != PackageManager.PERMISSION_GRANTED)  // Permission has not yet been granted
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);  // Request permission
            return false;
        } else {
            canWrite = true;
            verifyRecordAudioPermission();
        }
        return true;
    }

    private boolean verifyRecordAudioPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_AUDIO, 1);
            return false;
        } else {
            canRecordAudio = true;
            verifyGpsPermission();
        }

        return true;
    }

    private boolean verifyGpsPermission()
    {
        haveAskedPermission = true;

        if (!checkGpsPermission())  // Permission has not yet been granted
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS_GPS, 1);  // Request permission
            isAvailableGPS = false;
            return false;
        }

        if(checkIfGpsEnabled())
        {
            isAvailableGPS = true;
            gpsHelper.restartGPS();
        }

        else
        {
            Toast.makeText(this, "GPS is disabled!", Toast.LENGTH_LONG).show();

            isAvailableGPS = false;
            return false;
        }

        Log.d("verifyGpsPermission: ", String.valueOf(haveAskedPermission));

        return true;
    }

    private boolean recheckGPS()
    {
        isAvailableGPS = checkGpsPermission() && checkIfGpsEnabled();
        return isAvailableGPS;
    }

    private boolean checkGpsPermission()
    {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);  // Checks if the permission has already been granted
        return permission == PackageManager.PERMISSION_GRANTED;
    }


    private boolean checkIfGpsEnabled()
    {
        LocationManager lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER); // Checks if GPS is enabled for the device
    }


    // **********************************************************************************************
    // Request permission for read and write local storage and location access and audio recording
    //
    @Override
    public void onRequestPermissionsResult(int reqCode, String[] perms, int[] results) {
        //  Any time a permission is granted or denied, it comes through this method
        if (reqCode == 1) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED)  // If the user grants permission
            {
                switch (perms[0]) {
                    case "android.permission.WRITE_EXTERNAL_STORAGE":
                        canWrite = true;
                        verifyRecordAudioPermission();
                        break;

                    case "android.permission.RECORD_AUDIO":
                        canRecordAudio = true;
                        verifyGpsPermission();
                        break;

                    case "android.permission.ACCESS_FINE_LOCATION":  // Name of the permission
                        gpsHelper.restartGPS();
                        break;
                }
            }

            if (results.length > 0 && results[0] != PackageManager.PERMISSION_GRANTED) // The user declined the permission
            {
                switch (perms[0]) {
                    case "android.permission.WRITE_EXTERNAL_STORAGE":
                        canWrite = false;
                        verifyRecordAudioPermission();
                        break;

                    case "android.permission.RECORD_AUDIO":
                        canRecordAudio = false;
                        verifyGpsPermission();
                        break;
                }
            }
        }
    }

    // *************************************************************************************************
    // Register Compass sensors and verify/request permission to access GPS location
    //
    //  Continuously updates the Heading.
    //  This may get changed later if battery life becomes an issue.
    //  May create way to turn this on or off depending on when the reading is actually needed.
    @Override
    public void onSensorChanged(SensorEvent event) {
        //  [0] == Magnetic heading   [1] == True north heading
        int[] headings = compass.GetHeading(event);

        baseHeading = headings[1];  // Only saving true north to the database as of now
//        Log.d("Magnetic Heading: ", String.valueOf(headings[0]));
//        Log.d("True Heading: ", String.valueOf(headings[1]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Required to have
    }

    private void register() {
        AccelerometerManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        MagneticSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (AccelerometerManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = AccelerometerManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            AccelerometerManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            isAvailableCompass = true;
        } else {
            sensorFailMessage("an accelerometer");
            isAvailableCompass = false;
        }

        if (MagneticSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            GPS = MagneticSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            MagneticSensorManager.registerListener(this, GPS, SensorManager.SENSOR_DELAY_NORMAL);
            isAvailableCompass = true;
        } else {
            sensorFailMessage("a magnetic field");
            isAvailableCompass = false;
        }
    }

    private void sensorFailMessage(String sensor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sensor missing");
        builder.setMessage("This device is not equipped with " + sensor + " sensor.  This app will not function properly on this device.");

        builder.setCancelable(false);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isAvailableGPS) {
            gpsHelper.stopGPS(); // Stop the GPS when the screen disappears
            GpsHelper.isLocked = false;
        }
    }

    @Override
    protected void onStop() {
        String path = Environment.getExternalStorageDirectory() + "/Dolphin Tracker/Audio Files/" + dolphinSighting.getDolphinAudioFileName();

        super.onStop();

        File f = new File(path);

        if (f.exists() && !saveAudio) {
            f.delete();  // Deletes the current audio file for the last particular entry if the user closed the app without hitting save.  Used to reduce "junk" files
        }

        if(isAvailableGPS)
        {
            gpsHelper.forceStop();  // Stop the GPS
            GpsHelper.isLocked = false;
        }

    }

    private void reCheckPermissions() {
        int permission;

        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        canWrite = permission == PackageManager.PERMISSION_GRANTED;

        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        canRecordAudio = permission == PackageManager.PERMISSION_GRANTED;

        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);  // Checks if the permission has already been granted

        if (permission == PackageManager.PERMISSION_GRANTED && checkIfGpsEnabled())  // Permission has not yet been granted
        {
            isAvailableGPS = true;
            gpsHelper.restartGPS();
        } else {
            isAvailableGPS = false;
        }
    }
}