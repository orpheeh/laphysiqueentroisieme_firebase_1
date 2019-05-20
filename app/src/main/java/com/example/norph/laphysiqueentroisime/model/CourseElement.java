package com.example.norph.laphysiqueentroisime.model;

public class CourseElement {

    public static final String TYPE_TEXT = "text";
    public static final  String TYPE_IMAGE = "image";
    public static final  String TYPE_TITLE = "title";

    public String text;
    public String text2;
    public String type;

    public CourseElement(String text, String type, String text2){
        this.text = text;
        this.text2 = text2;
        this.type = type;
    }
}
