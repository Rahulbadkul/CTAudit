package com.actiknow.ctaudit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actiknow.ctaudit.R;
import com.actiknow.ctaudit.utils.AppConfigTags;
import com.actiknow.ctaudit.utils.AppConfigURL;
import com.actiknow.ctaudit.utils.Constants;
import com.actiknow.ctaudit.utils.LoginDetailsPref;
import com.actiknow.ctaudit.utils.NetworkConnection;
import com.actiknow.ctaudit.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    TextView tvForgetPassword;
    EditText etUsername, etPassword;
    TextView tvLogin;
    ProgressDialog progressDialog;
    CoordinatorLayout coordinatorLayout;
    //    MorphingButton btLogin;
    TextInputLayout input_layout_password, input_layout_email;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        initView ();
        initData ();
        initListener ();
    }

    private void initView () {
        coordinatorLayout = (CoordinatorLayout) findViewById (R.id.coordinatorLayout);
        tvForgetPassword = (TextView) findViewById (R.id.tvForgetPassword);
        etUsername = (EditText) findViewById (R.id.etUsername);
        etPassword = (EditText) findViewById (R.id.etPassword);
        tvLogin = (TextView) findViewById (R.id.tvLogin);
        input_layout_email = (TextInputLayout) findViewById (R.id.input_layout_email);
        input_layout_password = (TextInputLayout) findViewById (R.id.input_layout_password);
    }

    private void initData () {
        progressDialog = new ProgressDialog (LoginActivity.this);
        Utils.setTypefaceToAllViews (this, tvForgetPassword);
    }

    private void initListener () {
        etPassword.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                input_layout_password.setError (null);
                input_layout_password.setErrorEnabled (false);
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged (Editable s) {

                // TODO Auto-generated method stub
            }
        });

        etUsername.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                input_layout_email.setError (null);
                input_layout_email.setErrorEnabled (false);
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged (Editable s) {

                // TODO Auto-generated method stub
            }
        });

        tvForgetPassword.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tvForgetPassword.setTextColor (getResources ().getColor (R.color.colorPrimary));
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tvForgetPassword.setTextColor (getResources ().getColor (R.color.text_color_white));
                }
                return true;
            }
        });
        tvLogin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Utils.hideSoftKeyboard (LoginActivity.this);
                int status_username = Utils.isValidEmail (etUsername.getText ().toString ());
                int status_password = Utils.isValidPassword (etPassword.getText ().toString ());
                if (status_username == 1 && status_password == 1) {
                    sendLoginDetailsToServer ();
                }
                if (status_username == 0) {
                    input_layout_email.setError ("Enter a Username");
//                    etUsername.setError ("Enter a Username");
                }
                if (status_username == 2) {
                    input_layout_email.setError ("Enter correct Username");
//                    etUsername.setError ("Enter correct Username");
                }
                if (status_password == 0) {
                    input_layout_password.setError ("Enter the password");
//                    etPassword.setError ("Enter the password");
                }
            }
        });
    }

    private void sendLoginDetailsToServer () {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_LOGIN, true);
            Utils.showProgressDialog (progressDialog, null, false);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_LOGIN,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    progressDialog.dismiss ();
                                    JSONObject jsonObj = new JSONObject (response);
                                    int status = jsonObj.getInt (AppConfigTags.STATUS);
                                    if (status == 1) {
                                        Constants.auditor_id_main = jsonObj.getInt (AppConfigTags.AUDITOR_ID);
                                        Constants.username = jsonObj.getString (AppConfigTags.AUDITOR_EMAIL);
                                        Constants.auditor_name = jsonObj.getString (AppConfigTags.AUDITOR_NAME);
                                        Constants.auditor_agency_id = jsonObj.getInt (AppConfigTags.AUDITOR_AGENCY_ID);
                                        setPreferences ();
                                        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                                        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity (intent);
                                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                    } else {
                                        progressDialog.dismiss ();
                                        Utils.showSnackBar (coordinatorLayout, "Invalid Login Credentials");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            progressDialog.dismiss ();
                            Utils.showSnackBar (coordinatorLayout, "Please try again after some time");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.EMAIL, etUsername.getText ().toString ());
                    params.put (AppConfigTags.PASSWORD, etPassword.getText ().toString ());
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };

            Utils.sendRequest (strRequest1, 8);


        } else {
            progressDialog.dismiss ();
            Utils.showSnackBar (coordinatorLayout, "Please check your network connection");
        }


    }

    private void setPreferences () {
        LoginDetailsPref loginDetailsPref = LoginDetailsPref.getInstance ();
        loginDetailsPref.putStringPref (LoginActivity.this, LoginDetailsPref.AUDITOR_NAME, Constants.auditor_name);
        loginDetailsPref.putStringPref (LoginActivity.this, LoginDetailsPref.USERNAME, Constants.username);
        loginDetailsPref.putIntPref (LoginActivity.this, LoginDetailsPref.AUDITOR_ID, Constants.auditor_id_main);
        loginDetailsPref.putIntPref (LoginActivity.this, LoginDetailsPref.AUDITOR_AGENCY_ID, Constants.auditor_agency_id);
    }

    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }
}