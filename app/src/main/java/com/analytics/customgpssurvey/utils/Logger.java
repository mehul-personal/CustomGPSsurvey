package com.analytics.customgpssurvey.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

/****************************MODIFICATION HISTORY***************************
 * Date		    |  Allocation No./Enhancement/	    | Purpose/Description Redmine Ticket
 *---------------------------------------------------------------------------------------------------------------------------
 * 27-Aug-2015 	|  Release 1.0			            | Created new Redmine#
 *---------------------------------------------------------------------------------------------------------------------------
 /****************************MODIFICATION HISTORY***************************/

public class Logger {
    /**
     * This will show the log message in logcat with the given TAG.
     * If the isDebugOn is set to false in GlobalObjects class it will not print any log.
     *
     * @param TAG generally the class name to identify the call region.
     * @param message String message to be displayed in log.
     */
    public static void printLog(Context context, String TAG, String message){
        if(GlobalObjects.isDebugOn){
            Log.e(TAG, message+"");
            appendLog(context, TAG+"\t -> \t"+message);
        }
    }

    public static void appendLog(Context context, String strGeneralEvent) {
        try {
            if(!GlobalObjects.isDebugOn){
                return;
            }
            File logDir = new File(CommonMethods.getProjectDirectory(context) + "/Logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            File file = new File(logDir.getAbsolutePath(), "Log.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

//			Date date = new Date();
            FileWriter fr = new FileWriter(file, true);
            fr.write("\n");
            fr.write(DateFormat.format(StringUtils.getDate_yyyyMMddHHmmss(), new Date()).toString());
            fr.write( "\t:\t"  + strGeneralEvent);
            fr.close();
        } catch (Exception e) {
        }
    }
}
