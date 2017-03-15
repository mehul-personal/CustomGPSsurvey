package com.analytics.customgpssurvey.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MeHuL on 27-12-2016.
 */

public class FormDetailData {
    @SerializedName("type")
    private String type;

    @SerializedName("label")
    private String label;

    @SerializedName("input_id")
    private String input_id;

    @SerializedName("groupLabel")
    private String group_label;

    @SerializedName("group_label_id")
    private String group_label_id;

    @SerializedName("options")
    private List<FormDetailOption> optionsList;

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getInput_id() {
        return input_id;
    }

    public String getGroup_label() {
        return group_label;
    }

    public String getGroup_label_id() {
        return group_label_id;
    }

    public List<FormDetailOption> getOptionsList() {
        return optionsList;
    }

    public class FormDetailOption {
        @SerializedName("label")
        private String label;

        @SerializedName("value")
        private String value;

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }
}