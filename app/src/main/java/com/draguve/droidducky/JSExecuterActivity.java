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

public class JSExecuterActivity extends AppCompatActivity {

    Context appContext;
    String lang = "us";

    public String currentIP;
    public String usbIP;

    private Button runButton;
    private TextView remWindow;
    private ToggleButton serverToggle;
    private httpserver server;

    public void logREMComment(String comment) {
        String s = remWindow.getText().toString();
        s += "\n" + comment;
        remWindow.setText(s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsexecuter);

        appContext = getApplicationContext();

        runButton = findViewById(R.id.runcode);
        remWindow = findViewById(R.id.rem_output);
        serverToggle = findViewById(R.id.serverToggle);

        //executeJS("");

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
                        logREMComment("Web server initialized");
                    } catch (IOException ioe) {
                        Log.w("Httpd", "The server could not start.");
                    }
                } else if (!isChecked && server != null) {
                    Log.w("Httpd", "Web server Stopped.");
                    currentIP = "";
                    server.stop();
                    server = null;
                    logREMComment("Web server disabled");
                }
            }

        });

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


    //Class to execute the duckyscript without blocking the main thread
    public class ExecuterAsync extends AsyncTask<String, Float, Integer> {

        int DEFAULT_DELAY = 200;
        DataOutputStream os = null;
        /* contains the keyboard configuration */
        private Properties keyboardProps;
        /* contains the language layout */
        private Properties layoutProps;
        /* contains the commands configuration */
        private Properties commandProps;

        protected Integer doInBackground(String... jsScripts) {
            initProcess();
            initProperties();

            V8 runtime = V8.createV8Runtime();
            //runtime.registerJavaMethod();
           runtime.executeVoidScript(""
                    + "var hello = 'hello, ';\n"
                    + "var world = 'world!';\n"
                    + "hello.concat(world).length;\n");
           runtime.release();


            return 0;
        }

        protected void initProperties(){
            try {
                keyboardProps = DuckConverter.loadProperties("keyboard", appContext);
                //TODO: Change this default language selection
                layoutProps = DuckConverter.loadProperties(lang, appContext);
                commandProps = DuckConverter.loadProperties("commands", appContext);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        protected void onProgressUpdate(Float... progress) {

        }

        protected void onPostExecute(Integer result) {

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

        public void sendString(String string){
            ArrayList<String> keys =  stringToCommands(string);
            sendKeys(keys);
        }

        public void sendCommands(String string){
            String key = DuckConverter.convertCommand(string.trim().split(" "));
            sendKey(key);
        }

        public void Log(String log){
            logREMComment(log);
        }

        public void Delay(int time){
            try {
                Thread.sleep(time);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void Delay(){
            Delay(DEFAULT_DELAY);
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
