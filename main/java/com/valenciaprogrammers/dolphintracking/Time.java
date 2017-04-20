package com.valenciaprogrammers.dolphintracking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Brannon Martin on 2/12/2017.
 * <p>
 * This class will have all the methods for getting date/time, just the date, or just the time.
 * It will also have the methods to parse a date/time into just the date or just the time.
 */

public class Time {
    public static String getFullDateTime() // Returns a string with "yyyy-MM-dd HH:mm:ss" format
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getFullDateTimeCondensed() // Returns a string with "yyyyMMdd-HHmmss" format
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate()  // Returns a string with "yyyy-MM-dd" format
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTime()  // Returns a string with "HH:mm:ss" format
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateFromDateTime(String dt)  //  Accepts a full date time string and returns a string with "yyyy-MM-dd" format
    {
        String[] time = {"", ""};  // Initialized with empty strings.  Cannot split a null value, but need to return something other than null

        if (dt != null)
            time = dt.split(" ");

        return time[0];
    }

    public static String getTimeFromDateTime(String dt)  //  Accepts a full date time string and returns a string with "HH:mm:ss" format
    {
        String[] time = {"", ""};  // Initialized with empty strings.  Cannot split a null value, but need to return something other than null

        if (dt != null)
            time = dt.split(" ");

        return time[1];
    }
}
