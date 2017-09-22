package com.draguve.droidducky;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        for(int i=0;i<letters.length;i++){
            String[] command = {"cd " + binHome,"echo "+ letters[i] +" | ./hid-gadget-test /dev/hidg0 keyboard"};
            TheExecuter.RunAsRoot(command);
        }
    }

}
