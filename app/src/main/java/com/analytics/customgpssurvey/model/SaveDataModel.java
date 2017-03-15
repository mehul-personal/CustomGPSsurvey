package com.analytics.customgpssurvey.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MeHuL on 09-02-2017.
 */

public class SaveDataModel extends RealmObject {
    String JsonData, RecordStatus,DisplayData;

    String form_id;

    @PrimaryKey
    private long id;

    public String getJsonData() {
        return JsonData;
    }

    public void setJsonData(String jsonData) {
        JsonData = jsonData;
    }

    public String getRecordStatus() {
        return RecordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        RecordStatus = recordStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayData() {
        return DisplayData;
    }

    public void setDisplayData(String displayData) {
        DisplayData = displayData;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }
}
