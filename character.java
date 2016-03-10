package com.example.android.mycomics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//test
/**
 * Created by Scott on 3/7/2016.
 */

public class character implements Serializable {
    private String characterName;
    private byte[] byteArray = null;

    public character(String name){
        characterName= name;
    }

    public String getCharacterName(){
        return characterName;
    }
    public Bitmap getImage(){
        if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        else{return null;}
    }
    public void setImage(Bitmap b){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
    }
}

