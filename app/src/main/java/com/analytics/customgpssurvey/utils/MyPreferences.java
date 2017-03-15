package com.analytics.customgpssurvey.utils;

import android.content.Context;
import android.content.SharedPreferences;

/****************************
 * MODIFICATION HISTORY***************************
 * Date		    |  Allocation No./Enhancement/	    | Purpose/Description Redmine Ticket
 * ---------------------------------------------------------------------------------------------------------------------------
 * 27-Aug-2015 	|  Release 1.0			            | Created new Redmine#
 * 07-Sep-2015  | Login process                     |  Modified for Login process
 * 03-Nov-2015  | Login Session_hash                | Modified for Login
 * 22-Dec-2015  | GetBrandingMaterial               | Added GetBrandingMaterial related content
 * 27-Apr-2016  | ParkingAlert                      | Parking Alert settings prefs for auto/manual and update alert time.
 * 29-Apr-2016  |  Redmine#6061                     | Anonymous login
 * 17-May-2016  | Local Redmine:5737                | Google Direction / ARS RPS implementation
 * 24-Jun-2016  | Ext Redmine 6362                  | Changed response of isGoogleDirectionSelected to true
 * 24-Jun-2016  | Ext Redmine 6361                  | Changed response of isInitialParkingAuto and isSecondaryParkingAuto to false and getParkingAlertTime to 10
 * ---------------------------------------------------------------------------------------------------------------------------
 * /****************************MODIFICATION HISTORY
 ***************************/

public class MyPreferences {
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    private static final String PREFERENCES_NAME = "CUSTOM_GPS_SURVEY_PREFERENCE";
    private static final String USER_ID = "user_id";
    private static final String IS_LOGGED_IN = "is_logged_in";
    private static final String SET_OFFLINE_PROJECT_LIST = "offline_project_list";
    private static final String SET_OFFLINE_FORM_LIST = "offline_form_list";
    private static final String SET_OFFLINE_FORM_DATA = "offline_form_data";
    private static final String IS_FRIENDS_NOTIF_ON = "is_friends_notif_on";
    private static final String IS_CHAT_NOTIF_ON = "is_chat_notif_on";
    private static final String REGISTRATION_TOKEN = "registration_token";
    private static final String CURRENT_LATITUDE = "current_latitude";
    private static final String CURRENT_LONGITUDE = "current_longitude";
    private static SharedPreferences preferences;
    private Context context;
    private  static SharedPreferences.Editor editor;

    /**
     * Constructor with context to initialize the MyPreference object.
     *
     * @param context Context object.
     */
    public MyPreferences(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREFERENCES_NAME, 0);
        editor = preferences.edit();
        editor.commit();
    }

    /**
     * this method will return boolean value for login status.
     *
     * @return boolean value true if the user is logged in else false.
     */
    public boolean isLoggedIn() {
        if (preferences == null) {
            return false;
        } else
            return preferences.getBoolean(IS_LOGGED_IN, false);
    }

    /**
     * This will set the login status of the user as boolean value.
     *
     * @param isLoggedIn boolean value true or false.
     */
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.commit();
    }



    /**
     * This method will return stored unique ID.
     *
     * @return String value of user_id
     */
    public String getUserID() {
        return preferences.getString(USER_ID, "");
    }

    /**
     * This method will store the user login ID in preference.
     *
     * @param user_id String value for user login ID.
     */
    public void setUserID(String user_id) {
        editor.putString(USER_ID, user_id);
        editor.commit();
    }

    /**
     * This method will return the offline project else blank value.
     *
     * @return Get json for project list data.
     */
    public String getOfflineProjectListData() {
        String json = preferences.getString(SET_OFFLINE_PROJECT_LIST, "");
        return json;
    }

    /**
     * This method will store the project values in the preference.
     *
     * @param projectData String value for the set project data.
     */
    public void setOfflineProjectListData(String projectData) {
        editor.putString(SET_OFFLINE_PROJECT_LIST, projectData);
        editor.commit();
    }

    /**
     * This method will return the offline form else blank value.
     *@param projectId String value for the form id
     * @return Get json for form list data.
     */
    public String getOfflineFormListData(String projectId) {
        String json = preferences.getString(SET_OFFLINE_FORM_LIST+projectId, "");
        return json;
    }

    /**
     * This method will store the user profile values in the preference.
     *@param projectId String value for the form id
     * @param formData String value for the form list data.
     */
    public void setOfflineFormListData(String  formData,String projectId) {
        editor.putString(SET_OFFLINE_FORM_LIST+projectId, formData);
        editor.commit();
    }


    /**
     * This method will return then Gcm Registration Token value.
     *@param formID formid
     * @return json string of form create
     */
    public String getOfflineFormCreateData(String formID) {
        return preferences.getString(SET_OFFLINE_FORM_DATA+formID, "");
    }

    /**
     * This method will store the Gcm Registration Token vlaue in the preference.
     *@param formID formid
     * @param formCreateData json string of form create
     */
    public void setOfflineFormCreateData(String formCreateData,String formID) {
        editor.putString(SET_OFFLINE_FORM_DATA+formID, formCreateData);
        editor.commit();
    }

    /**
     * This method will set the default values for user specific data
     */
    public void clearUserDefaultValues() {
        setLoggedIn(false);
//        setUserProfile(null);
//        setUserLoginMessage(null);
        setCurrentLatitude("0.0");
        setCurrentLongitude("0.0");
    }

    /**
     * This method will return then current saved latitude.
     *
     * @return
     */
    public String getCurrentLatitude() {
        return preferences.getString(CURRENT_LATITUDE, "0.0");
    }

    /**
     * This method will store the Gcm Registration Token vlaue in the preference.
     *
     * @param CurrentLatitude
     */
    public void setCurrentLatitude(String CurrentLatitude) {
        editor.putString(CURRENT_LATITUDE, CurrentLatitude);
        editor.commit();
    }

    /* This method will return then current saved longitude.
            * @return
            */
    public String getCurrentLongitude() {
        return preferences.getString(CURRENT_LONGITUDE, "0.0");
    }

    /**
     * This method will store the Gcm Registration Token vlaue in the preference.
     *
     * @param CurrentLongitude
     */
    public void setCurrentLongitude(String CurrentLongitude) {
        editor.putString(CURRENT_LONGITUDE, CurrentLongitude);
        editor.commit();
    }
}
