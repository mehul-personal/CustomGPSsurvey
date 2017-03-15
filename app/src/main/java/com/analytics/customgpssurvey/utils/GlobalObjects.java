package com.analytics.customgpssurvey.utils;


public class GlobalObjects {
    /**
     * Set isDebugOn to true for debug build and false when you are going for production build.
     * Every one must have to keep track of this variable for test and production build.
     */
    public static boolean isDebugOn = true;

    /**
    * Set isLibDebugOn to true for build with Navigation Library DebugOn else false when you are going for production build.
    * Every one must have to keep track of this variable for test and production build.
    */
    public static boolean isLibDebugOn = true;

    /**
     * Set IS_TEST_ENVIRMENT to true for build with Test routes else false when you are going for Client build.
     * Every one must have to keep track of this variable for test and production build.
     *
     * To make build for Indian city search replace NLD to IND in api_autocomplete in server_api.xml file
     */
    public static boolean IS_TEST_ENVIRMENT = true;

    /**
     * This code will be returned to calling activity on success of result
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * This code will be returned to calling activity on failure of result
     */
    public static final int FAILURE_CODE = 404;

    /**
     * This code will be returned to calling activity on session expire status
     */
    public static final int SESSION_EXPIRE_CODE = 400;

    /**
     * This code will be returned when the file is uploaded successfully
     */
    public static final int FILE_SUCCESS_CODE = 200;

    /**
     * This code will be returned when the file is uploaded successfully with different size
     */
    public static final int FILE_SIZE_DIFF_CODE = 201;


    /**
     * This array is to represent the alarm time index used in Planned Route
     */
    public static final int[] reminderTime = new int[]{0,5,10,15,30,45,60,75,90,-1};

    /**
     * This will be used in StartNavigation to set interval time in setNoLocationFoundListener method.
     */
    public static final int naviWaitingInterval = 120;
    /**
     * This will be used in StartNavigation to set Radius in setDestinationReachedListener method.
     */
    public static final int  neviRadius = 50;

    /**
     * This will be used to pitch in TTS(Text To Speech).
     */
    public static final float TTS_PITCH = 1.0f;
    /**
     * This will be used in StartNavigation to set Radius in setDestinationReachedListener method.
     */
    public static final float TTS_RATE = 1.0f;

    /**
     * This is request type for ARS RPS api for direction result
     */
    public static final String REQUEST_TYPE_ARS_RPS = "FREEFLOW_ROUTE_PPA_V1";

    /**
     * This is request type for Google api for direction result
     */
    public static final String REQUEST_TYPE_GOOGLE = "FREEFLOW_ROUTE_GOOGLE";
}
