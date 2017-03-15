package com.analytics.customgpssurvey.utils;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

public class VolleyClient {
    public static final String TAG = "VolleyClient";
    private static RequestQueue requestQueue = null;

    public void sendRequest(Context mContext, String apiName, final Map<String, String> mParams, Response.Listener<String> mSuccessListener, Response.ErrorListener mErrorListener) {
        if (mParams != null) {
            String url = apiName;
            //Logger.printLog(mContext, "url {}", url.toString());
       ///     Logger.printLog(mContext, "params {}", mParams.toString());
//            StringRequest loginRequest = new StringRequest(POST, url, mParams, mSuccessListener, mErrorListener);
            StringRequest loginRequest = new StringRequest(POST, url, mSuccessListener, mErrorListener) {
                @Override
                protected Map<String, String> getParams() {
                    return mParams;
                }
            };
            loginRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
            //loginRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            loginRequest.setTag(apiName);
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(mContext);
            }
            requestQueue.add(loginRequest);
        }
    }

    public void sendRequest(Context mContext, String apiName, final JSONObject mParams, Response.Listener<JSONArray> mSuccessListener, Response.ErrorListener mErrorListener) {
        if (mParams != null) {
            String url = apiName;
            //Logger.printLog(mContext, "url {}", url.toString());
          //  Logger.printLog(mContext, "params {}", mParams.toString());
            // StringRequest loginRequest = new StringRequest(POST, url, mParams, mSuccessListener, mErrorListener);
            try {
                mParams.put("Content-Type", "application/json");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonArrayRequest loginRequest = new JsonArrayRequest(POST, url, mParams, mSuccessListener, mErrorListener);

            loginRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
            //loginRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            loginRequest.setTag(apiName);
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(mContext);
            }
            requestQueue.add(loginRequest);
        }
    }
    public void sendRequest(Context mContext, String apiName, final JSONArray mParams, Response.Listener<JSONArray> mSuccessListener, Response.ErrorListener mErrorListener) {
        if (mParams != null) {
            String url = apiName;
            //Logger.printLog(mContext, "url {}", url.toString());
          //  Logger.printLog(mContext, "params {}", mParams.toString());
            // StringRequest loginRequest = new StringRequest(POST, url, mParams, mSuccessListener, mErrorListener);
            try {
                JSONObject jsob=new JSONObject();
                jsob.put("Content-Type", "application/json");
                mParams.put(jsob);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsonArrayRequest loginRequest = new JsonArrayRequest(POST, url, mParams, mSuccessListener, mErrorListener);

            loginRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
            //loginRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            loginRequest.setTag(apiName);
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(mContext);
            }
            requestQueue.add(loginRequest);
        }
    }
    public void sendRequest(Context mContext, String apiName, Response.Listener<String> mSuccessListener, Response.ErrorListener mErrorListener) {
        String url = apiName;
        //Logger.printLog(mContext, "url {}", url.toString());
        StringRequest loginRequest = new StringRequest(POST, url, mSuccessListener, mErrorListener);
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        loginRequest.setTag(apiName);
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        requestQueue.add(loginRequest);
    }

    public void sendRequestWithArray(Context mContext, String apiName, Response.Listener<JSONArray> mSuccessListener, Response.ErrorListener mErrorListener) {
        String url = apiName;
        //Logger.printLog(mContext, "url {}", url.toString());
        JsonArrayRequest loginRequest = new JsonArrayRequest(POST, url, mSuccessListener, mErrorListener);
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        loginRequest.setTag(apiName);
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        requestQueue.add(loginRequest);
    }

    /**
     * This method will return string as response from the given URL
     *
     * @param mContext for reference
     * @param url      to get response from.
     * @return String response of the given URL.
     */
    public String getRsponseOfURL(Context mContext, String url) {
        url = url.replace(" ", "%20");
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest loginRequest = new StringRequest(GET, url, future, future);
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        loginRequest.setTag(url);
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        requestQueue.add(loginRequest);
        try {
            String resp = future.get(30, TimeUnit.SECONDS);
            future.cancel(true);
            return resp;
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            //Logger.printLog(mContext, TAG, stackTrace.toString());
        }
        return null;
    }

}