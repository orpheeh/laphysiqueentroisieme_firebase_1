package com.example.norph.laphysiqueentroisime.model;

public class QCM {

    public String question;

    public int response;

    public String[] propositions;

    public QCM(String question, int response, String[] propositions){
        this.question = question;
        this.response = response;
        this.propositions = propositions;
    }
}
