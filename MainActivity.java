package com.example.android.mycomics;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int TextViewCount = 0;//Keeps track of number of text views.
    ArrayList<String> characterNames = new ArrayList<>();//Holds character names to be displayed
    List<character> characters = new ArrayList<>();//Holds our characters

    int userImage = 1;//Used for user selected image.
    ImageView currentUserEdit;

    Context context = this;
    String nextDisplayName = "";//Used to hold String for new character as input by user
    int comicHeightInPx;//For dp conversion


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
            readCharacters();//Should build our array of Characters from file.
        }catch(Exception e){Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show();}
        for(int i = 0; i< characters.size();i++){//Creates the characterNames array from characters array.
            String charactername = characters.get(i).getCharacterName();
            characterNames.add(charactername);
        }
        displayNames();
        for(int i = 0;i<characters.size();i++){//Sets images previously selected by user.
            if(characters.get(i).getImage() !=null){
                ImageView mImageView = (ImageView) findViewById(i);
                mImageView.setImageBitmap(characters.get(i).getImage());
            }
        }
    }
    //Goes to our Series view.
    public void nextActivity(String characterName) {
        Intent intent = new Intent(this, SeriesActivity.class);
        intent.putExtra("currentCharName",characterName);//So we know what character we are dealing with
        startActivity(intent);
    }

    public void addCharacterView() {//Creates the Character View that holds picture and name
        //Horizontal layout for holding our Text and Imageview
        LinearLayout parentLinearLayout = (LinearLayout) findViewById(R.id.parentLinear);
        LinearLayout childLinearLayout = new LinearLayout(this);
        childLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //The image view
        ImageView comic = new ImageView(this);
        comic.setId(TextViewCount);//For retrieval to set Image later.
        comic.setLayoutParams(new LinearLayout.LayoutParams(0, comicHeightInPx, 2.25f));
        comic.setImageResource(R.drawable.addimage);//Default plus sign
        childLinearLayout.addView(comic);
        comic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//To set custom image
                currentUserEdit = (ImageView) v;
                userSetImage();
            }
        });
        //The text view
        TextView characterTextView = new TextView(this);
        characterTextView.setText(nextDisplayName);//This was set by our characterDialog
        characterTextView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 8f));
        characterTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//Goes to series view for current character
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
                nextDisplayName = txtInput.getText().toString();//Gets name of character to add
                addCharacterView();
                //Next three lines update variables and saves them.
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


    private void saveCharacters(Context context){//Writes our characters array to file using serializable.
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

    private void displayNames() {//Used in onCreate method to display all characters in characterNames
        for (int i = 0; i < characterNames.size(); i++) {
            addCharacterView(characterNames.get(i));
        }
    }

    private void userSetImage() {//Called from clicking the image view.
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, userImage);
    }

    //Currently loads too slow for larger pictures. Need help here.
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
                selectedImage = getResizedBitmap(selectedImage,1080,1920);
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
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        // Create a matrix for manipulation
        Matrix matrix = new Matrix();
        // Resize the bitmap
        matrix.setRectToRect(new RectF(0, 0, width, height), new RectF(0, 0, newWidth, newHeight), Matrix.ScaleToFit.CENTER);
        // Return a newly created bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    protected void readCharacters() {//Reads characters array from file
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
    public void buildDefault(View view){
        characters.clear();
        character Batman = new character("Batman");
        character Superman = new character("Superman");
        character wonderWoman = new character("Wonder Woman");
        character avengers = new character("Avengers");
        character justicLeague = new character("Justice League");
        character spidreman = new character("Spider-Man");
        character harelyQuinn = new character("Harley Quinn");
        characters.add(Batman);
        characters.add(Superman);
        characters.add(wonderWoman);
        characters.add(avengers);
        characters.add(justicLeague);
        characters.add(spidreman);
        characters.add(harelyQuinn);
        saveCharacters(context);
        try{
            readCharacters();//Should build our array of Characters from file.
        }catch(Exception e){Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show();}
        for(int i = 0; i< characters.size();i++){//Creates the characterNames array from characters array.
            String charactername = characters.get(i).getCharacterName();
            characterNames.add(charactername);
        }
        displayNames();
        for(int i = 0;i<characters.size();i++){//Sets images previously selected by user.
            if(characters.get(i).getImage() !=null){
                ImageView mImageView = (ImageView) findViewById(i);
                mImageView.setImageBitmap(characters.get(i).getImage());
            }
        }

    }


    //This methods below are either not working correctly at the moment or are just for refrence
    //Deletes the most recent characterview, for testing purposes only.  Not final method
    //Doesnt currently work, avoid using for now
    //Method not edited out so remove button can stay.
    public void removeTextView(View view) {
        /*LinearLayout linearLayout = (LinearLayout) findViewById(R.id.parentLinear);
        if (TextViewCount != 0) {
            LinearLayout toRemove = (LinearLayout) findViewById(TextViewCount - 1);
            linearLayout.removeView(toRemove);
            characterNames.remove(TextViewCount - 1);
            characters.remove(TextViewCount -1);
            saveCharacters(this);
            TextViewCount--;
        }
    */}

    //Saves our characters to prefrences
    //In old version, I save via preferences, keeping code for reference if I need it later.
    /*private void saveCharacters(Context context) {
        SharedPreferences myPrefs;
        SharedPreferences.Editor edit;
        myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        edit = myPrefs.edit();
        String string = listToString(characterNames);
        edit.putString(prefKey, string);
        edit.commit();
    }*/



}
