package com.analytics.customgpssurvey;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.analytics.customgpssurvey.adapter.FormDataListAdapter;
import com.analytics.customgpssurvey.model.AllFormModel;
import com.analytics.customgpssurvey.model.GetDataModel;
import com.analytics.customgpssurvey.model.SaveDataModel;
import com.analytics.customgpssurvey.realm.RealmController;
import com.analytics.customgpssurvey.utils.Logger;
import com.analytics.customgpssurvey.utils.MyPreferences;
import com.analytics.customgpssurvey.utils.ToastMsg;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import io.realm.RealmResults;

public class ManageFormActivity extends AppCompatActivity {
    static FormDataListAdapter adapter;
    private final String TAG = "FormListingActivity";
    RecyclerView recyclerView;
    Typeface LibreBoldFont, RobotRegularFont;
    MyPreferences preferences;
    AllFormModel projectFormList;
    String FormID = "", strtitle = "";
    TextView title;
    ImageView addNewFormData, back;
    Button btnSyncAllData, btnSyncSingleData;
    ArrayList<ArrayList<GetDataModel>> DATA_SHOW_LIST;
    JSONArray jsonArray ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_form);

        DATA_SHOW_LIST = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        title = (TextView) findViewById(R.id.title);
        addNewFormData = (ImageView) findViewById(R.id.addNewFormData);
        back = (ImageView) findViewById(R.id.back);
        btnSyncAllData = (Button) findViewById(R.id.btnSyncAllData);
        btnSyncSingleData = (Button) findViewById(R.id.btnSyncSingleData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        preferences = new MyPreferences(this);
        LibreBoldFont = Typeface.createFromAsset(getAssets(), "LibreBaskerville-Bold.otf");
        RobotRegularFont = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        FormID = getIntent().getStringExtra("FORMID");
        strtitle = getIntent().getStringExtra("TITLE");
        title.setText(strtitle.toUpperCase() + " LIST");
        addNewFormData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageFormActivity.this, AddFormDataActivity.class);
                intent.putExtra("TITLE", "" + strtitle);
                intent.putExtra("FORMID", "" + FormID);
                startActivityForResult(intent,5);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnSyncAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncMultipleData();
            }
        });
        // getFormData();
        getLocalStoredData();

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(requestCode==5){
                getLocalStoredData();
            }
        }
    }

    public void getFormData() {
        APICommunicator.showProgress(this, "Please wait..");
        APICommunicator.getFormGetData(this,preferences.getUserID(), FormID, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                //Logger.printLog(ManageFormActivity.this, TAG + " - getformdatalist", response.toString());
                try {
                    APICommunicator.stopProgress();
                    JSONObject jsonResponse = new JSONObject((String) response);
                    try {
                        // ArrayList<ArrayList<GetDataModel>> mainDataList = new ArrayList<ArrayList<GetDataModel>>();
                        if (jsonResponse.getString("msg").equalsIgnoreCase("Success")) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataOb = dataArray.getJSONObject(i);
                                Iterator<String> keys = dataOb.keys();
                                ArrayList<GetDataModel> dataList = new ArrayList<GetDataModel>();
                                while (keys.hasNext()) {
                                    GetDataModel gd = new GetDataModel();
                                    String keystr = (String) keys.next();
                                    gd.setKey(keystr);
                                    gd.setStatus("SENT");
                                    gd.setValue(dataOb.getString(keystr));
                                    dataList.add(gd);
                                }
                                DATA_SHOW_LIST.add(dataList);
                            }
                        }
                        updateImageData();
                        adapter = new FormDataListAdapter(ManageFormActivity.this, recyclerView, DATA_SHOW_LIST);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        StringWriter stackTrace = new StringWriter();
                        e.printStackTrace(new PrintWriter(stackTrace));
                        //Logger.printLog(ManageFormActivity.this, TAG, stackTrace.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                APICommunicator.stopProgress();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    ToastMsg.showShort(ManageFormActivity.this, "Please check your internet connection!");
                } else {
                    ToastMsg.showShort(ManageFormActivity.this, "Something is wrong Please try again!");
                }
            }
        });
    }

    public void updateImageData() {
        for (int i = 0; i < DATA_SHOW_LIST.size(); i++) {
            for (int j = 0; j < DATA_SHOW_LIST.get(i).size(); j++) {
                GetDataModel dataModel = DATA_SHOW_LIST.get(i).get(j);
                if (dataModel.getKey().equals("id")) {
                    String ImageId = dataModel.getValue();
                    getImageData(ImageId, i, j);
                }
            }
        }
    }

    public void getImageData(String imageid, final int index, final int position) {
        APICommunicator.showProgress(ManageFormActivity.this, "Please wait..");
        APICommunicator.getImageData(ManageFormActivity.this, imageid, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                //Logger.printLog(ManageFormActivity.this, TAG + " - getImageData", response.toString());
                try {
                    APICommunicator.stopProgress();
                        JSONObject jsonResponse = new JSONObject((String) response);
                    try {
                        // ArrayList<ArrayList<GetDataModel>> mainDataList = new ArrayList<ArrayList<GetDataModel>>();
                        if (jsonResponse.getString("msg").equalsIgnoreCase("Success")) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            //JSONObject dataOb = dataArray.getJSONObject(0);
                            ArrayList<GetDataModel> saveData=DATA_SHOW_LIST.get(index);
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataOb = dataArray.getJSONObject(i);
                                GetDataModel gdm=new GetDataModel();
                                gdm.setKey("IMAGE" + (i + 1));
                                gdm.setValue(dataOb.getString("image"));
                                saveData.add(gdm);
                            }
                            DATA_SHOW_LIST.set(index,saveData);//get(index).get(position).setKey("IMAGE" + (i + 1));
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        StringWriter stackTrace = new StringWriter();
                        e.printStackTrace(new PrintWriter(stackTrace));
                        //Logger.printLog(ManageFormActivity.this, TAG, stackTrace.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                APICommunicator.stopProgress();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    ToastMsg.showShort(ManageFormActivity.this, "Please check your internet connection!");
                } else {
                    ToastMsg.showShort(ManageFormActivity.this, "Something is wrong Please try again!");
                }
            }
        });
    }

    public ArrayList<ArrayList<GetDataModel>> getDBSurveyCall() {
        RealmResults<SaveDataModel> surveyDataList = RealmController.with(this).getSurveyDataList(FormID);
        ArrayList<ArrayList<GetDataModel>> mainDataList = new ArrayList<ArrayList<GetDataModel>>();
        for (int i = 0; i < surveyDataList.size(); i++) {
            SaveDataModel saveData = surveyDataList.get(i);
            try {
                JSONObject dataOb = new JSONObject(saveData.getJsonData());
                Iterator<String> keys = dataOb.keys();
                ArrayList<GetDataModel> dataList = new ArrayList<GetDataModel>();
                while (keys.hasNext()) {
                    GetDataModel gd = new GetDataModel();
                    String keystr = (String) keys.next();
                    gd.setKey(keystr);
                    gd.setValue(dataOb.getString(keystr));
                    // gd.setType(keystr.substring(0,keystr.lastIndexOf("_")));
                    gd.setStatus("DRAFT");
                    gd.setId(saveData.getId());
                    dataList.add(gd);
                }
                mainDataList.add(dataList);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return mainDataList;
    }

    /**

     * */
    public ArrayList<ArrayList<GetDataModel>> getDisplayDBSurveyCall() {
        RealmResults<SaveDataModel> surveyDataList = RealmController.with(this).getSurveyDataList(FormID);
        ArrayList<ArrayList<GetDataModel>> mainDataList = new ArrayList<ArrayList<GetDataModel>>();
        for (int i = 0; i < surveyDataList.size(); i++) {
            SaveDataModel saveData = surveyDataList.get(i);
            try {
                JSONObject dataOb = new JSONObject(saveData.getDisplayData());
                Iterator<String> keys = dataOb.keys();
                ArrayList<GetDataModel> dataList = new ArrayList<GetDataModel>();
                while (keys.hasNext()) {
                    GetDataModel gd = new GetDataModel();
                    String keystr = (String) keys.next();
                    gd.setKey(keystr);
                    gd.setValue(dataOb.getString(keystr));

                    // gd.setType(keystr.substring(0,keystr.lastIndexOf("_")));
                    gd.setStatus("DRAFT");
                    dataList.add(gd);
                }
                mainDataList.add(dataList);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return mainDataList;
    }

    public void getLocalStoredData() {
        DATA_SHOW_LIST = new ArrayList<>();
        DATA_SHOW_LIST.addAll(getDisplayDBSurveyCall());
        adapter = new FormDataListAdapter(ManageFormActivity.this, recyclerView, DATA_SHOW_LIST);
        recyclerView.setAdapter(adapter);

        getFormData();
    }

    public void syncSingleData(int index) {
        ArrayList<ArrayList<GetDataModel>> dataList = getDBSurveyCall();
        Map<String, String> mapData = new HashMap<String, String>();
        for (int i = 0; i < dataList.get(index).size(); i++) {
            mapData.put(dataList.get(index).get(i).getKey(), dataList.get(index).get(i).getValue());
        }

        //  Logger.printLog(ManageFormActivity.this, TAG + " - submitdata", FORM_ID);
        APICommunicator.showProgress(this, "Please wait..");
        APICommunicator.saveSingleData(this, mapData, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                //Logger.printLog(ManageFormActivity.this, TAG + " - savedata", response.toString());
                try {
                    APICommunicator.stopProgress();
                    JSONObject jsonResponse = new JSONObject((String) response);
                    if (jsonResponse.getString("msg").equalsIgnoreCase("Success")) {
                        ToastMsg.showShort(ManageFormActivity.this, "Data saved successfully!");
                        finish();
                    } else {
                        ToastMsg.showShort(ManageFormActivity.this, "Data saving failure!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastMsg.showShort(ManageFormActivity.this, "Data saving failure!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                APICommunicator.stopProgress();
                //Logger.printLog(ManageFormActivity.this, TAG, "login Error");
            }
        });
    }

    public void syncMultipleData() {
        ArrayList<ArrayList<GetDataModel>> dataList = getDBSurveyCall();

        final TreeSet<Long> ID_LIST = new TreeSet<>();
        try {
            jsonArray = new JSONArray();
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                for (int j = 0; j < dataList.get(i).size(); j++) {
                    jsonObject.put(dataList.get(i).get(j).getKey(), dataList.get(i).get(j).getValue());
                    ID_LIST.add(dataList.get(i).get(j).getId());
                }
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        APICommunicator.showProgress(this, "Please wait..");
        APICommunicator.saveMultipleData(this, jsonArray, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                //Logger.printLog(ManageFormActivity.this, TAG + " - savedata", response.toString());
                try {
                    APICommunicator.stopProgress();
                    JSONArray jsArray= (JSONArray) response;
                    JSONObject jsonResponse = jsArray.getJSONObject(0);
                    if (jsonResponse.getString("msg").equalsIgnoreCase("Success")) {
                        ToastMsg.showShort(ManageFormActivity.this, "Data saved successfully!");
                        Iterator<Long> iterator = ID_LIST.iterator();
                        while (iterator.hasNext()) {
                            RealmController.with(ManageFormActivity.this).deleteData(iterator.next());
                        }
                        finish();
                    } else {
                        ToastMsg.showShort(ManageFormActivity.this, "Data saving failure!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastMsg.showShort(ManageFormActivity.this, "Data saving failure!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                APICommunicator.stopProgress();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    ToastMsg.showShort(ManageFormActivity.this, "Please check your internet connection!");
                } else {
                    ToastMsg.showShort(ManageFormActivity.this, "Something is wrong Please try again!");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
