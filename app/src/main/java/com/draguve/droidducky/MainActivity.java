package com.draguve.droidducky;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static String binHome = "/data/data/com.draguve.droidducky/files";
    public static Application application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = getApplication();
    }

    public void Setup(View view){
        DUtils.assetsToFiles(binHome,"","data",application.getApplicationContext());
        String command = "chmod 755 " + binHome + "/hid-gadget-test";
        DUtils.showToast(TheExecuter.RunAsRootOutput(command),application);
    }

    public void RunKeyboardAttack(View view){
        String[] letters = {"t","e","s","t"};
        TheExecuter.SendKeyStrokes(letters);
    }
}