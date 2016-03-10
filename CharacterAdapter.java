package com.example.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mycomics.MainActivity;
import com.example.android.mycomics.R;
import com.example.android.mycomics.character;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott on 3/10/2016.
 */
public class CharacterAdapter extends ArrayAdapter<character> {
    Context context;
    character myCharacter = null;
    public CharacterAdapter(Context mContext,List<character> characters){
        super(mContext, 0, characters);
        context = mContext;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        myCharacter = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.character_layout,parent,false);
        }
        TextView charName = (TextView) convertView.findViewById(R.id.textView);
        final ImageView charImage = (ImageView) convertView.findViewById(R.id.imageView);
        charImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//To set custom image
                MainActivity.currentUserEdit = charImage;
                MainActivity.currentSaveCharacter = getItem(position);
                userSetImage();
            }
        });
        charName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//To set custom image
                //Toast.makeText(getContext(), myCharacter.getCharacterName(), Toast.LENGTH_SHORT).show();
                nextActivity(getItem(position).getCharacterName());
            }
        });

        Bitmap bm = myCharacter.getImage();
        if(bm != null){
            charImage.setImageBitmap(bm);
        }
        else{
            charImage.setImageResource(R.drawable.addimage);
        }
        charName.setText(myCharacter.getCharacterName());
        return convertView;
    }
    public void userSetImage() {//Called from clicking the image view.
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        ((Activity) context).startActivityForResult(galleryIntent, 1);
    }
    public void nextActivity(String characterName) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra("currentCharName",characterName);//So we know what character we are dealing with
        context.startActivity(intent);
    }
}
