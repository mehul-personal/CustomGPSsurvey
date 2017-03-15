package com.analytics.customgpssurvey;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.analytics.customgpssurvey.model.DisplayDataModel;
import com.analytics.customgpssurvey.model.FormDetailData;
import com.analytics.customgpssurvey.model.FormDetailModel;
import com.analytics.customgpssurvey.model.SaveDataModel;
import com.analytics.customgpssurvey.realm.RealmController;
import com.analytics.customgpssurvey.utils.AccessPermissions;
import com.analytics.customgpssurvey.utils.CommonMethods;
import com.analytics.customgpssurvey.utils.Logger;
import com.analytics.customgpssurvey.utils.MyPreferences;
import com.analytics.customgpssurvey.utils.ToastMsg;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class AddFormDataActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 9119;
    public static String TAG = "AddFormDataActivity";
    public static boolean isExternalStorageWritePermissionGranted, isCameraPermissionGranted;
    public static boolean isGPSPermissionGranted;
    private static Location currentLocation = null;
    public String LAST_SAVED_TAG = "", FORM_ID = "";
    ListView formListview;
    Typeface LibreBoldFont, RobotRegularFont;
    FormDetailModel detailModel;
    LinearLayout llViewContainer;
    TextView submitData, pickLocation, txvExit,txvTakeGPSlatlng;
    TextView edtLatitude, edtLongitude;
    LocationRequest mLocationRequest;
    // private OnLocationUpdate locationUpdateer;
    GoogleApiClient mGoogleApiClient;
    double currentlongitude, currentlatitude;
    MyPreferences pref;
    String imageJSONob = "",LASTDATETAG="";
    Map<String, String> imageData;
    private Uri mFileUri;
    private LocationManager locationManager;
    private Realm realm;

    public static void createDirectory(String filePath) {
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getOtherDevicePath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_form_data);

        LibreBoldFont = Typeface.createFromAsset(getAssets(),
                "LibreBaskerville-Bold.otf");
        RobotRegularFont = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        pref = new MyPreferences(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));

        llViewContainer = (LinearLayout) findViewById(R.id.llViewContainer);
        submitData = (TextView) findViewById(R.id.txvSignIn);
        txvExit = (TextView) findViewById(R.id.txvExit);
        pickLocation = (TextView) findViewById(R.id.txvPickLocation);
        txvTakeGPSlatlng=(TextView) findViewById(R.id.txvTakeGPSlatlng);
        edtLatitude = (TextView) findViewById(R.id.latitude);
        edtLongitude = (TextView) findViewById(R.id.longitude);

        imageData = new HashMap<>();
        //get realm instance
        this.realm = RealmController.with(this).getRealm();

        FORM_ID = getIntent().getStringExtra("FORMID");
        getFormDetailList(getIntent().getStringExtra("FORMID"));

