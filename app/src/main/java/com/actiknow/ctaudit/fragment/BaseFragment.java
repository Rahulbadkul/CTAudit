package com.actiknow.ctaudit.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actiknow.ctaudit.R;
import com.actiknow.ctaudit.activity.ViewPagerActivity;
import com.actiknow.ctaudit.helper.DatabaseHandler;
import com.actiknow.ctaudit.model.Question;
import com.actiknow.ctaudit.model.Report;
import com.actiknow.ctaudit.model.Response;
import com.actiknow.ctaudit.utils.AppConfigTags;
import com.actiknow.ctaudit.utils.AppConfigURL;
import com.actiknow.ctaudit.utils.Constants;
import com.actiknow.ctaudit.utils.NetworkConnection;
import com.actiknow.ctaudit.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kyanogen.signatureview.SignatureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class BaseFragment extends android.support.v4.app.Fragment {

    public static boolean isLast = false;
    final int CAMERA_ACTIVITY = 1;
    Bitmap bp1;
    Bitmap bptemp;
    DatabaseHandler db;
    ProgressDialog pDialog;
    Question question;
    RelativeLayout rlRating;
    SeekBar sbRating;
    TextView tvRatingNumber;
    LinearLayout llChecks;
    String optionSelected = "";
    List<String> extraOptions = new ArrayList<> ();
    Dialog dialogSign;
    // Store instance variables
    private String question_text;
    private RadioGroup rgOptions;
    private int question_id, page;
    private EditText etComments;
    private TextView tvQuestion;
    private ImageView ivImage1;
    private Button btNext;
    private Button btPrev;

    private RelativeLayout rlImage;
    private TextView tvImageRequired;


    public static BaseFragment newInstance (int page) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        args.putInt (AppConfigTags.PAGE_NUMBER, page);
        isLast = true;
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    public static BaseFragment newInstance (int page, String question, int question_id) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        isLast = false;
        args.putInt (AppConfigTags.PAGE_NUMBER, page);
        args.putString (AppConfigTags.QUESTION, question);
        args.putInt (AppConfigTags.QUESTION_ID, question_id);
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        page = getArguments ().getInt (AppConfigTags.PAGE_NUMBER, 0);
        if (! isLast) {
            question_text = getArguments ().getString (AppConfigTags.QUESTION);
            question_id = getArguments ().getInt (AppConfigTags.QUESTION_ID);
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        db = new DatabaseHandler (getActivity ());
        view = inflater.inflate (R.layout.fragment_first, container, false);
        initView (view);
        initListener ();
        try {
            question = Constants.questionsList.get (page);

            Utils.showLog (Log.DEBUG, "QUESTION ID", " " + question.getQuestion_id (), true);
            Utils.showLog (Log.DEBUG, "QUESTION TEXT", question.getQuestion (), true);
            Utils.showLog (Log.DEBUG, "QUESTION TYPE", question.getQuestion_type (), true);
            Utils.showLog (Log.DEBUG, "QUESTION OPTIONS SIZE", " " + question.getOptions ().size (), true);
            Utils.showLog (Log.DEBUG, "QUESTION IMAGE REQUIRED", " " + question.isImage_required (), true);
            Utils.showLog (Log.DEBUG, "QUESTION COMMENT REQUIRED", " " + question.isComment_required (), true);
            Utils.showLog (Log.DEBUG, "QUESTION COMMENT REQUIRED FOR", " " + question.getComment_required_for (), true);
            Utils.showLog (Log.DEBUG, "QUESTION COMMENT REQUIRED", question.getQuestion (), true);
            Utils.showLog (Log.DEBUG, "QUESTION CT QUESTIONS", " " + question.isCt_question (), true);
            Utils.showLog (Log.DEBUG, "QUESTION EXTRA OPTION PRESENT", " " + question.isExtra_options_present (), true);
            Utils.showLog (Log.DEBUG, "QUESTION EXTRA OPTION SIZE", " " + question.getExtra_options ().size (), true);


            tvQuestion.setText (question.getQuestion ());
            if (question.isImage_required ()) {
                rlImage.setVisibility (View.VISIBLE);
///                if(ViewPagerActivity.ct_flag && question.isCt_question ()){
///                    tvImageRequired.setVisibility (View.GONE);
///                } else {
///                    tvImageRequired.setVisibility (View.VISIBLE);
///                }
            } else {
                rlImage.setVisibility (View.GONE);
            }


            switch (question.getQuestion_type ()) {
                case "Radio":
                    addRadioButtons (rgOptions, question.getOptions ());
                    rgOptions.setVisibility (View.VISIBLE);
                    rlRating.setVisibility (View.GONE);
                    llChecks.setVisibility (View.GONE);
                    break;
                case "Rating":
                    rgOptions.setVisibility (View.GONE);
                    rlRating.setVisibility (View.VISIBLE);
                    llChecks.setVisibility (View.GONE);
                    optionSelected = "1";
                    break;
                case "Hybrid":
                    addRadioButtons (rgOptions, question.getOptions ());
                    addCheckBoxes (llChecks, question.getExtra_options ());
                    rgOptions.setVisibility (View.VISIBLE);
                    rlRating.setVisibility (View.GONE);
                    llChecks.setVisibility (View.GONE);
                    break;
            }


///            if (ViewPagerActivity.ct_flag && question.isCt_question ()) {
///                for (int i = 0; i < rgOptions.getChildCount (); i++) {
///                    RadioButton btn = (RadioButton) rgOptions.getChildAt (i);
///                    if (btn.getText ().toString ().equalsIgnoreCase ("N/A") || btn.getText ().toString ().equalsIgnoreCase ("No")) {
///                        rgOptions.check (i + 1);
///                    }
///                }
///            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace ();
        }


        if (page == 0) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 2.0f);
            param.setMargins (10, 10, 10, 10);
            btPrev.setVisibility (View.GONE);
            btNext.setVisibility (View.VISIBLE);
            btNext.setLayoutParams (param);
        } else {
            btPrev.setVisibility (View.VISIBLE);
            btNext.setVisibility (View.VISIBLE);
        }

        if (page == Constants.questionsList.size () - 1) {
            btNext.setText ("SUBMIT");
        } else {
            btNext.setText ("NEXT");
        }

        Utils.setTypefaceToAllViews (getActivity (), tvQuestion);
        if (savedInstanceState != null) {
            bptemp = savedInstanceState.getParcelable ("BitmapImage1");
            if (bptemp != null) {
                ivImage1.setImageBitmap (bptemp);
                bp1 = bptemp;
            } else
                ivImage1.setImageResource (R.drawable.image_placeholder);
        }
        db.closeDB ();
        return view;
    }

    public void addRadioButtons (RadioGroup radioGroup, List<String> Options) {

        radioGroup.setOrientation (LinearLayout.VERTICAL);
        for (int i = 0; i < Options.size (); i++) {
            RadioButton rdbtn = new RadioButton (getActivity ());
            rdbtn.setId ((i + 1));
            rdbtn.setText (Options.get (i));
            radioGroup.addView (rdbtn);
        }

        if (page == 0) {
            final Calendar cld = Calendar.getInstance ();
            int time = cld.get (Calendar.HOUR_OF_DAY);
            Log.e ("time", String.valueOf (time));
            if (time < 5) {
                radioGroup.check (1);
            } else if (time >= 5 && time < 19) {
                radioGroup.check (2);
            } else if (time >= 19 && time < 23) {
                radioGroup.check (3);
            } else if (time >= 23) {
                radioGroup.check (1);
            }
        }
    }

    public void addCheckBoxes (LinearLayout llChecks, List<String> ExtraOptions) {
        for (int i = 0; i < ExtraOptions.size (); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins (20, 0, 0, 0);
            CheckBox checkbox = new CheckBox (getActivity ());
            checkbox.setLayoutParams (params);
//               mAllText.add (checkbox);
            checkbox.setText (ExtraOptions.get (i));
            llChecks.addView (checkbox);
        }

    }

    public boolean isPackageExists (String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getActivity ().getPackageManager ();
        packages = pm.getInstalledApplications (0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals (targetPackage)) return true;
        }
        return false;
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult (requestCode, resultCode, data);
        try {
            if (resultCode == - 1) {
                switch (requestCode) {
                    case CAMERA_ACTIVITY:
                        File f = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");
                        if (f.exists ()) {
                            bp1 = Utils.compressBitmap (BitmapFactory.decodeFile (f.getAbsolutePath ()), getActivity ());
                        }
                        ivImage1.setImageBitmap (bp1);
                        break;
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        outState.putParcelable ("BitmapImage1", bp1);
        super.onSaveInstanceState (outState);
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView ();
    }

    private void initView (View view) {
        rgOptions = (RadioGroup) view.findViewById (R.id.rgOptions);
        etComments = (EditText) view.findViewById (R.id.etComments);
        btNext = (Button) view.findViewById (R.id.btNextInFragment);
        btPrev = (Button) view.findViewById (R.id.btPrevInFragment);
        ivImage1 = (ImageView) view.findViewById (R.id.ivImage1);
        tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
        rlRating = (RelativeLayout) view.findViewById (R.id.rlRating);
        sbRating = (SeekBar) view.findViewById (R.id.sbRating);
        tvRatingNumber = (TextView) view.findViewById (R.id.tvRatingNumber);
        llChecks = (LinearLayout) view.findViewById (R.id.llChecks);
        rlImage = (RelativeLayout) view.findViewById (R.id.rlImage);
        tvImageRequired = (TextView) view.findViewById (R.id.tvImageRequired);
    }

    private void initListener () {
        etComments.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (! question.getQuestion_type ().equalsIgnoreCase ("Hybrid")) {
                    if (question.isComment_required () && s.length () > 0) {
                        etComments.setError (null);
                    } else if (question.isComment_required () && s.length () == 0 && optionSelected.equalsIgnoreCase (question.getComment_required_for ())) {
                        etComments.setError (question.getComment_hint ());
                    }
                }
            }


            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged (Editable s) {
            }
        });

        sbRating.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener () {
            public void onStopTrackingTouch (SeekBar bar) {
                int value = bar.getProgress (); // the value of the seekBar progress
            }

            public void onStartTrackingTouch (SeekBar bar) {
            }

            public void onProgressChanged (SeekBar bar, int paramInt, boolean paramBoolean) {
//                Utils.showLog (Log.DEBUG, "VALUE", "" + Math.ceil (paramInt / 20), true);
                if ((int) (Math.ceil (paramInt / 20) + 1) > 5) {
                    optionSelected = "5";
                    tvRatingNumber.setText (optionSelected);
                } else {
                    optionSelected = String.valueOf ((int) (Math.ceil (paramInt / 20) + 1));
                    tvRatingNumber.setText ("" + (int) (Math.ceil (paramInt / 20) + 1));
                }
            }
        });

        rgOptions.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged (RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount (); i++) {
                    RadioButton btn = (RadioButton) group.getChildAt (i);
                    if (btn.getId () == checkedId) {
                        String text = btn.getText ().toString ();
                        optionSelected = text;

///                        if (page == 1 && (optionSelected.equalsIgnoreCase ("N/A") || optionSelected.equalsIgnoreCase ("No"))){
///                            ViewPagerActivity.ct_flag = true;
///                        } else {
///                            ViewPagerActivity.ct_flag = false;
///                        }

                        if (question.isExtra_options_present () && optionSelected.equalsIgnoreCase (question.getExtra_option_required_for ())) {
                            llChecks.setVisibility (View.VISIBLE);
                        } else {
                            llChecks.setVisibility (View.GONE);
                            extraOptions.clear ();
                            for (int j = 0; j < llChecks.getChildCount (); j++) {
                                View nextChild = llChecks.getChildAt (j);
                                if (nextChild instanceof CheckBox) {
                                    CheckBox check = (CheckBox) nextChild;
                                    check.setChecked (false);
                                }
                            }
                        }

//                        Utils.showLog (Log.DEBUG, "SELECTED OPTION", optionSelected, true);
                        if (question.isComment_required () && optionSelected.equalsIgnoreCase (question.getComment_required_for ())) {
                            etComments.setError (question.getComment_hint ());
                        } else {
                            etComments.setError (null);
                        }

                        return;
                    }
                }
            }
        });


        ivImage1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Intent mIntent = null;
                if (isPackageExists ("com.google.android.camera")) {
                    mIntent = new Intent ();
                    mIntent.setPackage ("com.google.android.camera");
                    mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                } else {
                    PackageManager packageManager = getActivity ().getPackageManager ();
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
                    mIntent.setAction (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");
                    mIntent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile (f));

                }
                if (mIntent.resolveActivity (getActivity ().getPackageManager ()) != null)
                    startActivityForResult (mIntent, CAMERA_ACTIVITY);
            }
        });

        btNext.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Utils.hideSoftKeyboard (getActivity ());
                extraOptions.clear ();
                for (int i = 0; i < llChecks.getChildCount (); i++) {
                    View nextChild = llChecks.getChildAt (i);
                    if (nextChild instanceof CheckBox) {
                        CheckBox check = (CheckBox) nextChild;
                        if (check.isChecked ()) {
                            extraOptions.add (check.getText ().toString ());
                        }
                    }
                }
                String extra_option_response = "";
                for (int i = 0; i < extraOptions.size (); i++) {
                    extra_option_response = android.text.TextUtils.join (",", extraOptions);
                }


                if (validate ()) {
                    etComments.setError (null);
                    final String image = Utils.bitmapToBase64 (bp1);
                    Utils.showLog (Log.DEBUG, AppConfigTags.PAGE_NUMBER, "" + page, true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "question_id", "" + question.getQuestion_id (), true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "question_name", "" + question.getQuestion (), true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "response_text", optionSelected, true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "question_type", question.getQuestion_type (), true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "comment", etComments.getText ().toString (), true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "image", image, true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "extra_options", extra_option_response, true);


                    Response response = new Response ();
                    response.setQuestion_id (question.getQuestion_id ());
                    response.setQuestion (question.getQuestion ());
                    response.setQuestion_type (question.getQuestion_type ());
                    response.setResponse_text (optionSelected);
                    if (page == 0)
                        response.setComment (etComments.getText ().toString () + " " + Constants.atm_location_in_manual);
                    else
                        response.setComment (etComments.getText ().toString ());
                    response.setImage_str (image);
                    response.setExtra_response_text (extra_option_response);
                    Constants.responseList.add (page, response);


                    if (page == Constants.questionsList.size () - 1) {
//                        Utils.showToast (getActivity (), "Last page");
                        Utils.showLog (Log.INFO, "Page :", "Last page", true);
                        JSONArray jsonArray = new JSONArray ();
                        try {
                            for (int i = 0; i < Constants.questionsList.size (); i++) {
                                final Response response2;
                                response2 = Constants.responseList.get (i);
                                JSONObject jsonObject = new JSONObject ();
                                jsonObject.put ("issue_id", String.valueOf (response2.getQuestion_id ()));
                                jsonObject.put ("question_name", response2.getQuestion ());
                                jsonObject.put ("question_type", response2.getQuestion_type ());
                                jsonObject.put ("response_text", response2.getResponse_text ());
                                jsonObject.put ("image", response2.getImage_str ());
//                                jsonObject.put ("image", "");
                                jsonObject.put ("comment", response2.getComment ());
                                jsonObject.put ("extra_options", response2.getExtra_response_text ());
                                jsonArray.put (jsonObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }
                        Constants.report.setResponses_json_array (String.valueOf (jsonArray));
                        showSignatureDialog ();
                    } else {
                        ViewPagerActivity.nextPage ();
                    }








/*



//                        Utils.showToast (getActivity (), "Please select an option");
                        if (etComments.getText ().toString ().length () != 0) {
                            etComments.setError (null);
                            ViewPagerActivity.nextPage ();

                            for (int i = 0; i < llChecks.getChildCount (); i++) {
                                View nextChild = llChecks.getChildAt (i);
                                if (nextChild instanceof CheckBox) {
                                    CheckBox check = (CheckBox) nextChild;
                                    if (check.isChecked ()) {
                                        extraOptions.add (check.getText ().toString ());
                                    }
                                }
                            }

                            final String image = Utils.bitmapToBase64 (bp1);
                            Utils.showLog (Log.DEBUG, AppConfigTags.PAGE_NUMBER, "" + page, true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.QUESTION_ID, "" + question.getQuestion_id (), true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.QUESTION, "" + question.getQuestion (), true);
                            Utils.showLog (Log.DEBUG, "SELECTED OPTION", optionSelected, true);
                            Utils.showLog (Log.DEBUG, "QUESTION_TYPE", question.getQuestion_type (), true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.COMMENT, etComments.getText ().toString (), true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.IMAGE, image, true);

//                            String extra_option_response = "";
                            for (int i=0;i<extraOptions.size ();i++){
                                extra_option_response  = android.text.TextUtils.join (",", extraOptions);
                            }
                            Utils.showLog (Log.DEBUG, "EXTRA OPTIONS", extra_option_response, true);

                            /*
                        Response response = new Response ();
                        response.setResponse_auditor_id (Constants.auditor_id_main);
                        response.setResponse_agency_id (Constants.atm_agency_id);
                        response.setResponse_atm_unique_id (Constants.atm_unique_id);
                        response.setResponse_question_id (question_id);
                        response.setResponse_question (question_text);
                        response.setResponse_switch_flag (switch_flag);
                        if (page == 0)
                            response.setResponse_comment (etComments.getText ().toString () + " " + Constants.atm_location_in_manual);
                        else
                            response.setResponse_comment (etComments.getText ().toString ());
                        response.setResponse_image1 (image1);
                        response.setResponse_image2 (image2);
                        Constants.responseList.add (page, response);

                        } else {
                            etComments.setError (null);
                            ViewPagerActivity.nextPage ();

                            final String image = Utils.bitmapToBase64 (bp1);

                            Utils.showLog (Log.DEBUG, AppConfigTags.PAGE_NUMBER, "" + page, true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.AUDITOR_ID, "" + Constants.auditor_id_main, true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.QUESTION_ID, "" + question.getQuestion_id (), true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.QUESTION, "" + question.getQuestion (), true);
                            Utils.showLog (Log.DEBUG, "SELECTED OPTION", optionSelected, true);
                            Utils.showLog (Log.DEBUG, "QUESTION_TYPE", question.getQuestion_type (), true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.COMMENT, etComments.getText ().toString (),true);
                            Utils.showLog (Log.DEBUG, AppConfigTags.IMAGE, image, true);

                            Utils.showLog (Log.DEBUG, "EXTRA OPTIONS", extra_option_response, true);

/*
                    Response response = new Response ();
                    response.setResponse_auditor_id (Constants.auditor_id_main);
                    response.setResponse_agency_id (Constants.atm_agency_id);
                    response.setResponse_atm_unique_id (Constants.atm_unique_id);
                    response.setResponse_question_id (question_id);
                    response.setResponse_question (question_text);
                    response.setResponse_switch_flag (switch_flag);
                    if (page == 0)
                        response.setResponse_comment (etComments.getText ().toString () + " " + Constants.atm_location_in_manual);
                    else
                        response.setResponse_comment (etComments.getText ().toString ());
                    response.setResponse_image1 (image1);
                    response.setResponse_image2 (image2);
                    Constants.responseList.add (page, response);

                        }
*/

                }
            }
        });
        btPrev.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Utils.hideSoftKeyboard (getActivity ());
                ViewPagerActivity.prevPage ();

            }
        });
    }

    private boolean validate () {
        boolean validate = true;
        List<String> error = new ArrayList<> ();
        error.clear ();
        switch (question.getQuestion_type ()) {
            case "Radio":
                if (optionSelected.length () == 0) {
                    error.add ("Select an option");
//                    Utils.showToast (getActivity (), "Please select an option");
                    validate = false;
                }
                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0 && ! ViewPagerActivity.ct_flag) {
                    error.add ("Select an image");
//                    Utils.showToast (getActivity (), "Select an image");
                    validate = false;
                }
///                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0 && ViewPagerActivity.ct_flag && question.isCt_question ()) {
/////                    error.add ("Select an image");
/////                    Utils.showToast (getActivity (), "Select an image");
///                    validate = true;
///                }
                if (question.isComment_required () && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () == 0) {
                    error.add ("Enter the value in comment");
//                    Utils.showToast (getActivity (), "Enter the value in comment");
                    validate = false;
                }
                if (error.size () > 0) {
                    Utils.showValidationErrorDialog (getActivity (), error);
                }
                break;
            case "Hybrid":
                if (optionSelected.length () == 0) {
                    error.add ("Select an option");
//                    Utils.showToast (getActivity (), "Please select an option");
                    validate = false;
                }
                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0) {
                    error.add ("Select an image");
//                    Utils.showToast (getActivity (), "Select an image");
                    validate = false;
                }
                if (question.isComment_required () && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () == 0) {
                    error.add ("Enter the value in comment");
//                    Utils.showToast (getActivity (), "Enter the value in comment");
                    validate = false;
                }
                if (optionSelected.equalsIgnoreCase (question.getExtra_option_required_for ()) && extraOptions.size () == 0) {
                    error.add ("Select atleast one value");
//                    Utils.showToast (getActivity (), "Select atleast one value");
                    validate = false;
                }
                if (error.size () > 0) {
                    Utils.showValidationErrorDialog (getActivity (), error);
                }
                break;
            case "Blank":
                if (question.isComment_required () && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () == 0) {
                    error.add ("Enter the value in comment");
//                    Utils.showToast (getActivity (), "Enter the value in comment");
                    validate = false;
                }
                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0) {
                    error.add ("Select an image");
//                    Utils.showToast (getActivity (), "Select an image");
                    validate = false;
                }
                if (error.size () > 0) {
                    Utils.showValidationErrorDialog (getActivity (), error);
                }
                break;
            case "Rating":
                if (question.isComment_required () && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () == 0) {
                    error.add ("Enter the value in comment");
//                    Utils.showToast (getActivity (), "Enter the value in comment");
                    validate = false;
                }
                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0) {
                    error.add ("Select an Image");
//                    Utils.showToast (getActivity (), "Select an Image");
                    validate = false;
                }
                if (error.size () > 0) {
                    Utils.showValidationErrorDialog (getActivity (), error);
                }
                break;
        }
        return validate;
    }

    private void showSignatureDialog () {
        Button btSignCancel;
        Button btSignClear;
        Button btSignSave;
        final SignatureView signatureView;

        dialogSign = new Dialog (getActivity ());
        dialogSign.setContentView (R.layout.dialog_signature);
        dialogSign.setCancelable (false);
        btSignCancel = (Button) dialogSign.findViewById (R.id.btSignCancel);
        btSignClear = (Button) dialogSign.findViewById (R.id.btSignClear);
        btSignSave = (Button) dialogSign.findViewById (R.id.btSignSave);
        signatureView = (SignatureView) dialogSign.findViewById (R.id.signSignatureView);

        Utils.setTypefaceToAllViews (getActivity (), btSignCancel);
//        dialogSign.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialogSign.getWindow ().setBackgroundDrawable (new ColorDrawable (android.graphics.Color.TRANSPARENT));
        dialogSign.show ();
        btSignCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                dialogSign.dismiss ();
            }
        });
        btSignClear.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                signatureView.clearCanvas ();
            }
        });
        btSignSave.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                dialogSign.dismiss ();
                pDialog = new ProgressDialog (getActivity ());
                Utils.showProgressDialog (pDialog, null);
                Bitmap bp = signatureView.getSignatureBitmap ();
                Constants.report.setSignature_image_string (Utils.bitmapToBase64 (bp));
                submitReportToServer (Constants.report);
            }
        });
    }

    private void submitReportToServer (final Report report) {
        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SUBMITREPORT, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SUBMITREPORT,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                pDialog.dismiss ();
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    switch (jsonObj.getInt (AppConfigTags.STATUS)) {
                                        case 0:
                                            db.createReport (report);
                                            pDialog.dismiss ();
                                            Utils.showOkDialog (getActivity (), "Some error occurred your responses have been saved offline and will be uploaded later", true);
                                            break;
                                        case 1:
                                            pDialog.dismiss ();
                                            Utils.showOkDialog (getActivity (), "Your responses have been uploaded successfully to the server", true);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                db.createReport (report);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            pDialog.dismiss ();
                            Utils.showOkDialog (getActivity (), "Seems like there is an issue with the internet connection," +
                                    " your responses have been saved and will be uploaded once you are online", true);
                            db.createReport (report);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.ATM_ID, String.valueOf (report.getAtm_id ()));
                    params.put (AppConfigTags.ATM_UNIQUE_ID, report.getAtm_unique_id ());
                    params.put (AppConfigTags.ATM_AGENCY_ID, String.valueOf (report.getAgency_id ()));
                    params.put (AppConfigTags.AUDITOR_ID, String.valueOf (report.getAuditor_id ()));
                    params.put (AppConfigTags.ISSUES, report.getResponses_json_array ());
                    params.put (AppConfigTags.GEO_IMAGE, report.getGeo_image_string ());
                    params.put (AppConfigTags.LATITUDE, report.getLatitude ());
                    params.put (AppConfigTags.LONGITUDE, report.getLongitude ());
                    params.put (AppConfigTags.SIGN_IMAGE, report.getSignature_image_string ());

                    Log.e (AppConfigTags.ATM_ID, String.valueOf (report.getAtm_id ()));
                    Log.e (AppConfigTags.ATM_UNIQUE_ID, report.getAtm_unique_id ());
                    Log.e (AppConfigTags.ATM_AGENCY_ID, String.valueOf (report.getAgency_id ()));
                    Log.e (AppConfigTags.AUDITOR_ID, String.valueOf (report.getAuditor_id ()));
                    Log.e (AppConfigTags.ISSUES, report.getResponses_json_array ());
//                        Log.e (AppConfigTags.GEO_IMAGE, finalReport.getGeo_image_string ());
                    Log.e (AppConfigTags.LATITUDE, report.getLatitude ());
                    Log.e (AppConfigTags.LONGITUDE, report.getLongitude ());
//                        Log.e (AppConfigTags.SIGN_IMAGE, finalReport.getSignature_image_string ());


//                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 300);
        } else {
            pDialog.dismiss ();
            Utils.showOkDialog (getActivity (), "Seems like there is no internet connection, your responses have been saved" +
                    " and will be uploaded once you are online", true);
            db.createReport (report);
        }
    }
}