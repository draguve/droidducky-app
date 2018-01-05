package com.draguve.droidducky;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
//import android.widget.Toolbar;

/**
 * Created by Draguve on 1/4/2018.
 */

public class CodeEditor extends AppCompatActivity {

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
        codeTextBox = (EditText)findViewById(R.id.codeEdit);
        scriptName = (EditText)findViewById(R.id.scriptName);
        db = new ScriptsManager(this);
        if(scriptID!=null){
            currentScript = db.getScript(scriptID);
            if(currentScript!=null){
                scriptName.setText(currentScript.getName());
                codeTextBox.setText(currentScript.getCode());
            }else{
                currentScript = new Script("","");
            }
        }else{
            currentScript = new Script("","");
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.code_toolbar);
        setSupportActionBar(toolbar);
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
            //Toast.makeText(getApplicationContext(),"Replacing the saved script",Toast.LENGTH_SHORT).show();
            //db.updateScript(currentScript);
            new MaterialDialog.Builder(this)
                    .title("How do you want to save the script")
                    .positiveText("Create new")
                    .negativeText("Cancel")
                    .neutralText("Overwrite")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            db.addScript(new Script(currentScript.getName(),currentScript.getCode()));
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
        }
    }

    public void goBackToSelector(){
        Intent goingBack = new Intent();
        setResult(RESULT_OK,goingBack);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBackToSelector();
    }
}
