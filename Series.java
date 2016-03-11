package com.example.android.mycomics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Scott on 3/7/2016.
 */
public class Series implements Serializable {
    private String seriesName;
    private byte[] byteArray = null;//For image associated with character. Bitmap not serializable so must convert from bitmap to byteArray.
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
    public Bitmap getImage(){//Returns our image as a bitmap
        if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        else{return null;}
    }
    //Stores bitmap to byteArray.
    public void setImage(Bitmap b){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
    }
    public void sortIssues(){
        Collections.sort(issues);
    }
}
