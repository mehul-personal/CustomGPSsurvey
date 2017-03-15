package com.analytics.customgpssurvey;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.analytics.customgpssurvey.utils.MyPreferences;
import com.analytics.customgpssurvey.utils.ToastMsg;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    public static final String TAG = "SignInActivity";
    static MyPreferences preferences;
    TextView txvSign, txvIn, txvUsername, txvPassword, txvForgotPassword, txvSignIn;
    EditText edtUserName, edtPassword;
    CheckBox chbRememberMe;
    Typeface LibreBoldFont, RobotRegularFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        txvSign = (TextView) findViewById(R.id.txvSign);
        txvIn = (TextView) findViewById(R.id.txvIn);
        txvUsername = (TextView) findViewById(R.id.txvUsername);
        txvPassword = (TextView) findViewById(R.id.txvPassword);
        txvForgotPassword = (TextView) findViewById(R.id.txvForgotPassword);
        txvSignIn = (TextView) findViewById(R.id.txvSignIn);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        chbRememberMe = (CheckBox) findViewById(R.id.chbRememberMe);

        LibreBoldFont = Typeface.createFromAsset(getAssets(),
                "LibreBaskerville-Bold.otf");
        RobotRegularFont = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        txvSign.setTypeface(LibreBoldFont);
        txvIn.setTypeface(LibreBoldFont);
        txvUsername.setTypeface(RobotRegularFont);
        txvPassword.setTypeface(RobotRegularFont);
        txvForgotPassword.setTypeface(RobotRegularFont);
        txvSignIn.setTypeface(RobotRegularFont);
        edtUserName.setTypeface(RobotRegularFont);
        edtPassword.setTypeface(RobotRegularFont);
        chbRememberMe.setTypeface(RobotRegularFont);

        txvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtUserName.getText().toString().isEmpty()) {
                    ToastMsg.showShort(SignInActivity.this, "Please enter username");
                } else if (edtPassword.getText().toString().isEmpty()) {
                    ToastMsg.showShort(SignInActivity.this, "Please enter password");
                } else {
                    login(edtUserName.getText().toString(), edtPassword.getText().toString());
                }

            }
        });
        preferences = new MyPreferences(this);
        Log.e("Preferences value", ">" + preferences.isLoggedIn() + ">" + preferences.getUserID());
        if (preferences.isLoggedIn()) {
            Intent i = new Intent(SignInActivity.this, ProjectListingActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void login(String username, String password) {
        APICommunicator.showProgress(this, "Please wait..");
        APICommunicator.login(this, username, password, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                //Logger.printLog(SignInActivity.this, TAG + " - login", response.toString());
                APICommunicator.stopProgress();
                setLoginData(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                APICommunicator.stopProgress();
                //Logger.printLog(SignInActivity.this, TAG, "login Error");
            }
        });
    }

    public void setLoginData(String response) {
        try {
            JSONObject var = new JSONObject(response.toString());
            if (var.getString("msg").equalsIgnoreCase("Success")) {
                JSONObject dataob = var.getJSONArray("data").getJSONObject(0);

                preferences.setUserID(dataob.getString("userid"));
                if (chbRememberMe.isChecked()) {
                    preferences.setLoggedIn(true);
                } else {
                    preferences.setLoggedIn(false);
                }

                Intent i = new Intent(SignInActivity.this, ProjectListingActivity.class);
                startActivity(i);
                finish();
            } else {
                ToastMsg.showLong(SignInActivity.this, "Your Login Authentication Failure \nPlease try again!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
