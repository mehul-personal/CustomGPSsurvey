package com.analytics.customgpssurvey.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MeHuL on 25-12-2016.
 */

public class FormDetailModel {
    public static final String TAG = "data";

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private List<FormDetailData> data;

    public String getMsg() {
        return msg;
    }

    public List<FormDetailData> getData() {
        return data;
    }


}
