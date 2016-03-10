package com.example.android.mycomics;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.SeriesActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int TextViewCount = 0;
    //Default list for testing.
    ArrayList<String> characterNames = new ArrayList<>();
    //Arrays.asList("Batman", "Superman", "Spider-Man", "Wonder Woman")
    List<character> characters = new ArrayList<>();
    int userImage = 1;
    ImageView currentUserEdit;
    Context context = this;
    String nextDisplayName = "";
    int comicHeightInPx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Gets height using dp
        comicHeightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String charsString = prefs.getString(prefKey, "");//Gets characters from memory.
        //If we have never saved anything before.
        if (charsString != "") {
            characterNames = listFromString(charsString);
            for(int i = 0;i<characterNames.size();i++){
                character myChar = new character(characterNames.get(i));
                characters.add(myChar);
            }
        }
        */
        try{
            readCharacters();
        }catch(Exception e){Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show();}
        for(int i = 0; i< characters.size();i++){
            String charactername = characters.get(i).getCharacterName();
            characterNames.add(charactername);
        }
        displayNames();
        for(int i = 0;i<characters.size();i++){
            if(characters.get(i).getImage() !=null){
                ImageView mImageView = (ImageView) findViewById(i);
                mImageView.setImageBitmap(characters.get(i).getImage());
            }
        }
    }

    public void nextActivity(String characterName) {
        Intent intent = new Intent(this, SeriesActivity.class);
        intent.putExtra("currentCharName",characterName);
        startActivity(intent);
    }

    public void addCharacterView() {
        //Creates the Character View that holds picture and name
        LinearLayout parentLinearLayout = (LinearLayout) findViewById(R.id.parentLinear);
        LinearLayout childLinearLayout = new LinearLayout(this);
        childLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        //The image
        ImageView comic = new ImageView(this);
        comic.setId(TextViewCount);
        comic.setLayoutParams(new LinearLayout.LayoutParams(
                0, comicHeightInPx, 2.25f));
        comic.setImageResource(R.drawable.addimage);
        childLinearLayout.addView(comic);
        comic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentUserEdit = (ImageView) v;
                userSetImage();
            }
        });
        //The name
        TextView characterTextView = new TextView(this);
        characterTextView.setText(nextDisplayName);
        characterTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 8f));
        characterTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView currentTextView = (TextView) v;
                String charName = currentTextView.getText().toString();
                nextActivity(charName);
            }
        });
        characterTextView.setTextSize(28);
        childLinearLayout.addView(characterTextView);
        parentLinearLayout.addView(childLinearLayout);
        TextViewCount++;
    }

    //Creates dialoge for adding a character
    public void characterDialog(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final EditText txtInput = new EditText(this);
        dialogBuilder.setTitle("Add Character:");
        dialogBuilder.setView(txtInput);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nextDisplayName = txtInput.getText().toString();
                addCharacterView();
                characterNames.add(nextDisplayName);
                characters.add(new character(nextDisplayName));
                saveCharacters(context);
            }
        });
        AlertDialog dialogCharacterName = dialogBuilder.create();
        dialogCharacterName.show();
    }

    //Creates a characterView if we allready know the name
    public void addCharacterView(String characterName) {
        nextDisplayName = characterName;
        addCharacterView();
    }

    //Deletes the most recent characterview
    public void removeTextView(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.parentLinear);
        if (TextViewCount != 0) {
            LinearLayout toRemove = (LinearLayout) findViewById(TextViewCount - 1);
            linearLayout.removeView(toRemove);
            characterNames.remove(TextViewCount - 1);
            characters.remove(TextViewCount -1);
            saveCharacters(this);
            TextViewCount--;
        }
    }

    //Saves our characters to prefrences
    /*private void saveCharacters(Context context) {
        SharedPreferences myPrefs;
        SharedPreferences.Editor edit;
        myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        edit = myPrefs.edit();
        String string = listToString(characterNames);
        edit.putString(prefKey, string);
        edit.commit();
    }*/
    private void saveCharacters(Context context){
        String filePath = context.getFilesDir().getPath().toString() + "/saveFile.txt";
        File f = new File(filePath);
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try{
            fos = new FileOutputStream(f);
            out = new ObjectOutputStream(fos);
            out.writeObject(characters);
            out.close();
        }
        catch(Exception e){Toast.makeText(MainActivity.this,
                "ERROR", Toast.LENGTH_LONG).show();
                Log.d("MYERROR",e.toString());}
    }

    private void displayNames() {
        for (int i = 0; i < characterNames.size(); i++) {
            addCharacterView(characterNames.get(i));
        }
    }

    private void userSetImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, userImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        try {
            // When an Image is picked
            if (resultCode == RESULT_OK && null != returnedIntent) {
                // Get the Image from data

                Uri imageLocation = returnedIntent.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageLocation);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                currentUserEdit.setImageBitmap(selectedImage);
                characters.get(currentUserEdit.getId()).setImage(selectedImage);
                saveCharacters(context);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG)
                    .show();
            Log.d("ImageGet ERROR", e.toString());
        }

    }

    protected void readCharacters() {
        try {
            String filePath = context.getFilesDir().getPath().toString() + "/saveFile.txt";
            File f = new File(filePath);
            FileInputStream fis = null;
            ObjectInputStream in = null;
            fis = new FileInputStream(f);
            in = new ObjectInputStream(fis);
            characters = (List<character>) in.readObject();
            in.close();
        }
        catch(Exception e){}
    }


}
