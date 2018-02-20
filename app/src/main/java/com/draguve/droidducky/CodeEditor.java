package com.draguve.droidducky;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Arrays;

/**
 * Created by Draguve on 1/4/2018.
 */

public class CodeEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner langSpinner;
    private static final String[] languages = {"be","br","ca","ch","de","dk","es","fi","fr","gb","hr","it","no","pt","ru","si","sv","tr","us"};
    private Script currentScript = null;
    ScriptsManager db;
    EditText codeTextBox = null;
    EditText scriptName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_code);
        Intent callingIntent = getIntent();
        String scriptID = callingIntent.getExtras().getString("idSelected",null);

        //Spinner Settings
        langSpinner = (Spinner)findViewById(R.id.lang);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,languages);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);
        langSpinner.setOnItemSelectedListener(this);

        codeTextBox = (EditText)findViewById(R.id.codeEdit);
        scriptName = (EditText)findViewById(R.id.scriptName);
        codeTextBox.setHorizontallyScrolling(true);
        codeTextBox.setHorizontalScrollBarEnabled(true);
        codeTextBox.setVerticalScrollBarEnabled(true);

        db = new ScriptsManager(this);
        if(scriptID!=null){
            currentScript = db.getScript(scriptID);
            if(currentScript!=null){
                scriptName.setText(currentScript.getName());
                codeTextBox.setText(currentScript.getCode());
                //Can be optimized,the reverse search
                langSpinner.setSelection(Arrays.asList(languages).indexOf(currentScript.getLang()));
            }else{
                currentScript = new Script("","","us");
                langSpinner.setSelection(18);
            }
        }else{
            currentScript = new Script("","","us");
            langSpinner.setSelection(18);
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.code_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit DuckyScript");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        goBackToSelector();
        return true;
    }

    public void runCode(View view){
        currentScript.setCode(codeTextBox.getText().toString());
        currentScript.executeCode(this);
    }

    public void saveScript(View view){
        if(scriptName!=null) {
            if(scriptName.getText().length()==0){
                Toast.makeText(this,"Please name the script to save it",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        currentScript.setCode(codeTextBox.getText().toString());
        currentScript.setName(scriptName.getText().toString());
        if(db.getScript(currentScript.getID())!=null){
            new MaterialDialog.Builder(this)
                    .title("How do you want to save the script")
                    .positiveText("Create new")
                    .negativeText("Cancel")
                    .neutralText("Overwrite")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            db.addScript(new Script(currentScript.getName(),currentScript.getCode(),currentScript.getLang()));
                            goBackToSelector();
                        }
                    })
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            db.updateScript(currentScript);
                            goBackToSelector();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else{
            db.addScript(currentScript);
            goBackToSelector();
        }
    }

    public void goBackToSelector(){
        Intent goingBack = new Intent();
        setResult(RESULT_OK,goingBack);
        finish();
    }

    public void goBack(){
        Intent goingBack = new Intent();
        setResult(RESULT_OK,goingBack);
        finish();
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        currentScript.setLang(languages[position]);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        currentScript.setLang("us");
        // Another interface callback
    }

    @Override
    public void onBackPressed() {
        if(codeTextBox.getText().toString().trim().equals(currentScript.getCode().trim())){
            Toast.makeText(this,"Changes in script saved",Toast.LENGTH_SHORT).show();
            currentScript.setCode(codeTextBox.getText().toString());
            currentScript.setName(scriptName.getText().toString());
            db.updateScript(currentScript);
        }
        goBackToSelector();
    }
}
