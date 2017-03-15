package com.analytics.customgpssurvey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.analytics.customgpssurvey.model.AllFormModel;
import com.analytics.customgpssurvey.utils.MyPreferences;
import com.analytics.customgpssurvey.utils.ToastMsg;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class FormListingActivity extends AppCompatActivity {
    private final String TAG = "FormListingActivity";
    ListView formListview;
    Typeface LibreBoldFont, RobotRegularFont;
    MyPreferences preferences;
    AllFormModel projectFormList;
    String Project_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_listing);
        formListview = (ListView) findViewById(R.id.formListview);
        preferences = new MyPreferences(this);
        LibreBoldFont = Typeface.createFromAsset(getAssets(),
                "LibreBaskerville-Bold.otf");
        RobotRegularFont = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forms");
        Project_id = getIntent().getStringExtra("PROJECT_ID");
        formListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FormListingActivity.this, ManageFormActivity.class);
                intent.putExtra("TITLE", "" + projectFormList.getData().get(i).getDescription());
                intent.putExtra("FORMID", "" + projectFormList.getData().get(i).getFormid());
                startActivity(intent);
            }
        });
        getFormList();
    }

    public void getFormList() {
        APICommunicator.showProgress(this, "Please wait..");
        APICommunicator.getFormList(this, preferences.getUserID(), Project_id, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                //Logger.printLog(FormListingActivity.this, TAG + " - getformlist", response.toString());
                APICommunicator.stopProgress();
                preferences.setOfflineFormListData((String) response,Project_id);
                getFormData((String) response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                APICommunicator.stopProgress();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    getFormData(preferences.getOfflineFormListData(Project_id));
                } else {
                    ToastMsg.showShort(FormListingActivity.this, "Something is wrong Please try again!");
                }
                //Logger.printLog(FormListingActivity.this, TAG, "login Error");
            }
        });
    }

    public void getFormData(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type listType = new TypeToken<AllFormModel>() {
                }.getType();
                projectFormList = gson.fromJson(jsonResponse.toString(), listType);

                FormListAdapter adapter = new FormListAdapter(projectFormList.getData());
                formListview.setAdapter(adapter);

            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                //Logger.printLog(FormListingActivity.this, TAG, stackTrace.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public class FormListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        List<AllFormModel.FormData> ProjectFormList;

        public FormListAdapter(List<AllFormModel.FormData> ProjectFormList) {
            this.ProjectFormList = ProjectFormList;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return ProjectFormList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_form_listdata, null);
                holder = new ViewHolder();
                holder.formName = (TextView) convertView.findViewById(R.id.txvFormName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.formName.setTypeface(RobotRegularFont);
            holder.formName.setText(ProjectFormList.get(position).getDescription());

            return convertView;
        }

        class ViewHolder {
            TextView formName;
        }

    }

}
