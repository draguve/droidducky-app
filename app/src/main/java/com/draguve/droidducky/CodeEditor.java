package com.draguve.droidducky;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

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
        db = new ScriptsManager(this);
        if(scriptID!=null){
            currentScript = db.getScript(scriptID);
            if(currentScript!=null){
                codeTextBox = (EditText)findViewById(R.id.codeEdit);
                scriptName = (EditText)findViewById(R.id.scriptName);
                scriptName.setText(currentScript.getName());
                codeTextBox.setText(currentScript.getCode());
            }else{
                currentScript = new Script("","");
            }
        }else{
            currentScript = new Script("","");
        }
    }

    public void runCode(View view){
        currentScript.setCode(codeTextBox.getText().toString());
        currentScript.executeCode(this);
    }

    public void saveScript(View view){
        if(scriptName.getText().length()==0){
            Toast.makeText(this,"Please name the script to save it",Toast.LENGTH_SHORT).show();
        }
        currentScript.setCode(codeTextBox.getText().toString());
        currentScript.setName(scriptName.getText().toString());
        if(db.getScript(currentScript.getID())!=null){
            Toast.makeText(getApplicationContext(),"Replacing the saved script",Toast.LENGTH_SHORT).show();
            db.updateScript(currentScript);
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
