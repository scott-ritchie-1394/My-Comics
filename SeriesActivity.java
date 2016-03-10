package com.example.android;

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

import com.example.android.mycomics.MainActivity;
import com.example.android.mycomics.R;
import com.example.android.mycomics.character;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

//NOTE: Currently does not support custom Series image. Parts of skeleton for that code are here.
public class SeriesActivity extends AppCompatActivity {//Works similaryly to MainActivity
    String currentCharacter = "";//Holds the character we are working from. Gets from previous activity
    Context context = this;
    List<String> seriesNames = new ArrayList<>();
    String nextDisplaySeries = "";//Holds string of new Series name
    int comicHeightInPx;
    ImageView currentUserEdit;
    int TextViewCount = 0;
    int userImage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        comicHeightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        //Gets our current Character to associate our Series' with
        Intent i = getIntent();
        currentCharacter = (String) i.getSerializableExtra("currentCharName");
        TextView title = (TextView) findViewById(R.id.seriesTitle);
        title.setText(currentCharacter);
        readSeries();//Gets seriesNames from file
        displaySeries();
    }

    public void seriesDialog(View view) {//Same as characterDialog from MainActivity
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final EditText txtInput = new EditText(this);
        dialogBuilder.setTitle("Add Series:");
        dialogBuilder.setView(txtInput);
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nextDisplaySeries = txtInput.getText().toString();
                addSeriesView();
                seriesNames.add(nextDisplaySeries);
                saveSeries(context);
            }
        });
        AlertDialog dialogCharacterName = dialogBuilder.create();
        dialogCharacterName.show();
    }
    //Should go to our character screen, but currently does not due to memory issues.
    //Hoping once I fix image issues, this will be fixed as well.
    public void previousActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //Reads our seriesNames from file.  Reads from a file named after our currentCharacter
    protected void readSeries() {
        try {
            String filePath = context.getFilesDir().getPath().toString() + "/" + currentCharacter + ".txt";
            File f = new File(filePath);
            FileInputStream fis = null;
            ObjectInputStream in = null;
            fis = new FileInputStream(f);
            in = new ObjectInputStream(fis);
            seriesNames = (List<String>) in.readObject();
            in.close();
        } catch (Exception e) {
        }
    }

    public void addSeriesView() {//Same code as addCharacterView from MainActivity
        //Creates the Character View that holds picture and name
        LinearLayout parentLinearLayout = (LinearLayout) findViewById(R.id.parentLinear);
        LinearLayout childLinearLayout = new LinearLayout(this);
        childLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        //The image
        ImageView comic = new ImageView(this);
        comic.setLayoutParams(new LinearLayout.LayoutParams(
                0, comicHeightInPx, 1.55f));
        comic.setImageResource(R.drawable.addimage);
        childLinearLayout.addView(comic);
        comic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentUserEdit = (ImageView) v;
                userSetImage();
            }
        });
        //The name
        TextView seriesTextView = new TextView(this);
        seriesTextView.setText(nextDisplaySeries);
        seriesTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 8f));
        //for when I create an issuesActivity
        /*seriesTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView currentTextView = (TextView) v;
                String charName = currentTextView.getText().toString();
                nextActivity(charName);
            }
        });*/
        childLinearLayout.setId(TextViewCount);
        seriesTextView.setTextSize(28);
        childLinearLayout.addView(seriesTextView);
        parentLinearLayout.addView(childLinearLayout);
        TextViewCount++;
    }

    public void addSeriesView(String characterName) {
        nextDisplaySeries = characterName;
        addSeriesView();
    }
    //Saves our seriesNames to file.  Saves our Series to a file named after our currentCharacter
    private void saveSeries(Context context) {
        String filePath = context.getFilesDir().getPath().toString() + "/" + currentCharacter + ".txt";
        File f = new File(filePath);
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(f);
            out = new ObjectOutputStream(fos);
            out.writeObject(seriesNames);
            out.close();
        } catch (Exception e) {
            Toast.makeText(SeriesActivity.this,
                    "ERROR", Toast.LENGTH_LONG).show();
            Log.d("MYERROR", e.toString());
        }
    }

    private void userSetImage() {//Not set up to be saved after close
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, userImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        try {
            // When an Image is picked
            if (resultCode == RESULT_OK
                    && null != returnedIntent) {
                // Get the Image from data

                Uri imageLocation = returnedIntent.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageLocation);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                currentUserEdit.setImageBitmap(selectedImage);

            }
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG)
                    .show();
            Log.d("ImageGet ERROR", e.toString());
        }

    }

    private void displaySeries() {
        for (int i = 0; i < seriesNames.size(); i++) {
            addSeriesView(seriesNames.get(i));
        }
    }


}
