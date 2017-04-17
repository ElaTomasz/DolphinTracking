package com.valenciaprogrammers.dolphintracking;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.widget.Toast;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


/**
 * Created by Brannon Martin on 2/18/2017.
 */

public class CSVWriter extends Activity
{
    Context context; //  Needs the context from the MainActivity
    String parentFolderName = "/Dolphin Tracker";
    String subFolderName = "/CSV Files";
    String folders = parentFolderName + subFolderName;
    String folderPath = Environment.getExternalStorageDirectory() + folders;
    private String fileName = "DolphinCSV";
    private Messages msg = null;

    public CSVWriter(Context context)
    {
        this.context = context;
        msg = new Messages(this.context);
    }

    private String CreateCSVString()
    {
        String result = "";
        Cursor cur = GetDatabaseInfo();
        String[] columns = cur.getColumnNames();

        //  Split each DateTime column into separate date and time columns
        for(String s : columns)
        {
            Log.d("Date Time", "*****  "+s);
            switch (s)
            {
                case DolphinContract.DolphinTable.ENTERED_DATE_TIME:
                    result += "Entered_Date,Entered_Time,";
                    break;

                case DolphinContract.DolphinTable.OBSERVED_DATE_TIME:
                    result += "Observed_Date,Observed_Time,";
                    break;

                case DolphinContract.ObservationTable.START_DATE_TIME:
                    result += "Start_Date,Start_Time,";
                    break;

                case DolphinContract.ObservationTable.END_DATE_TIME:
                    result += "End_Date,End_Time,";
                    break;

                default:
                    result += s + ",";
            }
        }

        result += "\n";  //  Add a line after the column headers

        while (cur.moveToNext())
        {
            int count  = cur.getColumnCount();

            //  Take each DateTime string and split it
            for(int i = 0; i < count; i++)
            {
                switch (i)
                {
                    case DolphinContract.ENTERED_DATE_TIME_INDEX:  //  This is Entered_DateTime
                        result += Time.getDateFromDateTime(cur.getString(i)) + ",";
                        result += Time.getTimeFromDateTime(cur.getString(i)) + ",";
                        break;

                    case DolphinContract.OBSERVED_DATE_TIME_INDEX:  //  This is Observed_DateTime
                        result += Time.getDateFromDateTime(cur.getString(i)) + ",";
                        result += Time.getTimeFromDateTime(cur.getString(i)) + ",";
                        break;

                    case DolphinContract.START_DATE_TIME_INDEX:  //  This is Start_DateTime
                        result += Time.getDateFromDateTime(cur.getString(i)) + ",";
                        result += Time.getTimeFromDateTime(cur.getString(i)) + ",";
                        break;

                    case DolphinContract.END_DATE_TIME_INDEX:  //  This is End_DateTime
                        result += Time.getDateFromDateTime(cur.getString(i)) + ",";
                        result += Time.getTimeFromDateTime(cur.getString(i)) + ",";
                        break;

                    default: result += cur.getString(i) + ",";  //  All columns that are not date/time
                        break;
                }
            }
            result += "\n";
        }
        cur.close();
        return result;
    }

    private Cursor GetDatabaseInfo()
    {
        //  Gets all the info out of the database
        ADDatabaseHelper help = new ADDatabaseHelper(context);
        return help.readAllDataFromDB();
    }

    public File WriteCSVFile()
    {
        File file = GetCSVFile();

        String csvInfo = CreateCSVString();
        Log.d("Record to write", "****   "+csvInfo);
        try
        {
            FileOutputStream fos = new FileOutputStream(file,false);
            OutputStreamWriter os = new OutputStreamWriter(fos);
            os.write(csvInfo);
            os.close();
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
//            Toast.makeText(context, "Error creating file", Toast.LENGTH_LONG).show();
            msg.displayErrorMessage(Messages.TYPE.NO_FILE);
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
//            Toast.makeText(context, "Error creating file", Toast.LENGTH_LONG).show();
            msg.displayErrorMessage(Messages.TYPE.WRITE_ERROR);
            return null;
        }
        return file;
    }

    private File GetCSVFile()
    {
        File newFile = new File(Environment.getExternalStorageDirectory(), folders);
        if(!newFile.exists())
        {
            newFile.mkdirs();
        }

        File path = new File(folderPath);
        String temp = Time.getFullDateTimeCondensed();
        String dolphinFileName = fileName + "_" + temp + ".csv";
        File file = new File(path, dolphinFileName);

        return file;
    }
}
