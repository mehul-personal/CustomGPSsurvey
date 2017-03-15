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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.analytics.customgpssurvey.model.ProjectModel;
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

public class ProjectListingActivity extends AppCompatActivity {
    public static String TAG = "ProjectListingActivity";
    ExpandableHeightGridView ExpandableView;
    Typeface LibreBoldFont, RobotRegularFont;
    MyPreferences preferences;
    ProjectModel projectFormList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_listing);

        preferences = new MyPreferences(this);
        ExpandableView = (ExpandableHeightGridView) findViewById(R.id.ExpandableView);
        ExpandableView.setExpanded(true);

        LibreBoldFont = Typeface.createFromAsset(getAssets(),
                "LibreBaskerville-Bold.otf");
        RobotRegularFont = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Projects");

        ExpandableView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        getProjectList();
    }

    public void getProjectList() {
        APICommunicator.showProgress(this, "Please wait..");
        APICommunicator.getProjectList(this, preferences.getUserID(), new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                //Logger.printLog(ProjectListingActivity.this, TAG + " - getProjectlist", response.toString());
                APICommunicator.stopProgress();
                preferences.setOfflineProjectListData((String) response);
                getProjectData((String) response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                APICommunicator.stopProgress();
                error.printStackTrace();
                //Logger.printLog(ProjectListingActivity.this, TAG, "login Error");
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    getProjectData(preferences.getOfflineProjectListData());
                } else {
                    ToastMsg.showShort(ProjectListingActivity.this, "Something is wrong Please try again!");
                }
            }
        });
    }

    public void getProjectData(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type listType = new TypeToken<ProjectModel>() {
                }.getType();
                projectFormList = gson.fromJson(jsonResponse.toString(), listType);

                ProjectListAdapter adapter = new ProjectListAdapter(projectFormList.getData());
                ExpandableView.setAdapter(adapter);

            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                //Logger.printLog(ProjectListingActivity.this, TAG, stackTrace.toString());
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

    public class ProjectListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        List<ProjectModel.ProjectData> ProjectFormList;

        public ProjectListAdapter(List<ProjectModel.ProjectData> ProjectFormList) {
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
                convertView = inflater.inflate(R.layout.item_project_griddata, null);
                holder = new ViewHolder();
                holder.projectName = (TextView) convertView.findViewById(R.id.txvProjectName);
                holder.itemRow = (LinearLayout) convertView.findViewById(R.id.itemRow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.projectName.setTypeface(RobotRegularFont);
            holder.projectName.setText(ProjectFormList.get(position).getProject());
            holder.itemRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ProjectID = projectFormList.getData().get(position).getId();
                    Intent intent = new Intent(ProjectListingActivity.this, FormListingActivity.class);
                    intent.putExtra("PROJECT_ID", "" + ProjectID);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView projectName;
            LinearLayout itemRow;
        }

    }

}
