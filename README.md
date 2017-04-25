# DolphinTracking
The app allows the capture dolphin sighting data when run on an 10” Android tablet.  Besides the data, the user can enter/select, if the tablet has the sensors for compass reading and/or GPS location capture, the app will obtain theses readings to save along with the user entered/selected sighting data.
The dolphin sighting attributes that can be selected are:  dolphin size, color, energy, swimming movement, acoustics characteristics, behavior activities and social grouping.  There are also 7 buttons that can be ‘defined’ for unpredicted behavior.
A polar plot is displayed on the main screen to allow recording the position the dolphin is sighted in relation to the recording location; usually a boat.  If the dolphin is moving in a visible direction, the direction can also be recorded.
While the app has many attributes that can be selected, it also allows the audio recording or text entry of additional information to be saved with the sighting data.

 * The app was developed using Android Studio version 2.3.1
 * Build SDK API 25: Android 7.1.1 (Nougat)

 * For GPS classes:
        In order for this class to work, you must put the following line of code in the build.gradle(Module: app) file
        compile 'com.google.android.gms:play-services-location:8.4.0'
        
        
