package com.actiknow.ctaudit.utils;

import com.actiknow.ctaudit.model.Question;
import com.actiknow.ctaudit.model.Report;
import com.actiknow.ctaudit.model.Response;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static String auditor_name = "";
    public static String username = "";
    public static int auditor_id_main = 0;
    public static int auditor_agency_id = 0;
    public static double latitude = 0.0;
    public static double longitude = 0.0;

    public static int splash_screen_first_time = 0; // 0 => default

    public static List<Question> questionsList = new ArrayList<Question> ();
    public static List<Response> responseList = new ArrayList<Response> ();
    public static Report report = new Report ();

    public static String atm_location_in_manual = "";

    public static boolean show_log = true;

    public static String location_tagging_start_time = "08:00";
    public static String location_tagging_end_time = "20:00";

    public static int image_quality = 10; // 10
    public static int max_image_size = 320; // 320

    public static int first_ct_question = 0;

    public static String server_time = "";

}
