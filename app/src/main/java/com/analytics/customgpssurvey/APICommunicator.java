package com.analytics.customgpssurvey;

import android.app.ProgressDialog;
import android.content.Context;

import com.analytics.customgpssurvey.utils.Logger;
import com.analytics.customgpssurvey.utils.VolleyClient;
import com.android.volley.Response;

import org.json.JSONArray;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class APICommunicator {

    public static final String TAG = "APICommunicator";
    public static ProgressDialog pDialog;

    /*
     * TODO ================== Custom GPS Survey API Calls ======================
     */

//    public static void changePassword(Context context, String userid, String password, String newPassword, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
//        JSONObject requestJSON = new JSONObject();
//        try {
//            requestJSON.put("userid", userid);
//            requestJSON.put("currentpassword", password);
//            requestJSON.put("newpassword", newPassword);
//            Logger.printLog(context, TAG, requestJSON.toString());
//        } catch (JSONException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            Logger.printLog(context, TAG, stackTrace.toString());
//            requestJSON = null;
//        }
//
//        VolleyClient v = new VolleyClient();
//        v.sendRequest(context, context.getString(R.string.api_changepassword), requestJSON, LoginSuccessListener, FailureListener);
//    }


//    public static void getAllCountries(Context context, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
//        VolleyClient v = new VolleyClient();
//        v.sendRequest(context, context.getString(R.string.api_getAllCountries), LoginSuccessListener, FailureListener);
//    }

    public static void login(Context context, String userName, String password, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
        Map<String, String> requestJSON = new HashMap<String, String>();
        requestJSON.put("username", userName.trim());
        requestJSON.put("password", password.trim());
        VolleyClient v = new VolleyClient();
        v.sendRequest(context, context.getString(R.string.api_login), requestJSON, LoginSuccessListener, FailureListener);
    }
    public static void getProjectList(Context context, String userid, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
        Map<String, String> requestJSON = new HashMap<String, String>();
        requestJSON.put("user_id", userid.trim());
        VolleyClient v = new VolleyClient();
        v.sendRequest(context, context.getString(R.string.api_get_project), requestJSON, LoginSuccessListener, FailureListener);
    }
    public static void getFormList(Context context, String userid,String projectid, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
        Map<String, String> requestJSON = new HashMap<String, String>();
        requestJSON.put("user_id", userid.trim());
        requestJSON.put("project_id",projectid.trim());
        VolleyClient v = new VolleyClient();
        v.sendRequest(context, context.getString(R.string.api_get_form), requestJSON, LoginSuccessListener, FailureListener);
    }
    public static void getFormGetData(Context context,String userid, String formid, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
        Map<String, String> requestJSON = new HashMap<String, String>();
        requestJSON.put("formid", formid.trim());
        requestJSON.put("userid", userid.trim());
        VolleyClient v = new VolleyClient();
        v.sendRequest(context, context.getString(R.string.api_get_data), requestJSON, LoginSuccessListener, FailureListener);
    }
public static void getImageData(Context context,String imageid,Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener){
    Map<String, String> requestJSON = new HashMap<String, String>();
    requestJSON.put("record_id", imageid);
    VolleyClient v = new VolleyClient();
    v.sendRequest(context, context.getString(R.string.api_get_image), requestJSON, LoginSuccessListener, FailureListener);
}
    public static void getFormDetailList(Context context, String formid, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
        Map<String, String> requestJSON = new HashMap<String, String>();
        requestJSON.put("formid", formid.trim());
        VolleyClient v = new VolleyClient();
        v.sendRequest(context, context.getString(R.string.api_get_form_detail), requestJSON, LoginSuccessListener, FailureListener);
    }

    public static void saveSingleData(Context context, Map<String, String> requestJSON, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
        VolleyClient v = new VolleyClient();
        v.sendRequest(context, context.getString(R.string.api_save_data), requestJSON, LoginSuccessListener, FailureListener);
    }
    public static void saveMultipleData(Context context, JSONArray requestJSONArray, Response.Listener LoginSuccessListener, Response.ErrorListener FailureListener) {
        VolleyClient v = new VolleyClient();
        Logger.printLog(context, TAG + " - sunc multiple", requestJSONArray.toString());
        v.sendRequest(context, context.getString(R.string.api_save_data), requestJSONArray, LoginSuccessListener, FailureListener);
    }
    //=================Below are temporary ========================

    /**
     * This method will display the progress dialog with given message. If the dialog is already displaying it will just change the message.
     *
     * @param context class context
     * @param message message to be displayed
     */
    public static void showProgress(Context context, String message) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.setMessage(message);
            try {
                pDialog.show();
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                //Logger.printLog(context, TAG, stackTrace.toString());
            }
        } else {
            pDialog.setMessage(message);
        }
    }

    /**
     * This method will dismiss the progress dialog
     */
    public static void stopProgress() {
        try {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
