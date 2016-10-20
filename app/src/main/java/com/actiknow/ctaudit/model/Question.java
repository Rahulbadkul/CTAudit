package com.actiknow.ctaudit.model;

import android.util.Log;

import com.actiknow.ctaudit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private boolean image_required, comment_required, ct_question, extra_options_present, numeric_comment, auto_time, first_ct_question, make_serial_type;
    private int question_id;
    private String question, question_type, comment_required_for, extra_option_required_for, comment_hint, image_required_for, mandatory_comment_not_for;

    private List<String> options = new ArrayList<> ();
    private List<String> extra_options = new ArrayList<> ();


    public Question () {
    }

    public Question (int question_id, String question, String question_type, String comment_required_for, String extra_option_required_for, String comment_hint, String image_required_for, String mandatory_comment_not_for,
                     boolean image_required, boolean comment_required, boolean ct_question, boolean extra_options_present, boolean numeric_comment, boolean auto_time, boolean first_ct_question, boolean make_serial_type,
                     List<String> options, List<String> extra_options) {
        this.question_id = question_id;
        this.question = question;
        this.question_type = question_type;
        this.comment_required_for = comment_required_for;
        this.comment_hint = comment_hint;
        this.extra_option_required_for = extra_option_required_for;
        this.mandatory_comment_not_for = mandatory_comment_not_for;
        this.image_required_for = image_required_for;
        this.image_required = image_required;
        this.comment_required = comment_required;
        this.ct_question = ct_question;
        this.extra_options_present = extra_options_present;
        this.numeric_comment = numeric_comment;
        this.auto_time = auto_time;
        this.first_ct_question = first_ct_question;
        this.make_serial_type = make_serial_type;
        this.options = options;
        this.extra_options = extra_options;
    }

    public int getQuestion_id () {
        return question_id;
    }

    public void setQuestion_id (int question_id) {
        this.question_id = question_id;
        Utils.showLog (Log.DEBUG, "question_id", "" + question_id, false);
    }

    public String getQuestion () {
        return question;
    }

    public void setQuestion (String question) {
        this.question = question;
        Utils.showLog (Log.DEBUG, "question", question, false);
    }

    public String getComment_required_for () {
        return comment_required_for;
    }

    public void setComment_required_for (String comment_required_for) {
        this.comment_required_for = comment_required_for;
    }

    public boolean isNumeric_comment () {
        return numeric_comment;
    }

    public void setNumeric_comment (boolean numeric_comment) {
        this.numeric_comment = numeric_comment;
    }

    public String getComment_hint () {
        return comment_hint;
    }

    public void setComment_hint (String comment_hint) {
        this.comment_hint = comment_hint;
    }

    public String getImage_required_for () {
        return image_required_for;
    }

    public void setImage_required_for (String image_required_for) {
        this.image_required_for = image_required_for;
    }

    public String getExtra_option_required_for () {
        return extra_option_required_for;
    }

    public void setExtra_option_required_for (String extra_option_required_for) {
        this.extra_option_required_for = extra_option_required_for;
    }

    public String getMandatory_comment_not_for () {
        return mandatory_comment_not_for;
    }

    public void setMandatory_comment_not_for (String mandatory_comment_not_for) {
        this.mandatory_comment_not_for = mandatory_comment_not_for;
    }

    public boolean isImage_required () {
        return image_required;
    }

    public void setImage_required (boolean image_required) {
        this.image_required = image_required;
    }

    public boolean isComment_required () {
        return comment_required;
    }

    public void setComment_required (boolean comment_required) {
        this.comment_required = comment_required;
    }

    public boolean isCt_question () {
        return ct_question;
    }

    public void setCt_question (boolean ct_question) {
        this.ct_question = ct_question;
    }

    public boolean isExtra_options_present () {
        return extra_options_present;
    }

    public void setExtra_options_present (boolean extra_options_present) {
        this.extra_options_present = extra_options_present;
    }

    public String getQuestion_type () {
        return question_type;
    }

    public void setQuestion_type (String question_type) {
        this.question_type = question_type;
    }

    public boolean isAuto_time () {
        return auto_time;
    }

    public void setAuto_time (boolean auto_time) {
        this.auto_time = auto_time;
    }

    public boolean isFirst_ct_question () {
        return first_ct_question;
    }

    public void setFirst_ct_question (boolean first_ct_question) {
        this.first_ct_question = first_ct_question;
    }

    public boolean isMake_serial_type () {
        return make_serial_type;
    }

    public void setMake_serial_type (boolean make_serial_type) {
        this.make_serial_type = make_serial_type;
    }

    public List<String> getOptions () {
        return options;
    }

    public void setOptions (List<String> options) {
        this.options = options;
    }

    public void setOptionInList (String option) {
        this.options.add (option);
    }

    public List<String> getExtra_options () {
        return extra_options;
    }

    public void setExtra_options (List<String> extra_options) {
        this.extra_options = extra_options;
    }

    public void setExtra_optionInList (String extra_option) {
        this.extra_options.add (extra_option);
    }
}

