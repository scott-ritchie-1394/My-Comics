package com.example.android.mycomics;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Scott on 3/7/2016.
 */
public class Series implements Serializable {
    private String seriesName;
    private ArrayList<Double> issues = new ArrayList<>();


    public Series(String name){
        seriesName= name;
    }

    public String getSeriesName(){
        return seriesName;
    }

    public void addIssue(double issue){
        issues.add(issue);
    }

    public ArrayList<Double> getIssueArray(){
        return issues;
    }
}
