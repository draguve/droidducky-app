package com.draguve.droidducky;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ExecuterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executer);
        Intent callingIntent = getIntent();
        String scriptID = callingIntent.getExtras().getString("idSelected",null);
        ScriptsManager db = new ScriptsManager(this);
        if(scriptID!=null){
            //Stay if the script id is not null

        }else{
            //Go back to the calling activity if the could'nt get id
            goBackToSelector();
        }
    }

    public void goBackToSelector(){
        Intent goingBack = new Intent();
        setResult(RESULT_OK,goingBack);
        finish();
    }
}
