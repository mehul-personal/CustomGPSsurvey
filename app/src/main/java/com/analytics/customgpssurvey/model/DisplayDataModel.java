package com.analytics.customgpssurvey.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by MeHuL on 31-01-2017.
 */

public class DisplayDataModel extends RealmObject {
    String key,value;

    private RealmList<DisplayDataModel> list;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RealmList<DisplayDataModel> getList() {
        return list;
    }

    public void setList(RealmList<DisplayDataModel> list) {
        this.list = list;
    }
}
