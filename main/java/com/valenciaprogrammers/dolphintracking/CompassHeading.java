package com.valenciaprogrammers.dolphintracking;


/**
 * Created by Isabel Tomaszewski on 2/10/17.
 * <p>
 * Class used to determine the text of the heading
 * <p>
 * North	              0°	South	        180°
 * North-northeast	     22.5°	South-southwest	202.5°
 * Northeast	         45°	Southwest	    225°
 * East-northeast	     67.5°	West-southwest	247.5°
 * East	             90°	West	        270°
 * East-southeast	    112.5°	West-northwest	292.5°
 * Southeast	        135°	Northwest	    315°
 * South-southeast	    157.5°	North-northwest	337.5°
 */

public class CompassHeading {
    public static final String[] COMPASS_POINTS = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};

    public static final double ARC_INTERVALS = 22.5;

    //
//  uses the arc location to it's determine the compass bearing.
//  the arc interval (22.5) divides the arc to use as the index to the array containing
//  the text for the heading.
    public static String getBearing(int location) {
        int indexToDirection = (location - 5) < 0 ? 0 : (int) (((double) (location - 5) / ARC_INTERVALS) + .5);
        return COMPASS_POINTS[indexToDirection];
    }
}
