package com.draguve.droidducky;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

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
        EditText text = (EditText)findViewById(R.id.codeArea);
        ArrayList<String> duckyLines = new ArrayList<>(Arrays.asList(text.getText().toString().split("\n")));
        ArrayList<String> letters = Parser.parseDucky(duckyLines);
        for(String key : letters){
            Log.e("All Letters",key);
        }
        TheExecuter.injectKeystrokes(letters);
    }
}