package com.example.norph.laphysiqueentroisime.model;

import java.util.List;

public class Slide {

    public String title;

    public String subtitle;

    public String id;

    public List<CourseElement> contents;

    public Slide(String title, String subtitle, String id, List<CourseElement> content){
        this.title = title;
        this.id = id;
        this.contents = content;
        this.subtitle = subtitle;
    }
}
