package com.example.norph.laphysiqueentroisime.model;

public class Chapter {

    public String sectionName;
    public String title;
    public boolean isFavoris;
    public String url;

    public Chapter(String sectionName, String title, boolean isFavoris, String url){
        this.title = title;
        this.isFavoris = isFavoris;
        this.url = url;
        this.sectionName = sectionName;
    }
}
