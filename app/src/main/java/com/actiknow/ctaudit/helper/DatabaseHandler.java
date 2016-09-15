package com.actiknow.ctaudit.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.actiknow.ctaudit.model.Atm;
import com.actiknow.ctaudit.model.AuditorLocation;
import com.actiknow.ctaudit.model.Question;
import com.actiknow.ctaudit.model.Report;
import com.actiknow.ctaudit.utils.AppConfigTags;
import com.actiknow.ctaudit.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ctAudit";

    // Table Names
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_ATMS = "atms";
    private static final String TABLE_REPORT = "report";
    private static final String TABLE_AUDITOR_LOCATION = "geo_location";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // QUESTIONS Table - column names
    private static final String KEY_QUESTION = "question";

    // ATMS Table - column names
    private static final String KEY_ATM_ID = "atm_id";
    private static final String KEY_ATM_UNIQUE_ID = "atm_unique_id";
    private static final String KEY_AGENCY_ID = "agency_id";
    private static final String KEY_LAST_AUDIT_DATE = "last_audit_date";
    private static final String KEY_BANK_NAME = "bank_name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CITY = "city";
    private static final String KEY_PINCODE = "pincode";

    // REPORT Table - column names
    private static final String KEY_AUDITOR_ID = "auditor_id";
    private static final String KEY_ISSUES_JSON = "issues_json";
    private static final String KEY_SIGN_IMAGE = "sign_image";
    private static final String KEY_RATING = "rating";
    private static final String KEY_GEO_IMAGE = "geo_image";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";


    // AUDITOR_LOCATION Table - column names
    private static final String KEY_TIME = "time";
    private static final String KEY_AUDITOR_LOCATION_ID = "auditor_location_id";

    // Question table Create Statements
    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
            + TABLE_QUESTIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_QUESTION
            + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // ATM table create statement
    private static final String CREATE_TABLE_ATMS = "CREATE TABLE " + TABLE_ATMS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AGENCY_ID + " INTEGER," + KEY_ATM_ID + " INTEGER," + KEY_ATM_UNIQUE_ID + " TEXT,"
            + KEY_LAST_AUDIT_DATE + " TEXT," + KEY_BANK_NAME + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_CITY + " TEXT,"
            + KEY_PINCODE + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // Report table create statement
    private static final String CREATE_TABLE_REPORT = "CREATE TABLE " + TABLE_REPORT
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ATM_ID + " INTEGER," + KEY_ATM_UNIQUE_ID + " TEXT," + KEY_AGENCY_ID + " INTEGER,"
            + KEY_AUDITOR_ID + " INTEGER," + KEY_ISSUES_JSON + " BLOB," +
            KEY_GEO_IMAGE + " BLOB," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," +
            KEY_SIGN_IMAGE + " BLOB," + KEY_TIME + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // Auditor location table create statement
    private static final String CREATE_TABLE_AUDITOR_LOCATION = "CREATE TABLE " + TABLE_AUDITOR_LOCATION
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_AUDITOR_ID + " INTEGER," +
            KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_TIME + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    public DatabaseHandler (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL (CREATE_TABLE_QUESTIONS);
        db.execSQL (CREATE_TABLE_ATMS);
        db.execSQL (CREATE_TABLE_REPORT);
        db.execSQL (CREATE_TABLE_AUDITOR_LOCATION);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_ATMS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_REPORT);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_AUDITOR_LOCATION);
        onCreate (db);
    }

    // ------------------------ "questions" table methods ----------------//

    public long createQuestion (Question question) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Question", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_ID, question.getQuestion_id ());
        values.put (KEY_QUESTION, question.getQuestion ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long question_id = db.insert (TABLE_QUESTIONS, null, values);
        return question_id;
    }

    public Question getQuestion (long question_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_ID + " = " + question_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Question where ID = " + question_id, false);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Question question = new Question ();
        question.setQuestion_id (c.getInt (c.getColumnIndex (KEY_ID)));
        question.setQuestion ((c.getString (c.getColumnIndex (KEY_QUESTION))));
        return question;
    }

    public List<Question> getAllQuestions () {
        List<Question> questions = new ArrayList<Question> ();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all questions", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Question question = new Question ();
                question.setQuestion_id (c.getInt ((c.getColumnIndex (KEY_ID))));
                question.setQuestion ((c.getString (c.getColumnIndex (KEY_QUESTION))));
                questions.add (question);
            } while (c.moveToNext ());
        }
        return questions;
    }

    public int getQuestionCount () {
        String countQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total questions count : " + count, false);
        return count;
    }

    public int updateQuestion (Question question) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update questions", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_QUESTION, question.getQuestion ());
        // updating row
        return db.update (TABLE_QUESTIONS, values, KEY_ID + " = ?", new String[] {String.valueOf (question.getQuestion_id ())});
    }

    public void deleteQuestion (long question_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete question where ID = " + question_id, false);
        db.delete (TABLE_QUESTIONS, KEY_ID + " = ?",
                new String[] {String.valueOf (question_id)});
    }

    public void deleteAllQuestion () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all questions", false);
        db.execSQL ("delete from " + TABLE_QUESTIONS);
    }


    // ------------------------ "atms" table methods ----------------//

    public long createAtm (Atm atm) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Atm", false);
        values.put (KEY_ID, atm.getAtm_id ());
        values.put (KEY_AGENCY_ID, atm.getAtm_agency_id ());
        values.put (KEY_ATM_ID, atm.getAtm_id ());
        values.put (KEY_ATM_UNIQUE_ID, atm.getAtm_unique_id ());
        values.put (KEY_LAST_AUDIT_DATE, atm.getAtm_last_audit_date ());
        values.put (KEY_BANK_NAME, atm.getAtm_bank_name ());
        values.put (KEY_ADDRESS, atm.getAtm_address ());
        values.put (KEY_CITY, atm.getAtm_city ());
        values.put (KEY_PINCODE, atm.getAtm_pincode ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long atm_id = db.insert (TABLE_ATMS, null, values);
        return atm_id;
    }

    public Atm getAtm (long atm_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_ATMS + " WHERE " + KEY_ID + " = " + atm_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Atm where ID = " + atm_id, false);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Atm atm = new Atm ();
        atm.setAtm_id (c.getInt (c.getColumnIndex (KEY_ID)));
        atm.setAtm_agency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
        atm.setAtm_id (c.getInt (c.getColumnIndex (KEY_ATM_ID)));
        atm.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_UNIQUE_ID)));
        atm.setAtm_last_audit_date (c.getString (c.getColumnIndex (KEY_LAST_AUDIT_DATE)));
        atm.setAtm_bank_name (c.getString (c.getColumnIndex (KEY_BANK_NAME)));
        atm.setAtm_address (c.getString (c.getColumnIndex (KEY_ADDRESS)));
        atm.setAtm_city (c.getString (c.getColumnIndex (KEY_CITY)));
        atm.setAtm_pincode (c.getString (c.getColumnIndex (KEY_PINCODE)));
        return atm;
    }

    public List<Atm> getAllAtms () {
        List<Atm> atms = new ArrayList<Atm> ();
        String selectQuery = "SELECT  * FROM " + TABLE_ATMS;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all atm", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Atm atm = new Atm ();
                atm.setAtm_agency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
                atm.setAtm_id (c.getInt (c.getColumnIndex (KEY_ATM_ID)));
                atm.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_UNIQUE_ID)));
                atm.setAtm_last_audit_date (c.getString (c.getColumnIndex (KEY_LAST_AUDIT_DATE)));
                atm.setAtm_bank_name (c.getString (c.getColumnIndex (KEY_BANK_NAME)));
                atm.setAtm_address (c.getString (c.getColumnIndex (KEY_ADDRESS)));
                atm.setAtm_city (c.getString (c.getColumnIndex (KEY_CITY)));
                atm.setAtm_pincode (c.getString (c.getColumnIndex (KEY_PINCODE)));
                atms.add (atm);
            } while (c.moveToNext ());
        }
        return atms;
    }

    public int getAtmCount () {
        String countQuery = "SELECT  * FROM " + TABLE_ATMS;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total atm count : " + count, false);
        return count;
    }

    public int updateAtm (Atm atm) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update atm", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_ATM_ID, atm.getAtm_id ());
        values.put (KEY_ATM_UNIQUE_ID, atm.getAtm_unique_id ());
        values.put (KEY_AGENCY_ID, atm.getAtm_agency_id ());
        values.put (KEY_LAST_AUDIT_DATE, atm.getAtm_last_audit_date ());
        values.put (KEY_BANK_NAME, atm.getAtm_bank_name ());
        values.put (KEY_ADDRESS, atm.getAtm_address ());
        values.put (KEY_CITY, atm.getAtm_city ());
        values.put (KEY_PINCODE, atm.getAtm_pincode ());
        // updating row
        return db.update (TABLE_ATMS, values, KEY_ID + " = ?", new String[] {String.valueOf (atm.getAtm_id ())});
    }

    public void deleteAtm (long atm_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete atm where ID = " + atm_id, false);
        db.delete (TABLE_ATMS, KEY_ID + " = ?", new String[] {String.valueOf (atm_id)});
    }

    public void deleteAllAtms () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all atm", false);
        db.execSQL ("delete from " + TABLE_ATMS);
    }

    // ------------------------ "reports" table methods ----------------//

    public long createReport (Report report) {

        try {

        } catch (Exception e) {
            e.printStackTrace ();
            Utils.showLog (Log.DEBUG, "EXCEPTION", e.getMessage (), true);
        }
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Report", false);
        values.put (KEY_AGENCY_ID, report.getAgency_id ());
        values.put (KEY_ATM_ID, report.getAtm_id ());
        values.put (KEY_ATM_UNIQUE_ID, report.getAtm_unique_id ());
        values.put (KEY_AUDITOR_ID, report.getAuditor_id ());
        values.put (KEY_ISSUES_JSON, report.getResponses_json_array ());
        values.put (KEY_GEO_IMAGE, report.getGeo_image_string ());
        values.put (KEY_LATITUDE, report.getLatitude ());
        values.put (KEY_LONGITUDE, report.getLongitude ());
        values.put (KEY_SIGN_IMAGE, report.getSignature_image_string ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long report_id = db.insert (TABLE_REPORT, null, values);
        return report_id;
    }

    public Report getReport (long report_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT " + KEY_ID + ", " + KEY_ATM_ID + ", " + KEY_AUDITOR_ID + ", " + KEY_AGENCY_ID + ", " + KEY_ATM_UNIQUE_ID + ", " +
                KEY_GEO_IMAGE + ", " + KEY_LATITUDE + ", " + KEY_LONGITUDE + ", " + KEY_SIGN_IMAGE + " FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
        String selectQuery2 = "SELECT length(" + KEY_ISSUES_JSON + ") as length FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
        String selectQuery3 = "SELECT " + KEY_ISSUES_JSON + " FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Report where ID = " + report_id, true);
//        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "query = " + selectQuery2, true);
        Cursor c = db.rawQuery (selectQuery, null);
        Cursor c2 = db.rawQuery (selectQuery2, null);
        Cursor c3 = db.rawQuery (selectQuery3, null);
        if (c != null)
            c.moveToFirst ();

        if (c2 != null)
            c2.moveToFirst ();

        if (c3 != null)
            c3.moveToFirst ();


        String issue_json = "";

        if (c2.getInt (c2.getColumnIndex ("length")) > 1000000) {
            int i = c2.getInt (c2.getColumnIndex ("length")) / 10000000;


            int j = 1;
            Log.e ("value of i", " i = " + i);
            for (int i2 = 0; i2 <= i; i2++) {
                String query;
                if (i2 == i) {
                    int j2 = c2.getInt (c2.getColumnIndex ("length")) - i * 10000000;
                    query = "SELECT substr (" + KEY_ISSUES_JSON + ", " + j + ", " + j2 + ") as str FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                } else {
                    query = "SELECT substr (" + KEY_ISSUES_JSON + ", " + j + ", 1000000) as str FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                }
                Log.e ("query", query);
                Cursor cursor = db.rawQuery (query, null);
                if (cursor != null)
                    cursor.moveToFirst ();
//                json_substr.add (cursor.getString (cursor.getColumnIndex ("str")));

                Utils.showLog (Log.DEBUG, "IN FOR LOOP", (i2 + 1) + " iteration", true);

                issue_json = issue_json.concat (cursor.getString (cursor.getColumnIndex ("str")));
                j = j + 1000000;
                Log.e ("SUB STRING", cursor.getString (cursor.getColumnIndex ("str")));
            }


            try {
                JSONArray jsonArray = new JSONArray (issue_json);
            } catch (JSONException e) {
                e.printStackTrace ();
            }


//            for(int i3=0;i<json_substr.size ();i3++){
//            }


            /*
            switch (i){
                case 1:




                    String selectQuery4 = "SELECT substr (" + KEY_ISSUES_JSON + ", 1, 1000000) as str1 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery5 = "SELECT substr (" + KEY_ISSUES_JSON + ", 1000001, 1000000) as str2 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    Cursor c4 = db.rawQuery (selectQuery4, null);
                    Cursor c5 = db.rawQuery (selectQuery5, null);
                    if (c4 != null)
                        c4.moveToFirst ();
                    if (c5 != null)
                        c5.moveToFirst ();

                    issue_json = c4.getString (c4.getColumnIndex ("str1")) + c5.getString (c5.getColumnIndex ("str2"));
                    break;
                case 2:
                    String selectQuery6 = "SELECT substr (" + KEY_ISSUES_JSON + ", 1, 1000000) as str1 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery7 = "SELECT substr (" + KEY_ISSUES_JSON + ", 1000001, 1000000) as str2 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery8 = "SELECT substr (" + KEY_ISSUES_JSON + ", 2000001, 1000000) as str3 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    Cursor c6 = db.rawQuery (selectQuery6, null);
                    Cursor c7 = db.rawQuery (selectQuery7, null);
                    Cursor c8 = db.rawQuery (selectQuery8, null);
                    if (c6 != null)
                        c6.moveToFirst ();
                    if (c7 != null)
                        c7.moveToFirst ();
                    if (c8 != null)
                        c8.moveToFirst ();

                    issue_json = c6.getString (c6.getColumnIndex ("str1")) + c7.getString (c7.getColumnIndex ("str2")) + c8.getString (c8.getColumnIndex ("str3"));

                    break;
                case 3:
                    String selectQuery9 = "SELECT substr (" + KEY_ISSUES_JSON + ", 1, 1000000) as str1 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery10 = "SELECT substr (" + KEY_ISSUES_JSON + ", 1000001, 1000000) as str2 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery11 = "SELECT substr (" + KEY_ISSUES_JSON + ", 2000001, 1000000) as str3 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery12 = "SELECT substr (" + KEY_ISSUES_JSON + ", 3000001, 1000000) as str4 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    Cursor c9 = db.rawQuery (selectQuery9, null);
                    Cursor c10 = db.rawQuery (selectQuery10, null);
                    Cursor c11 = db.rawQuery (selectQuery11, null);
                    Cursor c12 = db.rawQuery (selectQuery12, null);
                    if (c9 != null)
                        c9.moveToFirst ();
                    if (c10 != null)
                        c10.moveToFirst ();
                    if (c11 != null)
                        c11.moveToFirst ();
                    if (c12 != null)
                        c12.moveToFirst ();

                    issue_json = c9.getString (c9.getColumnIndex ("str1")) + c10.getString (c10.getColumnIndex ("str2")) + c11.getString (c11.getColumnIndex ("str3")) + c12.getString (c12.getColumnIndex ("str4"));
                    break;
                case 4:
                    String selectQuery13 = "SELECT substr (" + KEY_ISSUES_JSON + ", 1, 1000000) as str1 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery14 = "SELECT substr (" + KEY_ISSUES_JSON + ", 1000001, 1000000) as str2 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery15 = "SELECT substr (" + KEY_ISSUES_JSON + ", 2000001, 1000000) as str3 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery16 = "SELECT substr (" + KEY_ISSUES_JSON + ", 3000001, 1000000) as str4 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    String selectQuery17 = "SELECT substr (" + KEY_ISSUES_JSON + ", 4000001, 1000000) as str5 FROM " + TABLE_REPORT + " WHERE " + KEY_ID + " = " + report_id;
                    Cursor c13 = db.rawQuery (selectQuery13, null);
                    Cursor c14 = db.rawQuery (selectQuery14, null);
                    Cursor c15 = db.rawQuery (selectQuery15, null);
                    Cursor c16 = db.rawQuery (selectQuery16, null);
                    Cursor c17 = db.rawQuery (selectQuery17, null);
                    if (c13 != null)
                        c13.moveToFirst ();
                    if (c14 != null)
                        c14.moveToFirst ();
                    if (c15 != null)
                        c15.moveToFirst ();
                    if (c16 != null)
                        c16.moveToFirst ();
                    if (c17 != null)
                        c17.moveToFirst ();

                    issue_json = c13.getString (c13.getColumnIndex ("str1")) + c14.getString (c14.getColumnIndex ("str2")) + c15.getString (c15.getColumnIndex ("str3")) + c16.getString (c16.getColumnIndex ("str4")) + c17.getString (c16.getColumnIndex ("str5"));
                    break;
            }


            */


        } else {
            issue_json = c3.getString (c3.getColumnIndex (KEY_ISSUES_JSON));
        }


        Utils.showLog (Log.DEBUG, "DATABASE LOG", "length of blob" + c2.getInt (c2.getColumnIndex ("length")), true);

        Report report = new Report ();
        try {
            report.setReport_id (c.getInt (c.getColumnIndex (KEY_ID)));
            report.setAgency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
            report.setAtm_id (c.getInt (c.getColumnIndex (KEY_ATM_ID)));
            report.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_UNIQUE_ID)));
            report.setAuditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
