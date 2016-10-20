package com.actiknow.ctaudit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actiknow.ctaudit.R;
import com.actiknow.ctaudit.helper.DatabaseHandler;
import com.actiknow.ctaudit.model.Atm;
import com.actiknow.ctaudit.utils.AppConfigTags;
import com.actiknow.ctaudit.utils.AppConfigURL;
import com.actiknow.ctaudit.utils.Constants;
import com.actiknow.ctaudit.utils.NetworkConnection;
import com.actiknow.ctaudit.utils.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class AtmDetailActivity extends AppCompatActivity {

    public static int GEO_IMAGE_REQUEST_CODE = 1;

    TextView tvDate;
    TextView tvAtmId;
    TextView tvBankName;
    TextView tvAtmAddress;
    Button btContinue;

    RelativeLayout rlDetails;

    ProgressDialog progressDialog;

    DatabaseHandler db;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_atm_details);
        initView ();
        initData ();
        initListener ();

        getAtmListFromServer ();
        //getAtmListFromLocalDatabase ();
        db.closeDB ();
    }

    private void initView () {
        tvDate = (TextView) findViewById (R.id.tvDate);
        tvAtmId = (TextView) findViewById (R.id.tvAtmId);
        tvBankName = (TextView) findViewById (R.id.tvBankName);
        tvAtmAddress = (TextView) findViewById (R.id.tvAtmAddress);
        btContinue = (Button) findViewById (R.id.btContinue);
        rlDetails = (RelativeLayout) findViewById (R.id.rlDetails);
    }

    private void initData () {
        progressDialog = new ProgressDialog (AtmDetailActivity.this);
        db = new DatabaseHandler (getApplicationContext ());
    }

    private void initListener () {
        btContinue.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                TextView tvMessage;
                MaterialDialog dialog = new MaterialDialog.Builder (AtmDetailActivity.this)
                        .customView (R.layout.dialog_basic, true)
                        .positiveText (R.string.dialog_geoimage_positive)
                        .negativeText (R.string.dialog_geoimage_negative)
                        .onPositive (new MaterialDialog.SingleButtonCallback () {
                            @Override
                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss ();
                                Intent mIntent = null;
                                if (Utils.isPackageExists (AtmDetailActivity.this, "com.google.android.camera")) {
                                    mIntent = new Intent ();
                                    mIntent.setPackage ("com.google.android.camera");
                                    mIntent.setAction (MediaStore.ACTION_IMAGE_CAPTURE);
                                } else {
                                    PackageManager packageManager = AtmDetailActivity.this.getPackageManager ();
                                    String defaultCameraPackage = null;
                                    List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
                                    for (int n = 0; n < list.size (); n++) {
                                        if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                                            Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString (), false);
                                            Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "package name  : " + list.get (n).packageName, false);
                                            if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
                                                defaultCameraPackage = list.get (n).packageName;
                                                break;
                                            }
                                        }
                                    }
                                    mIntent = new Intent ();
                                    mIntent.setPackage (defaultCameraPackage);
                                    mIntent.setAction (MediaStore.ACTION_IMAGE_CAPTURE);
                                    File f = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");
                                    mIntent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile (f));
                                }
                                if (mIntent.resolveActivity (getPackageManager ()) != null)
                                    startActivityForResult (mIntent, GEO_IMAGE_REQUEST_CODE);
                            }
                        }).build ();

                tvMessage = (TextView) dialog.getCustomView ().findViewById (R.id.tvMessage);
                tvMessage.setText (R.string.dialog_geoimage_content);
                Utils.setTypefaceToAllViews (AtmDetailActivity.this, tvMessage);
                dialog.show ();



