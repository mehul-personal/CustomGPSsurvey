package com.analytics.customgpssurvey.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MeHuL on 25-12-2016.
 */

public class ProjectModel {
    public static final String TAG = "data";

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private List<ProjectData> data;

    public class ProjectData{
        @SerializedName("project")
        private String project;

        @SerializedName("id")
        private String id;

        public String getProject() {
            return project;
        }

        public String getId() {
            return id;
        }
    }


    public String getMsg() {
        return msg;
    }

    public List<ProjectData> getData() {
        return data;
    }
}
