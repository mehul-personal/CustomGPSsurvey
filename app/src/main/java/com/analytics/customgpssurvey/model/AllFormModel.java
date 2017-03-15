package com.analytics.customgpssurvey.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MeHuL on 25-12-2016.
 */

public class AllFormModel {
    public static final String TAG = "data";

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private List<FormData> data;

    public class FormData{
        @SerializedName("userid")
        private String userid;

        @SerializedName("form_id")
        private String formid;

        @SerializedName("description")
        private String description;

        public String getUserid() {
            return userid;
        }

        public String getFormid() {
            return formid;
        }

        public String getDescription() {
            return description;
        }
    }


    public String getMsg() {
        return msg;
    }

    public List<FormData> getData() {
        return data;
    }
}
