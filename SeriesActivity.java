package com.example.android;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.mycomics.MainActivity;
import com.example.android.mycomics.R;
import com.example.android.mycomics.Series;
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
    String currentCharacter = "";
    String filePath = "";
    List<Series> series = new ArrayList<>();//Holds our characters
    SeriesAdapter adapter;
    ListView listView;
    public static ImageView currentUserEdit;
    public static Series currentSaveSeries;

    Context context = this;
    String nextDisplayName = "";//Used to hold String for new character as input by user
    int comicHeightInPx;//For dp conversion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Gets height using dp
        comicHeightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        filePath = this.getFilesDir().getPath().toString() + "/" + currentCharacter + ".txt";
        adapter  = new SeriesAdapter(this,series);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        try{
            readSeries();//Should build our array of Series from file.
        }catch(Exception e){Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show();
            Log.d("READ ERROR",e.toString());}
    }
    //Creates dialoge for adding a character
    public void characterDialog(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final EditText txtInput = new EditText(this);
        dialogBuilder.setTitle("Add Series:");
        dialogBuilder.setView(txtInput);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nextDisplayName = txtInput.getText().toString();//Gets name of character to add
                //Next three lines update variables and saves them.
                series.add(new Series(nextDisplayName));
                //adapter.add(series.get(series.size()-1));
                saveSeries(context);
            }
        });
        AlertDialog dialogCharacterName = dialogBuilder.create();
        dialogCharacterName.show();
    }
    private void saveSeries(Context context){//Writes our characters array to file using serializable.
        File f = new File(filePath);
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try{
            fos = new FileOutputStream(f);
            out = new ObjectOutputStream(fos);
            out.writeObject(series);
            out.close();
        }
        catch(Exception e){Toast.makeText(SeriesActivity.this,
                "SAVEERROR", Toast.LENGTH_LONG).show();
            Log.d("SAVEERROR",e.toString());}
    }

    //Sets image and saves it
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
                selectedImage = getResizedBitmap(selectedImage,1080/4,1920/4);
                currentUserEdit.setImageBitmap(selectedImage);
                currentSaveSeries.setImage(selectedImage);
                saveSeries(context);
            }
        } catch (Exception e) {
            Toast.makeText(this, "ImageGet ERROR", Toast.LENGTH_LONG)
                    .show();
            Log.d("ImageGet ERROR", e.toString());
        }

    }
    //Resizes a bitmap
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

    protected void readSeries() {//Reads characters array from file
        try {
            File f = new File(filePath);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fis);
            series = (List<Series>) in.readObject();
            in.close();
        }
        catch(Exception e){}
        adapter.addAll(series);
    }
    public void buildDefault(View v){
        series.clear();
        System.out.println(series);
        Series Batman = new Series("Batman");
        Series Superman = new Series("Superman");
        Series wonderWoman = new Series("Wonder Woman");
        Series avengers = new Series("Avengers");
        Series justicLeague = new Series("Justice League");
        Series spidreman = new Series("Spider-Man");
        Series harelyQuinn = new Series("Harley Quinn");
        series.add(Batman);
        series.add(Superman);
        series.add(wonderWoman);
        series.add(avengers);
        series.add(justicLeague);
        series.add(spidreman);
        series.add(harelyQuinn);
        saveSeries(context);
        adapter.notifyDataSetChanged();
        System.out.println(series);
    }
    public void goToMain(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

