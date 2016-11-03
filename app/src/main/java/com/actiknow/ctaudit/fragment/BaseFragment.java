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
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class BaseFragment extends android.support.v4.app.Fragment {

    final int CAMERA_ACTIVITY = 1;
    final int OTHER_CAMERA_ACTIVITY = 2;
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
    List<String> otherImagesList = new ArrayList<> ();
    // Store instance variables
    private RadioGroup rgOptions;
    private int page;
    private EditText etComments;
    private TextView tvQuestion;
    private ImageView ivImage1;
    private Button btNext;
    private Button btPrev;
    private RelativeLayout rlImage;
    private TextView tvImageRequired;
    private RelativeLayout rlMake;
    //    private TextView tvMakeRequired;
    private EditText etMake;
    private RelativeLayout rlSerial;
    //    private TextView tvSerialRequired;
    private EditText etSerial;
    private RelativeLayout rlOtherImages;
    private TextView tvOtherImageAdd;
    private LinearLayout llOtherImages;
    private TextInputLayout input_layout_comment;
    private TextInputLayout input_layout_make;
    private TextInputLayout input_layout_serial;

    public static BaseFragment newInstance (int page) {
        BaseFragment fragmentFirst = new BaseFragment ();
        Bundle args = new Bundle ();
        args.putInt (AppConfigTags.PAGE_NUMBER, page);
        fragmentFirst.setArguments (args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        page = getArguments ().getInt (AppConfigTags.PAGE_NUMBER, 0);
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

            if (question.isNumeric_comment ()) {
                etComments.setInputType (InputType.TYPE_CLASS_NUMBER);
            }

            if (question.isMake_serial_type ()) {
                rlMake.setVisibility (View.VISIBLE);
                rlSerial.setVisibility (View.VISIBLE);
//                input_layout_serial.setErrorEnabled (true);
//                input_layout_make.setErrorEnabled (true);
//                input_layout_serial.setError ("Please Specify");
//                input_layout_make.setError ("Pleasse Specify");

//                tvMakeRequired.setVisibility (View.VISIBLE);
//                tvSerialRequired.setVisibility (View.VISIBLE);
            }


//            if (question.getQuestion_id () == 14 || question.getQuestion_id () == 22) {
//                etComments.setInputType (InputType.TYPE_CLASS_NUMBER);
//            }

            if (page == Constants.questionsList.size () - 1) {
                rlImage.setVisibility (View.GONE);
                rlOtherImages.setVisibility (View.VISIBLE);
            } else {
                rlOtherImages.setVisibility (View.GONE);
            }

            tvQuestion.setText (question.getQuestion ());
            if (question.isImage_required ()) {
                rlImage.setVisibility (View.VISIBLE);
            } else {
//                Utils.showLog (Log.DEBUG, "IS CT NA ", "" + Utils.isCtNA (), true);
                if (Utils.isCtNA ()) {
                    if (question.getQuestion_id () == 13 || question.getQuestion_id () == 2) {
                        rlImage.setVisibility (View.VISIBLE);
                    } else {
                        rlImage.setVisibility (View.GONE);
                    }
                } else if (Utils.isCtNo ()) {
                    if (question.getQuestion_id () == 13) {
                        rlImage.setVisibility (View.VISIBLE);
                    } else {
                        rlImage.setVisibility (View.GONE);
                    }
                } else {
                    rlImage.setVisibility (View.GONE);
                }
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

    public void setDefaultCheck (RadioGroup radioGroup, int i) {
        for (int j = 0; j < radioGroup.getChildCount (); j++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt (j);
            switch (i) {
                case 2:
                    if (radioButton.getText ().toString ().equalsIgnoreCase ("N/A")) {
                        radioGroup.check (j + 1);
                    }
                    break;
                case 1:
                    if (radioButton.getText ().toString ().equalsIgnoreCase ("CT Not Available")) {
                        radioGroup.check (j + 1);
                    }
                    break;
            }
        }
    }

    @Override
    public void setMenuVisibility (final boolean visible) {
        super.setMenuVisibility (visible);
        Utils.showLog (Log.DEBUG, "page number karman : ", "" + page, true);
        try {
            question = Constants.questionsList.get (page);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace ();
        }
        Utils.showLog (Log.DEBUG, "question number karman : ", "" + question.getQuestion_id (), true);
        if (visible) {
//            if (question.getQuestion_id () == 20 && etComments.getText ().toString ().length () == 0) {
//                etComments.setHint ("Specify UPS Make and Sr Number");
//            }
//            if (question.getQuestion_id () == 19 && etComments.getText ().toString ().length () == 0) {
//                etComments.setHint ("Specify AC Make");
//            }

            if (question.getQuestion_id () > Constants.first_ct_question) {
                if (question.isCt_question ()) {
                    if (Utils.isCtNA ()) {
                        if (rgOptions.getCheckedRadioButtonId () == - 1) {
                            setDefaultCheck (rgOptions, 2);
                        }
                        if (question.getQuestion_id () == 13) {
                            question.setImage_required (false);
                            tvImageRequired.setVisibility (View.GONE);
                        }
                    }
                    if (Utils.isCtNo ()) {
                        if (rgOptions.getCheckedRadioButtonId () == - 1) {
                            setDefaultCheck (rgOptions, 1);
                        }
                        if (question.getQuestion_id () == 13) {
                            question.setImage_required (false);
                            tvImageRequired.setVisibility (View.GONE);
                        }
                    }
                }
            }
        }
    }

    public void addRadioButtons (RadioGroup radioGroup, List<String> Options) {
        radioGroup.setOrientation (LinearLayout.VERTICAL);
        for (int i = 0; i < Options.size (); i++) {
            RadioButton rb = new RadioButton (getActivity ());
            rb.setId ((i + 1));
            rb.setText (Options.get (i));
            radioGroup.addView (rb);
        }

        if (question.isAuto_time ()) {
            for (int i = 0; i < rgOptions.getChildCount (); i++) {
                rgOptions.getChildAt (i).setEnabled (false);
            }

            int time = Utils.getHourFromServerTime ();
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
            if (packageInfo.packageName.equals (targetPackage))
                return true;
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
                    case OTHER_CAMERA_ACTIVITY:
                        ImageView image = null;
                        File f2 = new File (Environment.getExternalStorageDirectory () + File.separator + "img.jpg");
                        Bitmap thePic = null;
                        if (f2.exists ()) {
                            thePic = Utils.compressBitmap (BitmapFactory.decodeFile (f2.getAbsolutePath ()), getActivity ());
                        }
                        otherImagesList.add (Utils.bitmapToBase64 (thePic));
                        image = new ImageView (getActivity ());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (500, 375);
                        params.setMargins (10, 10, 10, 10);
                        image.setLayoutParams (params);
                        llOtherImages.addView (image);
                        image.setImageBitmap (thePic);
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
        input_layout_comment = (TextInputLayout) view.findViewById (R.id.input_layout_comment);
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

        rlMake = (RelativeLayout) view.findViewById (R.id.rlMake);
//        tvMakeRequired = (TextView) view.findViewById (tvMakeRequired);
        input_layout_make = (TextInputLayout) view.findViewById (R.id.input_layout_make);
        etMake = (EditText) view.findViewById (R.id.etMake);

        rlSerial = (RelativeLayout) view.findViewById (R.id.rlSerial);
//        tvSerialRequired = (TextView) view.findViewById (tvSerialRequired);
        input_layout_serial = (TextInputLayout) view.findViewById (R.id.input_layout_serial);
        etSerial = (EditText) view.findViewById (R.id.etSerial);

        rlOtherImages = (RelativeLayout) view.findViewById (R.id.rlOtherImages);
        tvOtherImageAdd = (TextView) view.findViewById (R.id.tvOtherImageAdd);
        llOtherImages = (LinearLayout) view.findViewById (R.id.llOtherImages);

    }

    private void initListener () {
        etComments.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (! question.getQuestion_type ().equalsIgnoreCase ("Hybrid")) {
                    if (question.isComment_required () && s.length () > 0) {
                        input_layout_comment.setError (null);
                        input_layout_comment.setErrorEnabled (false);
//                        etComments.setError (null);
                    } else if (question.isComment_required () && s.length () == 0 && optionSelected.equalsIgnoreCase (question.getComment_required_for ())) {
//                        etComments.setError (question.getComment_hint ());
                        input_layout_comment.setError (question.getComment_hint ());
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
//                            etComments.setError (question.getComment_hint ());
                            input_layout_comment.setError (question.getComment_hint ());
                        } else {
                            input_layout_comment.setError (null);
                            input_layout_comment.setErrorEnabled (false);
//                            etComments.setError (null);
                        }

                        if (question.isFirst_ct_question ()) {
                            switch (optionSelected) {
                                case "N/A":
                                    question.setImage_required (false);
                                    tvImageRequired.setVisibility (View.GONE);
                                    break;
                                case "Yes":
                                    Utils.showOkDialog (getActivity (), "Please take a full image of the CT", false);
                                    question.setImage_required (true);
                                    tvImageRequired.setVisibility (View.VISIBLE);
                                    break;
                                case "No":
                                    Utils.showOkDialog (getActivity (), "Please take image of empty lobby", false);
                                    question.setImage_required (true);
                                    tvImageRequired.setVisibility (View.VISIBLE);
                                    break;
                                default:
                                    question.setImage_required (true);
                                    tvImageRequired.setVisibility (View.VISIBLE);
                                    break;

                            }
                        }

                        if (question.getQuestion_id () == 13) {
                            if (optionSelected.equalsIgnoreCase ("Yes")) {
                                question.setImage_required (true);
                                tvImageRequired.setVisibility (View.VISIBLE);
                            } else {
                                question.setImage_required (false);
                                tvImageRequired.setVisibility (View.GONE);
                            }
                        }

                        if (question.getQuestion_id () == 29) {
                            if (! optionSelected.equalsIgnoreCase ("N/A")) {
                                question.setImage_required (true);
                                tvImageRequired.setVisibility (View.VISIBLE);
                            } else {
                                question.setImage_required (false);
                                tvImageRequired.setVisibility (View.GONE);
                            }
                        }


                        if (question.isMake_serial_type ()) {
                            if (optionSelected.equalsIgnoreCase (question.getMandatory_comment_not_for ())) {
                                input_layout_serial.setErrorEnabled (false);
                                input_layout_make.setErrorEnabled (false);
                                input_layout_serial.setError (null);
                                input_layout_make.setError (null);
//                                tvSerialRequired.setVisibility (View.GONE);
//                                tvMakeRequired.setVisibility (View.GONE);
                            } else {
                                input_layout_serial.setErrorEnabled (true);
                                input_layout_make.setErrorEnabled (true);
                                input_layout_serial.setError ("Please Specify");
                                input_layout_make.setError ("Please Specify");
//                                tvSerialRequired.setVisibility (View.VISIBLE);
//                                tvMakeRequired.setVisibility (View.VISIBLE);
                            }
                        }

//                        if (question.isImage_required ()) {
//                            if (optionSelected.equalsIgnoreCase (question.getImage_required_for ())) {
//                                question.setImage_required (true);
//                                tvImageRequired.setVisibility (View.VISIBLE);
//                            } else {
//                                question.setImage_required (false);
//                                tvImageRequired.setVisibility (View.GONE);
//                            }
//                        }
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
                    input_layout_comment.setError (null);
                    input_layout_comment.setErrorEnabled (false);
//                    etComments.setError (null);
                    final String image = Utils.bitmapToBase64 (bp1);
                    Utils.showLog (Log.DEBUG, AppConfigTags.PAGE_NUMBER, "" + page, true);

                    Response response = new Response ();
                    response.setQuestion_id (question.getQuestion_id ());
                    response.setQuestion (question.getQuestion ());
                    response.setQuestion_type (question.getQuestion_type ());
                    response.setResponse_text (optionSelected);
                    if (page == 0) {
                        response.setComment (etComments.getText ().toString () + " " + Constants.atm_location_in_manual);
                    } else if (question.isMake_serial_type ()) {
                        response.setComment (etComments.getText ().toString () + " - Make: " + etMake.getText ().toString () + " - Sr Number: " + etSerial.getText ().toString ());
                    } else {
                        response.setComment (etComments.getText ().toString ());
                    }

                    response.setImage_str (image);
                    response.setExtra_response_text (extra_option_response);
                    Constants.responseList.add (page, response);

                    Utils.showLog (Log.DEBUG, "KARMAN " + "question_id", "" + question.getQuestion_id (), true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "question_name", "" + question.getQuestion (), true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "response_text", optionSelected, true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "question_type", question.getQuestion_type (), true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "comment", etComments.getText ().toString (), true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "image", image, true);
                    Utils.showLog (Log.DEBUG, "KARMAN " + "extra_options", extra_option_response, true);


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

                        JSONArray jsonArrayOtherImages = new JSONArray ();
                        try {
                            for (int i = 0; i < otherImagesList.size (); i++) {
                                JSONObject jsonObject = new JSONObject ();
                                jsonObject.put ("image", otherImagesList.get (i));
//                                jsonObject.put ("image", "helo");
                                jsonArrayOtherImages.put (jsonObject);
                            }
                        } catch (JSONException e) {
                            Utils.showLog (Log.ERROR, "JSON EXCEPTION", e.getMessage (), true);
                        }
                        Constants.report.setOther_images_json (String.valueOf (jsonArrayOtherImages));
                        Constants.report.setResponses_json_array (String.valueOf (jsonArray));
                        showSignatureDialog ();
                    } else {
                        ViewPagerActivity.nextPage ();
                    }
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

        tvOtherImageAdd.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (otherImagesList.size () < 5) {
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
                        startActivityForResult (mIntent, OTHER_CAMERA_ACTIVITY);
                } else {
                    Utils.showOkDialog (getActivity (), "Sorry you can add atmost 5 images", false);
                }
            }
        });

        etMake.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                input_layout_make.setError (null);
                input_layout_make.setErrorEnabled (false);
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
        etSerial.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                input_layout_serial.setError (null);
                input_layout_serial.setErrorEnabled (false);
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
        etComments.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                input_layout_comment.setError (null);
                input_layout_comment.setErrorEnabled (false);
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
    }

    private boolean validate () {
        boolean validate = true;
        List<String> error = new ArrayList<> ();
        error.clear ();
        switch (question.getQuestion_type ()) {
            case "Radio":
                if (optionSelected.length () == 0) {
                    error.add ("Select an option");
                    validate = false;
                }
                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0) {
                    error.add ("Select an image");
                    validate = false;
                }
                if (question.isComment_required () && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () == 0) {
                    error.add ("Enter the value in comment");
                    validate = false;
                }
                if (question.getQuestion_id () == 14 && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () < 6) {
                    error.add ("Enter a valid number (Atleast 6 digit)");
                    validate = false;
                }
//                if (question.getQuestion_id () == 19 && etComments.getText ().toString ().length () == 0) {
//                    error.add ("Enter AC Make in comments");
//                    validate = false;
//                }
//                if (question.getQuestion_id () == 20 && etComments.getText ().toString ().length () == 0) {
//                    error.add ("Enter UPS Make and Sr Number in comments");
//                    validate = false;
//                }


                if (question.isMake_serial_type () && ! question.getMandatory_comment_not_for ().equalsIgnoreCase (optionSelected)) {
                    if (etMake.getText ().toString ().length () == 0) {
                        error.add ("Please specify make in comments");
                        validate = false;
                    }
                    if (etSerial.getText ().toString ().length () == 0) {
                        error.add ("Please specify serial in comments");
                        validate = false;
                    }

                }


                if (error.size () > 0) {
                    Utils.showValidationErrorDialog (getActivity (), error);
                }
                break;
            case "Hybrid":
                if (optionSelected.length () == 0) {
                    error.add ("Select an option");
                    validate = false;
                }
                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0) {
                    error.add ("Select an image");
                    validate = false;
                }
                if (question.isComment_required () && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () == 0) {
                    error.add ("Enter the value in comment");
                    validate = false;
                }
                if (optionSelected.equalsIgnoreCase (question.getExtra_option_required_for ()) && extraOptions.size () == 0) {
                    error.add ("Select atleast one value");
                    validate = false;
                }
                if (error.size () > 0) {
                    Utils.showValidationErrorDialog (getActivity (), error);
                }
                break;
            case "Blank":
                if (question.isComment_required () && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () == 0) {
                    error.add ("Enter the value in comment");
                    validate = false;
                }
                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0) {
                    error.add ("Select an image");
                    validate = false;
                }
                if (error.size () > 0) {
                    Utils.showValidationErrorDialog (getActivity (), error);
                }
                break;
            case "Rating":
                if (question.isComment_required () && question.getComment_required_for ().equalsIgnoreCase (optionSelected) && etComments.getText ().toString ().length () == 0) {
                    error.add ("Enter the value in comment");
                    validate = false;
                }
                if (question.isImage_required () && Utils.bitmapToBase64 (bp1).length () == 0) {
                    error.add ("Select an Image");
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
//                pDialog = new ProgressDialog (getActivity ());
//                Utils.showProgressDialog (pDialog, null);
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
//                                pDialog.dismiss ();
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    switch (jsonObj.getInt (AppConfigTags.STATUS)) {
                                        case 0:
                                            db.createReport (report);
//                                            pDialog.dismiss ();
//                                            Utils.showOkDialog (getActivity (), "Some error occurred your responses have been saved offline and will be uploaded later", false);
                                            Utils.showLog (Log.INFO, "RESPONSE LOG", "Some error occurred your responses have been saved offline and will be uploaded later", true);
                                            break;
                                        case 1:
//                                            pDialog.dismiss ();
//                                            Utils.showOkDialog (getActivity (), "Your responses have been uploaded successfully to the server", false);
                                            Utils.showLog (Log.INFO, "RESPONSE LOG", "Your responses have been uploaded successfully to the server", true);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    db.createReport (report);
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
//                            pDialog.dismiss ();
//                            Utils.showOkDialog (getActivity (), "Seems like there is an issue with the internet connection," +
//                                    " your responses have been saved and will be uploaded once you are online", false);
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
                    params.put (AppConfigTags.OTHER_IMAGES, report.getOther_images_json ());

                    Log.e (AppConfigTags.ATM_ID, String.valueOf (report.getAtm_id ()));
                    Log.e (AppConfigTags.ATM_UNIQUE_ID, report.getAtm_unique_id ());
                    Log.e (AppConfigTags.ATM_AGENCY_ID, String.valueOf (report.getAgency_id ()));
                    Log.e (AppConfigTags.AUDITOR_ID, String.valueOf (report.getAuditor_id ()));
                    Log.e (AppConfigTags.ISSUES, report.getResponses_json_array ());
//                        Log.e (AppConfigTags.GEO_IMAGE, finalReport.getGeo_image_string ());
                    Log.e (AppConfigTags.LATITUDE, report.getLatitude ());
                    Log.e (AppConfigTags.LONGITUDE, report.getLongitude ());
                    Log.e (AppConfigTags.OTHER_IMAGES, report.getOther_images_json ());
//                        Log.e (AppConfigTags.SIGN_IMAGE, finalReport.getSignature_image_string ());


//                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 100);
            Utils.showOkDialog (getActivity (), "Your responses have been saved" +
                    " and will be uploaded in the background", true);
        } else {
//            pDialog.dismiss ();
            Utils.showOkDialog (getActivity (), "Seems like there is no internet connection, your responses have been saved" +
                    " and will be uploaded once you are online", true);
            db.createReport (report);
        }
    }
}