//        edtLatitude.setText(pref.getCurrentLatitude());
//        edtLongitude.setText(pref.getCurrentLongitude());
        llViewContainer.findViewWithTag("");
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setSubmitData();
                saveSingleRecord();
            }
        });
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddFormDataActivity.this, PickLocationActivity.class);
                startActivityForResult(i, 10);
            }
        });
        txvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("msg", "success");
                setResult(5, i);
                finish();
            }
        });
        txvTakeGPSlatlng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtLatitude.setText(pref.getCurrentLatitude());
                edtLongitude.setText(pref.getCurrentLongitude());
                ToastMsg.showShort(AddFormDataActivity.this, "Your location updated successfully!");
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

    public void getFormDetailList(String formid) {
        Logger.printLog(AddFormDataActivity.this, TAG + " - getformdetaillist", formid);
        APICommunicator.showProgress(this, "Please wait..");
        APICommunicator.getFormDetailList(this, formid, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Logger.printLog(AddFormDataActivity.this, TAG + " - getformdetaillist", response.toString());
                APICommunicator.stopProgress();
                if (pref.getOfflineFormCreateData(FORM_ID).isEmpty() ||
                        pref.getOfflineFormCreateData(FORM_ID).equals((String) response)) {
                    pref.setOfflineFormCreateData((String) response, FORM_ID);
                    getFormData((String) response);
                } else if (!pref.getOfflineFormCreateData(FORM_ID).equals((String) response) &&
                        RealmController.with(AddFormDataActivity.this).getSurveyDataList(FORM_ID).size() > 0) {
                    ToastMsg.showShort(AddFormDataActivity.this, "Your form updated! Please first sync all data!");
                } else {
                    pref.setOfflineFormCreateData((String) response, FORM_ID);
                    getFormData((String) response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                APICommunicator.stopProgress();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    getFormData(pref.getOfflineFormCreateData(FORM_ID));
                } else {
                    ToastMsg.showShort(AddFormDataActivity.this, "Something is wrong Please try again!");
                }
            }
        });
    }

    public void getFormData(String response) {
        try {

            JSONObject jsonResponse = new JSONObject(response);
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type listType = new TypeToken<FormDetailModel>() {
                }.getType();
                detailModel = gson.fromJson(jsonResponse.toString(), listType);
                setFormDetail(detailModel.getData());
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                Logger.printLog(AddFormDataActivity.this, TAG, stackTrace.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSubmitData() {

        Map<String, String> requestJSON = new HashMap<String, String>();
        List<FormDetailData> dataList = detailModel.getData();
        for (int i = 0; i < dataList.size(); i++) {
            FormDetailData data = dataList.get(i);
            if (data.getType().equalsIgnoreCase("text")) {
                EditText edit1 = (EditText) llViewContainer.findViewWithTag(data.getInput_id());
                requestJSON.put(data.getInput_id(), edit1.getText().toString());
            } else if (data.getType().equalsIgnoreCase("textarea")) {
                EditText edit1 = (EditText) llViewContainer.findViewWithTag(data.getInput_id());
                requestJSON.put(data.getInput_id(), edit1.getText().toString());
            } else if (data.getType().equalsIgnoreCase("select")) {
                Spinner spinnerData = (Spinner) llViewContainer.findViewWithTag(data.getInput_id());
                requestJSON.put(data.getInput_id(), spinnerData.getSelectedItem().toString());
            } else if (data.getType().equalsIgnoreCase("radio")) {
                RadioButton radioButton = (RadioButton) llViewContainer.findViewWithTag(data.getGroup_label_id());
                if (radioButton.isChecked())
                    requestJSON.put(data.getInput_id(), radioButton.getText().toString());
            } else if (data.getType().equalsIgnoreCase("checkbox")) {
                CheckBox checkBox = (CheckBox) llViewContainer.findViewWithTag(data.getGroup_label_id());
                if (checkBox.isChecked())
                    requestJSON.put(data.getGroup_label_id(), checkBox.getText().toString());
            }
        }
        requestJSON.put("user_id", pref.getUserID());
        requestJSON.put("form_id", FORM_ID);
        requestJSON.put("lat", edtLatitude.getText().toString());
        requestJSON.put("long", edtLongitude.getText().toString());
        requestJSON.putAll(imageData);

        Logger.printLog(AddFormDataActivity.this, TAG + " - submitdata", FORM_ID);
        APICommunicator.showProgress(this, "Please wait..");
        APICommunicator.saveSingleData(this, requestJSON, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Logger.printLog(AddFormDataActivity.this, TAG + " - savedata", response.toString());
                try {
                    APICommunicator.stopProgress();
                    JSONObject jsonResponse = new JSONObject((String) response);
                    if (jsonResponse.getString("msg").equalsIgnoreCase("Success")) {
                        ToastMsg.showShort(AddFormDataActivity.this, "Data saved successfully!");
                        finish();
                    } else {
                        ToastMsg.showShort(AddFormDataActivity.this, "Data saving failure!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastMsg.showShort(AddFormDataActivity.this, "Data saving failure!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                APICommunicator.stopProgress();
                Logger.printLog(AddFormDataActivity.this, TAG, "login Error");
            }
        });
    }

    public void saveSingleRecord() {
        JSONObject requestJsonOb = new JSONObject();
        try {
            List<FormDetailData> dataList = detailModel.getData();
            for (int i = 0; i < dataList.size(); i++) {
                FormDetailData data = dataList.get(i);

                if (data.getType().equalsIgnoreCase("text")) {
                    EditText edit1 = (EditText) llViewContainer.findViewWithTag(data.getInput_id());
                    requestJsonOb.put(data.getInput_id(), edit1.getText().toString());
                } else if (data.getType().equalsIgnoreCase("textarea")) {
                    EditText edit1 = (EditText) llViewContainer.findViewWithTag(data.getInput_id());
                    requestJsonOb.put(data.getInput_id(), edit1.getText().toString());
                } else if (data.getType().equalsIgnoreCase("select")) {
                    Spinner spinnerData = (Spinner) llViewContainer.findViewWithTag(data.getInput_id());
                    requestJsonOb.put(data.getInput_id(), spinnerData.getSelectedItem().toString());
                } else if (data.getType().equalsIgnoreCase("radio")) {
                    RadioButton radioButton = (RadioButton) llViewContainer.findViewWithTag(data.getGroup_label_id());
                    if (radioButton.isChecked())
                        requestJsonOb.put(data.getInput_id(), radioButton.getText().toString());
                } else if (data.getType().equalsIgnoreCase("checkbox")) {
                    CheckBox checkBox = (CheckBox) llViewContainer.findViewWithTag(data.getGroup_label_id());
                    if (checkBox.isChecked()) {
                        if (!requestJsonOb.has(data.getInput_id())) {
                            requestJsonOb.put(data.getInput_id(), checkBox.getText().toString());
                        } else {
                            requestJsonOb.put(data.getInput_id(), requestJsonOb.getString(data.getInput_id()) + "," + checkBox.getText().toString());
                        }
                    }
                } else if (data.getType().equalsIgnoreCase("file")) {
                    requestJsonOb.put(data.getInput_id(), imageData.get(data.getInput_id()));
                } else if (data.getType().equalsIgnoreCase("date")) {
                    EditText edit1 = (EditText) llViewContainer.findViewWithTag(data.getInput_id());
                    requestJsonOb.put(data.getInput_id(), edit1.getText().toString());
                }
            }
            requestJsonOb.put("user_id", pref.getUserID());
            requestJsonOb.put("form_id", FORM_ID);
            requestJsonOb.put("lat", edtLatitude.getText().toString());
            requestJsonOb.put("long", edtLongitude.getText().toString());

            SaveDataModel saveData = new SaveDataModel();
            saveData.setJsonData(requestJsonOb.toString());
            saveData.setForm_id(FORM_ID);
            saveData.setDisplayData(setDisplayListData(requestJsonOb).toString());
            saveData.setRecordStatus("DRAFT");
            saveData.setId(RealmController.with(this).getMaxId());

            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealm(saveData);
            realm.commitTransaction();

            ToastMsg.showShort(AddFormDataActivity.this, "Data saved Successfully");
            edtLatitude.setText("");
            edtLongitude.setText("");
            imageData = new HashMap<>();
            llViewContainer.removeAllViews();
            getFormDetailList(FORM_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.printLog(AddFormDataActivity.this, TAG + " - submitdata", FORM_ID);

    }

    public JSONObject setDisplayListData(JSONObject requestJsonOb) {
        //For getting label
        JSONObject DISPLAY_DATA = new JSONObject();
        List<DisplayDataModel> DISPLAY_LIST_DATA = new ArrayList<>();
        try {
            List<FormDetailData> formDetailData = detailModel.getData();
            int image = 1;
            for (int i = 0; i < formDetailData.size(); i++) {
                DisplayDataModel displayModel = new DisplayDataModel();
                FormDetailData form = formDetailData.get(i);
                if (form.getType().equalsIgnoreCase("text")) {
                    displayModel.setKey(form.getLabel());

                    Iterator<String> keys = requestJsonOb.keys();
                    while (keys.hasNext()) {
                        String keystr = (String) keys.next();
                        if (keystr.equalsIgnoreCase(form.getInput_id())) {
                            displayModel.setValue(requestJsonOb.getString(keystr));
                        }
                    }
                    DISPLAY_LIST_DATA.add(displayModel);
                } else if (form.getType().equalsIgnoreCase("date")) {
                    displayModel.setKey(form.getLabel());

                    Iterator<String> keys = requestJsonOb.keys();
                    while (keys.hasNext()) {
                        String keystr = (String) keys.next();
                        if (keystr.equalsIgnoreCase(form.getInput_id())) {
                            displayModel.setValue(requestJsonOb.getString(keystr));
                        }
                    }
                    DISPLAY_LIST_DATA.add(displayModel);
                } else if (form.getType().equalsIgnoreCase("radio")) {
                    Iterator<String> keys = requestJsonOb.keys();
                    while (keys.hasNext()) {
                        String keystr = (String) keys.next();
                        if (keystr.equalsIgnoreCase(form.getInput_id())) {
                            displayModel.setKey(form.getGroup_label());
                            displayModel.setValue(requestJsonOb.getString(keystr));
                            DISPLAY_LIST_DATA.add(displayModel);
                        }
                    }
                } else if (form.getType().equalsIgnoreCase("select")) {
                    displayModel.setKey(form.getLabel());

                    Iterator<String> keys = requestJsonOb.keys();
                    while (keys.hasNext()) {
                        String keystr = (String) keys.next();
                        if (keystr.equalsIgnoreCase(form.getInput_id())) {
                            displayModel.setValue(requestJsonOb.getString(keystr));
                        }
                    }
                    DISPLAY_LIST_DATA.add(displayModel);
                } else if (form.getType().equalsIgnoreCase("textarea")) {
                    displayModel.setKey(form.getLabel());

                    Iterator<String> keys = requestJsonOb.keys();
                    while (keys.hasNext()) {
                        String keystr = (String) keys.next();
                        if (keystr.equalsIgnoreCase(form.getInput_id())) {
                            displayModel.setValue(requestJsonOb.getString(keystr));
                        }
                    }
                    DISPLAY_LIST_DATA.add(displayModel);
                } else if (form.getType().equalsIgnoreCase("checkbox")) {


                    Iterator<String> keys = requestJsonOb.keys();
                    while (keys.hasNext()) {
                        String keystr = (String) keys.next();
                        if (keystr.equalsIgnoreCase(form.getGroup_label_id())) {
                            displayModel.setKey(form.getGroup_label());
                            displayModel.setValue(requestJsonOb.getString(keystr));
                            DISPLAY_LIST_DATA.add(displayModel);
                        }
                    }


                } else if (form.getType().equalsIgnoreCase("file")) {

                    Iterator<String> keys = requestJsonOb.keys();
                    while (keys.hasNext()) {
                        String keystr = (String) keys.next();
                        if (keystr.equalsIgnoreCase(form.getInput_id())) {
                            displayModel.setKey("IMAGE" + image);
                            displayModel.setValue(requestJsonOb.getString(keystr));
                            DISPLAY_LIST_DATA.add(displayModel);
                            image++;
                        }
                    }
                }

            }

            for (int j = 0; j < DISPLAY_LIST_DATA.size(); j++) {
                DisplayDataModel ddm = DISPLAY_LIST_DATA.get(j);
                if (j == 0) {
                    DISPLAY_DATA.put(ddm.getKey(), ddm.getValue());
                } else {
                    if (DISPLAY_DATA.toString().contains(ddm.getKey())) {
                        if (!DISPLAY_DATA.getString(ddm.getKey()).contains(ddm.getValue()))
                            DISPLAY_DATA.put(ddm.getKey(), DISPLAY_DATA.getString(ddm.getKey()) + "," + ddm.getValue());
                    } else {
                        DISPLAY_DATA.put(ddm.getKey(), ddm.getValue());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return DISPLAY_DATA;
    }

    public void setFormDetail(List<FormDetailData> formDetailData) {
        for (int i = 0; i < formDetailData.size(); i++) {
            FormDetailData form = formDetailData.get(i);
            if (form.getType().equalsIgnoreCase("header")) {
                addHeader(form.getLabel());
            }else if (form.getType().equalsIgnoreCase("paragraph")) {
                addTextView(form.getLabel());
            } else if (form.getType().equalsIgnoreCase("text")) {
                addTextView(form.getLabel());
                addEditTextView("", "", form.getInput_id());
            } else if (form.getType().equalsIgnoreCase("radio")) {
                boolean flag = false;
                for (int k = 0; k < i; k++) {
                    if (formDetailData.get(k).getInput_id().equalsIgnoreCase(form.getInput_id())) {
                        flag = true;
                    }
                }
                if (!flag) {
                    addTextView(form.getGroup_label());
                    ArrayList<String> label = new ArrayList<>();
                    ArrayList<String> tag = new ArrayList<>();
                    for (int j = 0; j < formDetailData.size(); j++) {
                        FormDetailData subform = formDetailData.get(j);
                        if (subform.getType().equalsIgnoreCase("radio") && subform.getInput_id().equalsIgnoreCase(form.getInput_id())) {
                            label.add(subform.getLabel());
                            tag.add(subform.getGroup_label_id());
                        }
                    }
                    addRadioButton(label, tag);
                }
            } else if (form.getType().equalsIgnoreCase("select")) {
                addTextView(form.getLabel());
                List<FormDetailData.FormDetailOption> optionList = form.getOptionsList();
                ArrayList<String> dataList = new ArrayList<>();
                for (int j = 0; j < optionList.size(); j++) {
                    dataList.add(optionList.get(j).getValue());
                }
                addSpinner(dataList, form.getInput_id());
            } else if (form.getType().equalsIgnoreCase("textarea")) {
                addTextView(form.getLabel());
                addTextAreaEditTextView("", "", form.getInput_id());
            } else if (form.getType().equalsIgnoreCase("checkbox")) {
                boolean flag = false;
                for (int k = 0; k < i; k++) {
                    if (formDetailData.get(k).getInput_id().equalsIgnoreCase(form.getInput_id())) {
                        flag = true;
                    }
                }
                if (!flag) {
                    addTextView(form.getGroup_label());
                    ArrayList<String> label = new ArrayList<>();
                    ArrayList<String> tag = new ArrayList<>();
                    for (int j = 0; j < formDetailData.size(); j++) {
                        FormDetailData subform = formDetailData.get(j);
                        if (subform.getType().equalsIgnoreCase("checkbox") && subform.getInput_id().equalsIgnoreCase(form.getInput_id())) {
                            label.add(subform.getLabel());
                            tag.add(subform.getGroup_label_id());
                        }
                    }
                    addCheckBox(label, tag);
                }
            } else if (form.getType().equalsIgnoreCase("file")) {
                addImage(form.getLabel(), form.getInput_id());
            } else if (form.getType().equalsIgnoreCase("date")) {
                addTextView(form.getLabel());
                addEditTextView("", "", form.getInput_id());
            }

        }
    }

    public void addTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        // textView.setPadding(0,20,0,0);
        textView.setTextAppearance(this, R.style.TextStyle);
        llViewContainer.addView(textView);
    }
    public void addHeader(String text) {
        TextView textView = new TextView(this);
        textView.setText(text); //textView.setTypeface(Typeface.DEFAULT_BOLD);
        // textView.setPadding(0,20,0,0);
        textView.setTextAppearance(this, R.style.HeaderStyle);
        llViewContainer.addView(textView);
    }

    public void addEditTextView(String text, String hint, String tag) {
        EditText editText = new EditText(this);
        editText.setText(text);
        editText.setHint(hint);
        editText.setTag(tag);
        editText.setBackgroundColor(Color.parseColor("#1c2127"));
        if (tag.contains("date")) {
            LASTDATETAG=tag;
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_calender, 0);
            editText.setCompoundDrawablePadding(5);
            editText.setPadding(0,0,5,0);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDateTime();
                }
            });

        }
        editText.setTextAppearance(this, R.style.EditStyle);
        llViewContainer.addView(editText);
        addDivider();
    }

    public void addTextAreaEditTextView(String text, String hint, String tag) {
        EditText editText = new EditText(this);
        editText.setText(text);
        editText.setHint(hint);
        editText.setTag(tag);
        editText.setBackgroundColor(Color.parseColor("#1c2127"));
        editText.setTextAppearance(this, R.style.TextAreaStyle);
        llViewContainer.addView(editText);
        addDivider();
    }

    public void addRadioButton(ArrayList<String> label, ArrayList<String> id) {
        final RadioButton[] rb = new RadioButton[label.size()];
        RadioGroup rg = new RadioGroup(this);
        rg.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (label.size() < 3)
            rg.setOrientation(LinearLayout.HORIZONTAL);
        else
            rg.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < label.size(); i++) {
            rb[i] = new RadioButton(this);
            rb[i].setText(label.get(i));
            rb[i].setTextAppearance(this, R.style.RadioButtonStyle);
            rb[i].setTag(id.get(i));
            rb[i].setId(i);
            rg.addView(rb[i]);
        }
        llViewContainer.addView(rg);
        addDivider();
    }

    public void addCheckBox(ArrayList<String> label, ArrayList<String> tag) {
        for (int i = 0; i < label.size(); i++) {
            CheckBox cb = new CheckBox(this);
            cb.setText(label.get(i));
            cb.setTag(tag.get(i));
            cb.setTextAppearance(this, R.style.CheckboxStyle);
            llViewContainer.addView(cb);
        }
        addDivider();
    }

    public void addSpinner(ArrayList<String> spinnerDataItem, String tag) {
        LinearLayout linear = new LinearLayout(this);
        linear.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linear.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinner.getBackground().setColorFilter(Color.parseColor("#ffc107"), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, spinnerDataItem);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setTag(tag);
        linear.addView(spinner);
        llViewContainer.addView(linear);
        addDivider();
    }

    public void addImage(String label, String tag) {
        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        // params.setMargins(0,20,0,0);
        linear.setLayoutParams(params);

        TextView textView = new TextView(this);
        textView.setText(label);
        textView.setTextAppearance(this, R.style.TextWeightStyle);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_photo_placeholder);
        imageView.setTag(tag);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(v.getTag());
            }
        });

        linear.addView(textView);
        linear.addView(imageView);
        llViewContainer.addView(linear);
        addDivider();
    }

    public void addDivider() {
        View view = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(0, 20, 0, 20);
        view.setLayoutParams(params);
        view.setBackgroundColor(getResources().getColor(R.color.hintColor));
        llViewContainer.addView(view);
    }

    public void chooseImage(final Object getTag) {
        final CharSequence[] options = {"Take Photo on Camera",
                "Choose Photo on Gallery", "Cancel"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                AddFormDataActivity.this);
        builder.setTitle("Add Photo Using Camera & Gallery!");
        builder.setItems(options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo on Camera")) {
                            LAST_SAVED_TAG = (String) getTag;
                            takePhotoOnCamera();
                        } else if (options[item].equals("Choose Photo on Gallery")) {
                            LAST_SAVED_TAG = (String) getTag;
                            takePhotoOnGallery();
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
        builder.show();
    }

    public void takePhotoOnCamera() {
        if (AccessPermissions.checkOrRequestCameraPermission(this, null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastMsg.showShort(AddFormDataActivity.this, getString(R.string.str_camera_permission));
            }
        })) {
            if (AccessPermissions.checkOrRequestWritePermission(this, null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ToastMsg.showShort(AddFormDataActivity.this, getString(R.string.str_storage_permission));
                }
            })) {
                startImageCamera();
                isExternalStorageWritePermissionGranted = AccessPermissions.isWritePermissionsGranted(this);
            }
            isExternalStorageWritePermissionGranted = AccessPermissions.isWritePermissionsGranted(this);
            isCameraPermissionGranted = AccessPermissions.isCameraPermissionsGranted(this);
        }
        isCameraPermissionGranted = AccessPermissions.isCameraPermissionsGranted(this);
    }

    public void takePhotoOnGallery() {
        if (AccessPermissions.checkOrRequestWritePermission(this, null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastMsg.showShort(AddFormDataActivity.this, getString(R.string.str_storage_permission));
            }
        })) {
            startImageGallery();
            isExternalStorageWritePermissionGranted = AccessPermissions.isWritePermissionsGranted(this);
        }
        isExternalStorageWritePermissionGranted = AccessPermissions.isWritePermissionsGranted(this);
    }

    private void startImageCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getOutputMediaFile(1);
        if (mFileUri != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent1, REQUEST_IMAGE_CAMERA);
        } else {
            Logger.printLog(AddFormDataActivity.this, TAG, "file not available");
        }
    }

    private void startImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
    }

    public Uri getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "CustomGPSSurvey");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Logger.printLog(AddFormDataActivity.this, TAG, "could not create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            String imageStoragePath = mediaStorageDir + "/Image/";
            createDirectory(imageStoragePath);
            mediaFile = new File(imageStoragePath + "IMG" + timeStamp + ".jpg");
        } else if (type == 2) {
            String audioStoragePath = mediaStorageDir + "/Audio/";
            createDirectory(audioStoragePath);
            mediaFile = new File(audioStoragePath + "AUD" + timeStamp + ".3gp");
        } else if (type == 3) {
            String videoStoragePath = mediaStorageDir + "/Video/";
            createDirectory(videoStoragePath);
            mediaFile = new File(videoStoragePath + "VID" + timeStamp + ".3gp");
        } else if (type == 4) {
            String videoStoragePath = mediaStorageDir + "/Video_Thumb/";
            createDirectory(videoStoragePath);
            mediaFile = new File(videoStoragePath + "VID_TMB" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAMERA || requestCode == REQUEST_IMAGE_GALLERY) {

        } else if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!CommonMethods.isGPSEnabled(this)) {
                    showGPSAlert();
                } else {
                    mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                    createLocationRequest();
                }
            }
        } else {
            for (int i = 0; i < permissions.length; i++) {
                switch (permissions[i]) {
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isExternalStorageWritePermissionGranted = true;
                            startImageGallery();
                        } else {
                            isExternalStorageWritePermissionGranted = false;
                        }
                        break;
                    case Manifest.permission.CAMERA:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isCameraPermissionGranted = true;
                            startImageCamera();
                        } else {
                            isCameraPermissionGranted = false;
                        }
                        break;

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAMERA) {
                String selectedImage = "";
                if (mFileUri != null) {
                    Logger.printLog(AddFormDataActivity.this, TAG, "file: " + mFileUri);
                    selectedImage = getRealPathFromURI(mFileUri);
                } else {
                    if (data != null) {
                        try {
                            selectedImage = getPath(data.getData(), true);
                        } catch (Exception e) {
                            selectedImage = getRealPathFromURI(data.getData());
                        }
                    }
                }
                ImageView imageView = (ImageView) llViewContainer.findViewWithTag(LAST_SAVED_TAG);
                try {
                    Picasso.with(AddFormDataActivity.this)
                            .load(new File(selectedImage)).skipMemoryCache()
                            .into(imageView);
                    Bitmap bitmap = BitmapFactory.decodeFile(compressImage(selectedImage));

                    imageJSONob = CommonMethods.BitMapToString(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastMsg.showShort(AddFormDataActivity.this, "Image size is bigger!!");
                }
                imageData.put(LAST_SAVED_TAG, imageJSONob);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {

                if (data != null && data.getData() != null) {
                    String selectedImage = "";
                    try {
                        selectedImage = getPath(data.getData(), true);
                        if (selectedImage == null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                selectedImage = getOtherDevicePath(this, data.getData());
                            }
                        }
                    } catch (Exception e) {
                        selectedImage = getRealPathFromURI(data.getData());
                    }
                    ImageView imageView = (ImageView) llViewContainer.findViewWithTag(LAST_SAVED_TAG);
                    Picasso.with(AddFormDataActivity.this)
                            .load(new File(selectedImage)).resize(0, imageView.getHeight())
                            .into(imageView);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImage);
                    try {
                        imageJSONob = CommonMethods.BitMapToString(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageData.put(LAST_SAVED_TAG, imageJSONob);
                }
            }
        }
        if (data != null) {
            if (requestCode == 10) {
                edtLatitude.setText(data.getStringExtra("picked_latitude"));
                edtLongitude.setText(data.getStringExtra("picked_longitude"));
                ToastMsg.showShort(this, "Your picked location set successfully");
            }
        }
    }

    public String compressImage(String filePath) {

        //  String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getPath(Uri uri, boolean isImage) {
        if (uri == null) {
            return null;
        }
        String[] projection;
        String coloumnName, selection;
        if (isImage) {
            selection = MediaStore.Images.Media._ID + "=?";
            coloumnName = MediaStore.Images.Media.DATA;
        } else {
            selection = MediaStore.Video.Media._ID + "=?";
            coloumnName = MediaStore.Video.Media.DATA;
        }
        projection = new String[]{coloumnName};
        Cursor cursor;
        if (Build.VERSION.SDK_INT > 19) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to
            if (isImage) {
                cursor = getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                                new String[]{id}, null);
            } else {
                cursor = getContentResolver()
                        .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, new String[]{id},
                                null);
            }
        } else {
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try {
            int column_index = cursor.getColumnIndex(coloumnName);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mCurrentLocation;

        Log.i(TAG, "OnConnected");
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                // Note that this can be NULL if last location isn't already known.
                if (mCurrentLocation != null) {
                    // Print current location if not null
                    Log.e("DEBUG", "current location: " + mCurrentLocation.toString());
                    currentlatitude = mCurrentLocation.getLatitude();
                    currentlongitude = mCurrentLocation.getLongitude();
                } else {
                    startLocationUpdates();
                }


            }
        } else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            // Note that this can be NULL if last location isn't already known.
            if (mCurrentLocation != null) {
                // Print current location if not null
                Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
                currentlatitude = mCurrentLocation.getLatitude();
                currentlongitude = mCurrentLocation.getLongitude();
            }
            // Begin polling for new location updates.
            startLocationUpdates();
        }
        if (currentlatitude > 0.0) {
            pref.setCurrentLatitude(String.valueOf(currentlatitude));
            pref.setCurrentLongitude(String.valueOf(currentlongitude));

//            if (locationUpdateer != null) {
//                locationUpdateer.locationUpdate(currentLocation);
//            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            //Toast.makeText(getApplicationContext(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            //Toast.makeText(getApplicationContext(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(AddFormDataActivity.this, connectionResult.RESOLUTION_REQUIRED);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Location services connection failed with code==>" + connectionResult.getErrorCode());
            Log.e(TAG, "Location services connection failed Because of==> " + connectionResult.getErrorMessage());
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        Logger.printLog(AddFormDataActivity.this, TAG, "Current Location==>" + location);
        if (location != null) {
            currentLocation = location;
            pref.setCurrentLatitude(String.valueOf(currentlatitude));
            pref.setCurrentLongitude(String.valueOf(currentlongitude));
//            if (locationUpdateer != null) {
//                locationUpdateer.locationUpdate(currentLocation);
//            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {
        Logger.printLog(AddFormDataActivity.this, TAG, "Building GoogleApiClient");
        if (ContextCompat.checkSelfPermission(AddFormDataActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddFormDataActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , REQUEST_CODE_PERMISSION_LOCATION);

        } else {
            if (!CommonMethods.isGPSEnabled(this)) {
                showGPSAlert();
            } else {
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                createLocationRequest();
            }
        }
    }

    private void createLocationRequest() {
        Logger.printLog(AddFormDataActivity.this, TAG, "CreateLocationRequest");
        mLocationRequest = new LocationRequest();
        long UPDATE_INTERVAL = 20 * 1000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        long FASTEST_INTERVAL = 10000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient
        mGoogleApiClient.connect();
    }

    private void showGPSAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS Setting");
        builder.setMessage("Your GPS is disable Do you want to enable GPS setting?");
        builder.setCancelable(false);
        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_PERMISSION_LOCATION);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

//        int textViewId = alert.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
//        TextView tv = (TextView) alert.findViewById(textViewId);
//        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void startLocationUpdates() {

        Log.i(TAG, "StartLocationUpdates");

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }

    }

    private void stopLocationUpdates() {
        Log.i(TAG, "StopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setDateTime() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(AddFormDataActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(Color.parseColor("#ffc107"));
        dpd.show(getFragmentManager(), DATEPICKER_TAG);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;

        EditText dateText = (EditText) llViewContainer.findViewWithTag(LASTDATETAG);
        dateText.setText(date);

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                AddFormDataActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND), true);
        tpd.setAccentColor(Color.parseColor("#ffc107"));
        tpd.show(getFragmentManager(), TIMEPICKER_TAG);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = hourOfDay + ":" + minute + ":" + second;

        EditText dateText = (EditText) llViewContainer.findViewWithTag(LASTDATETAG);
        dateText.setText(dateText.getText().toString().concat(" "+time));
    }
}
