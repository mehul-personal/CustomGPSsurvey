package com.analytics.customgpssurvey.utils;

import android.content.Context;
import android.widget.Toast;

/****************************MODIFICATION HISTORY***************************
 * Date		    |  Allocation No./Enhancement/	    | Purpose/Description Redmine Ticket
 *---------------------------------------------------------------------------------------------------------------------------
 * 27-Aug-2015 	|  Release 1.0			            | Created new Redmine#
 *---------------------------------------------------------------------------------------------------------------------------
 /****************************MODIFICATION HISTORY***************************/

public class ToastMsg {
    /**
     * This will show toast message for long time.
     * @param context Context object for reference.
     * @param message String message to be displayed in toast.
     */
    public static void showLong(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }

    /**
     * This will show toast message for short time.
     * @param context Context object for reference.
     * @param message String message to be displayed in toast.
     */
    public static void showShort(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }
}
