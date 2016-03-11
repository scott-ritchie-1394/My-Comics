package com.example.android.mycomics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.SeriesActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IssueActivity extends AppCompatActivity {
    List<String> stringIssues;
    ArrayList<Double> doubleIssues = new ArrayList<>();
    List<Series> seriesArray = new ArrayList<>();
    Series currentSeries;
    int index;
    String currentCharacter = "";
    ListView myListView;
    ArrayAdapter<String> myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        Intent intent = getIntent();
        currentSeries = (Series) intent.getSerializableExtra("currentSeries");
        currentCharacter = (String) intent.getSerializableExtra("character");
        myListView = (ListView) findViewById(R.id.issueListView);
        readSeries();
        index = -1;
        for(int i = 0;i<seriesArray.size();i++){
            if(currentSeries.getSeriesName().equals(seriesArray.get(i).getSeriesName())){
                index = i;
            }
        }
        doubleIssues = seriesArray.get(index).getIssueArray();
        combineIssueRuns(doubleIssues);
        myAdapter = new ArrayAdapter<>(this, R.layout.issues_layout, stringIssues);
        myListView.setAdapter(myAdapter);
        /*myListView = (ListView) findViewById(R.id.issueListView);
        doubleIssues = currentSeries.getIssueArray();
        combineIssueRuns(doubleIssues);
        myAdapter = new ArrayAdapter<>(this, R.layout.issues_layout, stringIssues);
        myListView.setAdapter(myAdapter);
        System.out.println(stringIssues);*/
    }

    private void combineIssueRuns(ArrayList<Double> list) {
        List<String> toReturn = new ArrayList<>();
        System.out.println(list);
        for (int i = 0; i < list.size(); i++) {
            int start = i;
            int size = 1;
            int end = start;
            if (i < list.size() - 2) {
                end = start + 1;
                while (list.get(end) == list.get(end - 1) + 1) {
                    size++;
                    end++;
                    if (end >= list.size()) {
                        break;
                    }
                }
            }
            if (size >= 3) {
                toReturn.add(list.get(start) + "-" + list.get(end - 1));
                i = end - 1;
            } else
                toReturn.add("" + list.get(i));
        }
        stringIssues = toReturn;
        try{
        myAdapter.notifyDataSetChanged();}catch(Exception e){}
        System.out.println(stringIssues);
    }

    public void addIssues(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final EditText txtInput = new EditText(this);
        txtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogBuilder.setTitle("Add Issues:");
        dialogBuilder.setView(txtInput);
        dialogBuilder.setPositiveButton("Done",null);
        dialogBuilder.setNeutralButton("Add", null);

        final AlertDialog dialogCharacterName = dialogBuilder.create();
        dialogCharacterName.setOnShowListener(new DialogInterface.OnShowListener(){
            @Override
            public void onShow(DialogInterface dialog){
                Button add = dialogCharacterName.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button done = dialogCharacterName.getButton(AlertDialog.BUTTON_POSITIVE);
                add.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        //doubleIssues.add(Double.valueOf(txtInput.getText().toString()));
                        seriesArray.get(index).addIssue(Double.valueOf(txtInput.getText().toString()));
                        combineIssueRuns(doubleIssues);
                        txtInput.setText("");
                    }
                });
                done.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        myAdapter.clear();
                        Collections.sort(doubleIssues);
                        combineIssueRuns(doubleIssues);
                        myAdapter.addAll(stringIssues);
                        saveIssues();
                        myAdapter.notifyDataSetChanged();
                        dialogCharacterName.dismiss();
                    }
                });
            }
        });
        dialogCharacterName.show();
    }
    public void saveIssues(){
        String filePath = this.getFilesDir().getPath().toString() + "/" + currentCharacter + ".txt";
        File f = new File(filePath);
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try{
            fos = new FileOutputStream(f);
            out = new ObjectOutputStream(fos);
            out.writeObject(seriesArray);
            out.close();
        }
        catch(Exception e){
            Toast.makeText(IssueActivity.this,
                    "SAVEERROR", Toast.LENGTH_LONG).show();
            Log.d("SAVEERROR", e.toString());}
    }
    protected void readSeries() {//Reads characters array from file
        try {
            String filePath = this.getFilesDir().getPath().toString() + "/" + currentCharacter + ".txt";
            File f = new File(filePath);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fis);
            seriesArray = (List<Series>) in.readObject();
            in.close();
        }
        catch(Exception e){}
    }
    public void goBack(View v){
        Intent intent = new Intent(this, SeriesActivity.class);
        intent.putExtra("currentCharName",currentCharacter);//So we know what character we are dealing with
        this.startActivity(intent);
    }
}
