package com.draguve.droidducky;

import android.content.Intent;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class ResponseReader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_reader);

        Intent callingIntent = getIntent();
        String fileName = callingIntent.getExtras().getString("fileName", null);
        String filePath = callingIntent.getExtras().getString("filePath", null);

        if(fileName == null || filePath == null){
            onBackPressed();
        }

        StringBuilder text = new StringBuilder();
        try{
            File file = new File(filePath);


            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

        }catch (Exception e ){
            e.printStackTrace();
        }

        EditText responseTextBox = findViewById(R.id.response_read);
        responseTextBox.setHorizontallyScrolling(true);
        responseTextBox.setHorizontalScrollBarEnabled(true);
        responseTextBox.setVerticalScrollBarEnabled(true);
        responseTextBox.setText(text.toString());

        EditText nameBox = findViewById(R.id.response_name);
        nameBox.setText(fileName);


        final Toolbar toolbar = findViewById(R.id.response_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Response");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void goBackToSelector() {
        Intent goingBack = new Intent();
        setResult(RESULT_OK, goingBack);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        goBackToSelector();
        return true;
    }

    @Override
    public void onBackPressed() {
        goBackToSelector();
    }
}
