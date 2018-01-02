package com.draguve.droidducky;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static String binHome;
    public static Application application;
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
            setup();
            DUtils.showToast("setup Started");
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
    public void setup(){
        DUtils.assetsToFiles(binHome,"","data",this);
        String command = "chmod 755 " + binHome + "/hid-gadget-test";
        TheExecuter.runAsRoot(command);
    }

    public void runKeyboardAttack(View view){
        EditText text = (EditText)findViewById(R.id.codeArea);
        Script script = new Script("script",""+text.getText().toString());
        script.executeCode(this);
    }
}