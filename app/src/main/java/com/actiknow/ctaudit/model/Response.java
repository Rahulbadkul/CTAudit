package com.actiknow.ctaudit.model;

public class Response {
    private int response_id, question_id;
    private String question, question_type, response_text, extra_response_text, comment, image_str;

    public Response () {
    }

    public Response (int response_id, int question_id, String question, String question_type,
                     String response_text, String extra_response_text, String comment, String image_str) {
        this.response_id = response_id;
        this.question_id = question_id;
        this.question = question;
        this.question_type = question_type;
        this.response_text = response_text;
        this.extra_response_text = extra_response_text;
        this.comment = comment;
        this.image_str = image_str;
    }

    public int getResponse_id () {
        return response_id;
    }

    public void setResponse_id (int response_id) {
        this.response_id = response_id;
    }

    public int getQuestion_id () {
        return question_id;
    }

    public void setQuestion_id (int question_id) {
        this.question_id = question_id;
    }

    public String getQuestion () {
        return question;
    }

    public void setQuestion (String question) {
        this.question = question;
    }

    public String getQuestion_type () {
        return question_type;
    }

    public void setQuestion_type (String question_type) {
        this.question_type = question_type;
    }

    public String getResponse_text () {
        return response_text;
    }

    public void setResponse_text (String response_text) {
        this.response_text = response_text;
    }

    public String getExtra_response_text () {
        return extra_response_text;
    }

    public void setExtra_response_text (String extra_response_text) {
        this.extra_response_text = extra_response_text;
    }

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
    }

    public String getImage_str () {
        return image_str;
    }

    public void setImage_str (String image_str) {
        this.image_str = image_str;
    }
}
