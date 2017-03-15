package com.analytics.customgpssurvey.realm;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.analytics.customgpssurvey.model.DisplayDataModel;
import com.analytics.customgpssurvey.model.SaveDataModel;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
//    public void refresh() {
//
//        realm.refresh();
//    }

    //clear all objects from Book.class
//    public void clearAll() {
//
//        realm.beginTransaction();
//        realm.clear(Book.class);
//        realm.commitTransaction();
//    }

    //find all objects in the SaveDataModel.class
    public RealmResults<SaveDataModel> getSurveyDataList(String formid) {

        return realm.where(SaveDataModel.class).equalTo("form_id", formid).findAll();
    }

    //query a single item with the given id
    public int getMaxId() {
        Number currentIdNum = realm.where(SaveDataModel.class).max("id");
        int nextId;
        if (currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }
    //find all objects in the DisplayDataModel.class
    public RealmResults<DisplayDataModel> getDisplayDataList() {

        return realm.where(DisplayDataModel.class).findAll();
    }

    public void deleteData(final long id){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final SaveDataModel saveModel = realm.where(SaveDataModel.class).equalTo("id", id).findFirst();
                if (saveModel != null) {
                    saveModel.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
              //  getAllCategory();
            }
        });

    }
    //check if Book.class is empty
//    public boolean hasBooks() {
//
//        return !realm.allObjects(Book.class).isEmpty();
//    }

    //query example
//    public RealmResults<Book> queryedBooks() {
//
//        return realm.where(Book.class)
//                .contains("author", "Author 0")
//                .or()
//                .contains("title", "Realm")
//                .findAll();
//
//    }
}
