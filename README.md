# DolphinTracking
The app allows the capture of dolphin sighting data when run on an 10” Android tablet. If the tablet has the sensors for compass reading and/or GPS location capture, the app will obtain theses readings to save along with the user entered/selected sighting data.
The dolphin sighting attributes to selected from include:  dolphin size, color, energy, swimming movement, acoustics characteristics, behavior activities and social grouping.  There are also 7 buttons that can be ‘defined’ for unpredicted behavior.  The app displays all the data saved in seperate screen to allow for quick review of data.
A polar plot is displayed on the main screen to record the position the dolphin is sighted in relation to the recording location; usually a boat.  If the dolphin is moving in a visible direction, the direction can also be recorded.
While the app has many attributes that can be selected, it also allows the audio recording or text entry of additional information to be saved with the sighting data.  Audio recording files are saved to the local directory. 
The app will also allow the user to specify which species is being observed in a location,  ie Inia, Sotalia or both.  The app defaults to Inia until changed.
If there is a group of dolphins together at a location spot, the app can save that location for several sightings along with the time stamp to identify the group.  The app provides 5 different social grouping that can be optionally selected.  These are 'Mother/Calf' (Larger dolphin with calf), 'Babysitting' (larger dolphin with several juveniles), 'Juveniles' (several juveniles together), 'Adults' (Adult dolphins togeher) and 'Mixed Grouping' (several dolphins of different sizes together).
The data is saved in a local sqlite database.  The data can be exported from the database to a csv file which can either be saved to a local directory or emailed.

 * The app was developed using Android Studio version 2.3.1
 * Build SDK API 25: Android 7.1.1 (Nougat)
 * Data Storage is a local 2 table sqlite database.
 * Data/audio files are saved in subfolders of the local directory /Dolphin Tracker.
 * For GPS classes:
        In order for this class to work, you must put the following line of code in the build.gradle(Module: app) file
        compile 'com.google.android.gms:play-services-location:8.4.0'
        
        
