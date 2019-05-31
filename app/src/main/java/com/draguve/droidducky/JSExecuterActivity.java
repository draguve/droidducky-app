package com.draguve.droidducky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import static com.draguve.droidducky.DuckConverter.stringToCommands;

public class JSExecuterActivity extends AppCompatActivity{

    Context appContext;
    String lang = "us";

    public String currentIP;
    public String usbIP;

    private Button runButton;
    private TextView remWindow;
    private ToggleButton serverToggle;
    private httpserver server;

    String filePath;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsexecuter);

        appContext = getApplicationContext();

        runButton = findViewById(R.id.runcode);
        remWindow = findViewById(R.id.rem_output);
        serverToggle = findViewById(R.id.serverToggle);

        Intent callingIntent = getIntent();
        filePath = callingIntent.getExtras().getString("filePath", null);
        if(filePath==null){
            goBackToSelector();
        }

        //Load File
        StringBuilder text = new StringBuilder();
        try{
            File file = new File(filePath);
            if(file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }else{
                goBackToSelector();
            }
        }catch (Exception e ){
            e.printStackTrace();
            goBackToSelector();
        }

        code = text.toString();

        //Get toolbar to change title and stuff
        final Toolbar toolbar = findViewById(R.id.executer_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("The Executor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Bind back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExecution();
            }
        });

        serverToggle.setChecked(false);
        serverToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && server == null) {
                    server = new httpserver();
                    try {
                        server.start();
                        String IPstring = DUtils.getIPAddress(true);
                        currentIP = IPstring;
                        usbIP = "192.168.42.129";
                        Log.w("Httpd", "Web server initialized.");
                        REMLog("Web server initialized");
                    } catch (IOException ioe) {
                        Log.w("Httpd", "The server could not start.");
                    }
                } else if (!isChecked && server != null) {
                    Log.w("Httpd", "Web server Stopped.");
                    currentIP = "";
                    server.stop();
                    server = null;
                    REMLog("Web server disabled");
                }
            }

        });

    }

    public void startExecution(){
        if (!DUtils.checkForFiles()) {
            DUtils.setupFilesForInjection(appContext);
        }
        new JSExecuterAsync().execute(code);
        runButton.setEnabled(false);
    }

    public void goBackToSelector() {
        Intent goingBack = new Intent();
        setResult(RESULT_OK, goingBack);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBackToSelector();
    }

    public void executionFinished() {
        runButton.setEnabled(true);
    }

    public void REMLog(String log) {
        String s = remWindow.getText().toString();
        s += "\n" + log;
        remWindow.setText(s);
    }


    //Class to execute the js without blocking the main thread
    public class JSExecuterAsync extends AsyncTask<String, Float, Integer> {

        protected Integer doInBackground(String... jsScripts) {
            V8 runtime = V8.createV8Runtime();
            KeyWriter writer = new KeyWriter();
            V8Object ducky = new V8Object(runtime);
            runtime.add("ducky", ducky);
            ducky.registerJavaMethod(writer, "SendString", "SendString", new Class<?>[] { String.class });
            ducky.registerJavaMethod(writer, "SendCommand", "SendCommand", new Class<?>[] { String.class });
            ducky.registerJavaMethod(writer, "Log", "Log", new Class<?>[] { String.class });
            ducky.registerJavaMethod(writer, "Delay", "Delay", new Class<?>[] { Integer.class });
            ducky.registerJavaMethod(writer, "PrintIP", "PrintIP", new Class<?>[] { Boolean.class });
            ducky.registerJavaMethod(writer, "WriteFile", "WriteFile", new Class<?>[] { String.class });
            ducky.release();

            try{
                runtime.executeVoidScript(jsScripts[0]);
                runtime.release();
            }
            catch(RuntimeException e){
                Log(e.getMessage());
            }
            return 0;
        }

        public void Log(final String log){
            final String text = log;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    REMLog(text);
                }
            });
        }

        protected void onProgressUpdate(Float... progress) {

        }

        protected void onPostExecute(Integer result) {
            executionFinished();
        }

        class KeyWriter {

            int DEFAULT_DELAY = 200;
            DataOutputStream os = null;

            public KeyWriter(){
                os = initProcess();
                initProperties();
            }

            protected void initProperties(){
                try {
                    DuckConverter.loadAllProperties(lang,appContext);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            public DataOutputStream initProcess(){
                DataOutputStream os = null;
                try {
                    Process process = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes("cd " + DUtils.binHome + '\n');
                }catch(Exception e){
                    e.printStackTrace();
                }
                return os;
            }

            public void SendString(String string){
                ArrayList<String> keys =  stringToCommands(string);
                sendKeys(keys);
            }

            public void SendCommand(String string){
                String key = DuckConverter.convertCommand(string.trim().split(" "));
                sendKey(key);
            }

            public void Log(final String log){
                final String text = log;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        REMLog(text);
                    }
                });
            }

            public void Delay(Integer time){
                try {
                    Thread.sleep(time);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void Delay(){
                Delay(DEFAULT_DELAY);
            }

            public void PrintIP(Boolean wifi){
                if(wifi){
                    SendString(currentIP);
                }else{
                    SendString(usbIP);
                }
            }

            public void WriteFile(String filename){
                ArrayList<String> letters = new ArrayList<>();
                File path = Environment.getExternalStorageDirectory();
                File file = new File(path, "/DroidDucky/host/" + filename);
                if (file.exists()) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        String receiveString;
                        while ((receiveString = bufferedReader.readLine()) != null) {
                            letters.addAll(stringToCommands(receiveString));
                            letters.add("enter");
                        }
                        bufferedReader.close();
                    } catch (FileNotFoundException e) {
                        Log("File not found: " + e.toString());
                    } catch (IOException e) {
                        Log("Can not read file: " + e.toString());
                    }
                } else {
                    Log("File does not exist");
                }
                sendKeys(letters);
            }

            public void sendKey(String key){
                try {
                    if (os != null) {
                        String command = "echo " + key + " | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                        os.writeBytes(command);
                        os.flush();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            public void sendKeys(ArrayList<String> keys){
                try {
                    if (os != null) {
                        for (String key : keys) {
                            String command = "echo " + key + " | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                            os.writeBytes(command);
                            os.flush();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
}