//            report.setResponses_json_array (c3.getString (c3.getColumnIndex (KEY_ISSUES_JSON)));
            report.setResponses_json_array (issue_json);
            report.setGeo_image_string (c.getString (c.getColumnIndex (KEY_GEO_IMAGE)));
            report.setLatitude (c.getString (c.getColumnIndex (KEY_LATITUDE)));
            report.setLongitude (c.getString (c.getColumnIndex (KEY_LONGITUDE)));
            report.setSignature_image_string (c.getString (c.getColumnIndex (KEY_SIGN_IMAGE)));
        } catch (Exception e) {
            e.printStackTrace ();
            Utils.showLog (Log.DEBUG, "EXCEPTION", e.getMessage (), true);
        }
        return report;

    }

    public List<Report> getAllReports () {
        List<Report> reports = new ArrayList<Report> ();
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_REPORT;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all reports", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                try {
                    Report report = new Report ();
                    report = getReport (c.getInt (c.getColumnIndex (KEY_ID)));
                    reports.add (report);
                } catch (Exception e) {
                    e.printStackTrace ();
                    Utils.showLog (Log.DEBUG, "EXCEPTION", e.getMessage (), true);
                    // this gets called even if there is an exception somewhere above
//                    if (c2 != null)
//                        c2.close ();
                }
            } while (c.moveToNext ());
        }
        return reports;
    }

    public int getReportCount () {
        String countQuery = "SELECT  * FROM " + TABLE_REPORT;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total report count : " + count, false);
        return count;
    }

    public int updateReport (Report report) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update report", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_ATM_ID, report.getAtm_id ());
        values.put (KEY_ATM_UNIQUE_ID, report.getAtm_unique_id ());
        values.put (KEY_AGENCY_ID, report.getAgency_id ());
        values.put (KEY_AUDITOR_ID, report.getAuditor_id ());
        values.put (KEY_ISSUES_JSON, report.getResponses_json_array ());
        values.put (KEY_GEO_IMAGE, report.getGeo_image_string ());
        values.put (KEY_LATITUDE, report.getLatitude ());
        values.put (KEY_LONGITUDE, report.getLongitude ());
        values.put (KEY_SIGN_IMAGE, report.getSignature_image_string ());
        // updating row
        return db.update (TABLE_REPORT, values, KEY_ID + " = ?", new String[] {String.valueOf (report.getReport_id ())});
    }

    public void deleteReport (String geo_image_string) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete report where geo_image_string = " + geo_image_string, false);
        db.delete (TABLE_REPORT, KEY_GEO_IMAGE + " = ?", new String[] {geo_image_string});
    }

    public void deleteAllReports () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all reports", false);
        db.execSQL ("delete from " + TABLE_REPORT);

    }

    // ------------------------ "auditor location" table methods ----------------//

    public long createAuditorLocation (AuditorLocation auditorLocation) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating auditorlocation", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_AUDITOR_ID, auditorLocation.getAuditor_id ());
        values.put (KEY_LATITUDE, auditorLocation.getLatitude ());
        values.put (KEY_LONGITUDE, auditorLocation.getLongitude ());
        values.put (KEY_TIME, auditorLocation.getTime ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long auditor_location_id = db.insert (TABLE_AUDITOR_LOCATION, null, values);
        return auditor_location_id;
    }

    public AuditorLocation getauditorLocation (long auditor_location_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_AUDITOR_LOCATION + " WHERE " + KEY_ID + " = " + auditor_location_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get auditor location where ID = " + auditor_location_id, false);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        AuditorLocation auditorLocation = new AuditorLocation ();
        auditorLocation.setAuditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
        auditorLocation.setTime (c.getString (c.getColumnIndex (KEY_TIME)));
        auditorLocation.setLatitude (c.getString (c.getColumnIndex (KEY_LATITUDE)));
        auditorLocation.setLongitude (c.getString (c.getColumnIndex (KEY_LONGITUDE)));
        return auditorLocation;
    }

    public List<AuditorLocation> getAllAuditorLocation () {
        List<AuditorLocation> auditorLocations = new ArrayList<AuditorLocation> ();
        String selectQuery = "SELECT  * FROM " + TABLE_AUDITOR_LOCATION;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all Auditor Locations", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                AuditorLocation auditorLocation = new AuditorLocation ();
                auditorLocation.setAuditor_id (c.getInt (c.getColumnIndex (KEY_AUDITOR_ID)));
                auditorLocation.setTime (c.getString (c.getColumnIndex (KEY_TIME)));
                auditorLocation.setLatitude (c.getString (c.getColumnIndex (KEY_LATITUDE)));
                auditorLocation.setLongitude (c.getString (c.getColumnIndex (KEY_LONGITUDE)));
                auditorLocations.add (auditorLocation);
            } while (c.moveToNext ());
        }
        return auditorLocations;
    }

    public int getAuditorLocationCount () {
        String countQuery = "SELECT  * FROM " + TABLE_AUDITOR_LOCATION;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total auditor locations count : " + count, false);
        return count;
    }

    public int updateAuditorLocation (AuditorLocation auditorLocation) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update auditor location", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_AUDITOR_ID, auditorLocation.getAuditor_id ());
        values.put (KEY_GEO_IMAGE, auditorLocation.getTime ());
        values.put (KEY_LATITUDE, auditorLocation.getLatitude ());
        values.put (KEY_LONGITUDE, auditorLocation.getLongitude ());
        // updating row
        return db.update (TABLE_AUDITOR_LOCATION, values, KEY_ID + " = ?", new String[] {String.valueOf (auditorLocation.getAuditor_location_id ())});
    }

    public void deleteAuditorLocation (String time) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete auditor location where time = " + time, false);
        db.delete (TABLE_AUDITOR_LOCATION, KEY_TIME + " = ?",
                new String[] {time});
    }

    public void deleteAllAuditorLocation () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all auditor locations", false);
        db.execSQL ("delete from " + TABLE_AUDITOR_LOCATION);
    }


    public void closeDB () {
        SQLiteDatabase db = this.getReadableDatabase ();
        if (db != null && db.isOpen ())
            db.close ();
    }

    private String getDateTime () {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.getDefault ());
        Date date = new Date ();
        return dateFormat.format (date);
    }
}