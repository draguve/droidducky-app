package com.draguve.droidducky;

import android.app.Application;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String binHome;
    public static Application application;
    public static boolean running = false;
    static Button runButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = getApplication();
        binHome = "/data/data/"+application.getPackageName()+"/files";
        DUtils.initUtils(binHome,application);
        runButton = (Button)findViewById(R.id.run);
        if(!checkForFiles()) {
            Setup();
            DUtils.showToast("Setup Started");
        }
    }

    //Checks Files
    public boolean checkForFiles(){
        if(DUtils.checkFilePermissions(binHome + "/hid-gadget-test")){
            return true;
        }else{
            return false;
        }
    }

    //Copies assets to files folder and sets permissions
    public void Setup(){
        DUtils.assetsToFiles(binHome,"","data",application.getApplicationContext());
        String command = "chmod 755 " + binHome + "/hid-gadget-test";
        TheExecuter.runAsRoot(command);
    }

    public void runKeyboardAttack(View view){
        if(!running){
            running = true;
            EditText text = (EditText)findViewById(R.id.text);
            ArrayList<String> letters = Parser.convertString(text.getText().toString().toCharArray());
            new KeyboardInjector().execute(letters);
            runButton.setText("Running");
            runButton.setClickable(false);
        }else{
            DUtils.showToast("Already Running");
        }
    }

    public static void executionFinished(boolean finished){
        if(finished){
            DUtils.showToast("Ran Successfully");
        }
        runButton.setText("Run");
        runButton.setClickable(true);
        running = false;
    }
}