/*
                AlertDialog.Builder builder = new AlertDialog.Builder (AtmDetailActivity.this);
                builder.setMessage ("Please take an image of the ATM Machine\nNote : This image will be Geotagged")
                        .setCancelable (false)
                        .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                            public void onClick (DialogInterface dialog, int id) {
                                dialog.dismiss ();
                                Intent mIntent = null;
                                if (Utils.isPackageExists (AtmDetailActivity.this, "com.google.android.camera")) {
                                    mIntent = new Intent ();
                                    mIntent.setPackage ("com.google.android.camera");
                                    mIntent.setAction (MediaStore.ACTION_IMAGE_CAPTURE);
                                } else {
                                    PackageManager packageManager = AtmDetailActivity.this.getPackageManager ();
                                    String defaultCameraPackage = null;
                                    List<ApplicationInfo> list = packageManager.getInstalledApplications (PackageManager.GET_UNINSTALLED_PACKAGES);
                                    for (int n = 0; n < list.size (); n++) {
                                        if ((list.get (n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                                            Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Installed Applications  : " + list.get (n).loadLabel (packageManager).toString (), false);
                                            Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "package name  : " + list.get (n).packageName, false);
                                            if (list.get (n).loadLabel (packageManager).toString ().equalsIgnoreCase ("Camera")) {
                                                defaultCameraPackage = list.get (n).packageName;
                                                break;
                                            }
                                        }
                                    }
                                    mIntent = new Intent ();
                                    mIntent.setPackage (defaultCameraPackage);
                                    mIntent.setAction (MediaStore.ACTION_IMAGE_CAPTURE);
                                    File f = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");
                                    mIntent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile (f));
                                }
                                if (mIntent.resolveActivity (getPackageManager ()) != null)
                                    startActivityForResult (mIntent, GEO_IMAGE_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton ("CANCEL", new DialogInterface.OnClickListener () {
                            public void onClick (DialogInterface dialog, int id) {
                                dialog.dismiss ();
                            }
                        });
                AlertDialog alert = builder.create ();
                alert.show ();
*/

            }
        });

    }


    private void getAtmListFromServer () {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showProgressDialog (progressDialog, null, true);
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_GETALLATMS, true);
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_GETALLATMS,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    progressDialog.dismiss ();
                                    JSONObject jsonObj = new JSONObject (response);
                                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.ATMS);
                                    for (int i = 0; i < jsonArray.length (); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                                        if (jsonObject.getInt (AppConfigTags.ATM_ID) == Constants.report.getAtm_id ()) {
                                            tvDate.setText (Utils.convertTimeFormat (jsonObject.getString (AppConfigTags.ATM_LAST_AUDIT_DATE)));
                                            tvBankName.setText (jsonObject.getString (AppConfigTags.ATM_BANK_NAME));
                                            tvAtmAddress.setText (jsonObject.getString (AppConfigTags.ATM_ADDRESS) + ", " + jsonObject.getString (AppConfigTags.ATM_CITY) + ", " + jsonObject.getString (AppConfigTags.ATM_PINCODE));
                                            tvAtmId.setText (jsonObject.getString (AppConfigTags.ATM_UNIQUE_ID));
                                        }
                                    }
                                    rlDetails.setVisibility (View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                                getAtmListFromLocalDatabase ();
                                progressDialog.dismiss ();
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            getAtmListFromLocalDatabase ();
                            progressDialog.dismiss ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (Constants.auditor_id_main));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 30);

        } else {
            getAtmListFromLocalDatabase ();
            Utils.showOkDialog (AtmDetailActivity.this, "Seems like there is no internet connection, the app will continue in Offline mode", false);
            progressDialog.dismiss ();
        }
    }

    private void getAtmListFromLocalDatabase () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the atm from local database", true);
        List<Atm> allAtm = db.getAllAtms ();
        for (Atm atm : allAtm) {
            if (atm.getAtm_id () == Constants.report.getAtm_id ()) {
                tvDate.setText (atm.getAtm_last_audit_date ());
                tvBankName.setText (atm.getAtm_bank_name ());
                tvAtmAddress.setText (atm.getAtm_address () + ", " + atm.getAtm_city () + ", " + atm.getAtm_pincode ());
                tvAtmId.setText (atm.getAtm_unique_id ());
            }
        }
        rlDetails.setVisibility (View.VISIBLE);
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == GEO_IMAGE_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    File f = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");

                    Bitmap bp = null;
                    if (f.exists ()) {
                        bp = Utils.compressBitmap (BitmapFactory.decodeFile (f.getAbsolutePath ()), AtmDetailActivity.this);
                    }

//                    Bitmap bp = (Bitmap) data.getExtras ().get ("data");
                    String image = Utils.bitmapToBase64 (bp);
                    Constants.report.setGeo_image_string (image);

                    Utils.showLog (Log.DEBUG, "GEO IMAGE", " " + image, true);

                    Intent intent = new Intent (AtmDetailActivity.this, ViewPagerActivity.class);
                    startActivity (intent);
                    overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case RESULT_CANCELED:
//                    Utils.showToast (MainActivity.this, "Please take an image");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed () {
        if (progressDialog.isShowing ())
            progressDialog.dismiss ();

        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
