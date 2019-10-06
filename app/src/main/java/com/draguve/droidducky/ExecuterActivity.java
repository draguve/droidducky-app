package com.draguve.droidducky;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ExecuterActivity extends AppCompatActivity {

    public String currentIP;
    public String usbIP;
    Context appContext;
    private ProgressBar codeProgress;
    private Button runButton;
    private TextView remWindow;
    private ToggleButton serverToggle;
    private httpserver server;
    private Integer DUCKYSCRIPT_EDIT = 0;
    private Integer COMMANDLINE_EDIT = 1;

    private Spinner langSpinner;

    String fileName;
    String filePath;
    String scriptType;

    String code;
    String lang = "us";
    private static final String[] languages = {"be", "br", "ca", "ch", "de", "dk", "es", "fi", "fr", "gb", "hr", "it", "no", "pt", "ru", "si", "sv", "tr", "us"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executer);

        //Storing ui elements
        codeProgress = findViewById(R.id.script_progress);
        runButton = findViewById(R.id.runcode);
        remWindow = findViewById(R.id.rem_output);
        serverToggle = findViewById(R.id.serverToggle);

        langSpinner = findViewById(R.id.lang);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);
        langSpinner.setSelection(18);
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lang = languages[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                lang = languages[18];
            }
        });

        //Getting arguments from the calling intent
        Intent callingIntent = getIntent();

        fileName = callingIntent.getExtras().getString("fileName", null);
        filePath = callingIntent.getExtras().getString("filePath", null);
        scriptType = callingIntent.getExtras().getString("scripttype",null);
        if(fileName == null || filePath == null || scriptType == null){
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

        appContext = this;

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

        //server toggle button logic
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

    @Override
    public boolean onSupportNavigateUp() {
        goBackToSelector();
        return true;
    }

    public void enableTether(View view){

    }

    public void executeCode(View view) {
        remWindow.setText("");
        if (!DUtils.checkForFiles()) {
            DUtils.setupFilesForInjection(this);
        }
//        if (currentMode == DUCKYSCRIPT_EDIT) {
//            new ExecuterAsync().execute(currentScript);
//        } else if (currentMode == COMMANDLINE_EDIT) {
//            new ExecuterAsync().execute(currentCLScript.convertToScript());
//        }
        Code script = new Code();
        script.code = code;
        script.lang = lang;
        new ExecuterAsync().execute(script);
        runButton.setEnabled(false);
    }

    public void setPercentage(int percentage) {
        codeProgress.setProgress(percentage);
    }

    public void executionFinished() {
        runButton.setEnabled(true);
    }

    public void logREMComment(String comment) {
        String s = remWindow.getText().toString();
        s += "\n" + comment;
        remWindow.setText(s);
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
    public class ExecuterAsync extends AsyncTask<Code, Float, Integer> {

        protected Integer doInBackground(Code... scripts) {
            try {
                publishProgress(0f);
                Code scriptToRun = scripts[0];
                //Initialize the superuser shell
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("cd " + DUtils.binHome + '\n');

                //for each line run the codes
                ArrayList<String> duckyLines = new ArrayList<>(Arrays.asList(scriptToRun.code.replaceAll("\\r", "").split("\n")));
                String lastLine = "";
                try {
                    DuckConverter.loadAllProperties(scriptToRun.lang, appContext);
                    String IPstring = DUtils.getIPAddress(true);
                    currentIP = IPstring;
                    usbIP = "192.168.42.129";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                float size = duckyLines.size();
                for (int i = 0; i < duckyLines.size(); i++) {
                    ArrayList<String> keys = DuckConverter.convertLine(duckyLines.get(i), appContext, lastLine);
                    for (String key : keys) {
                        if (key.charAt(0) == '\u0002') {
                            int time = Integer.parseInt(key.substring(1).trim());
                            Thread.sleep(time);
                        } else if (key.charAt(0) == '\u0001') {
                            logREMComment(key.substring(1));
                        } else if (key.charAt(0) == '\u0006') {
                            if (key.charAt(1) == '1') {
                                //Write wifi address here
                                ArrayList<String> ipCommands = new ArrayList<>();
                                ipCommands = DuckConverter.stringToCommands(currentIP);
                                for (String command : ipCommands) {
                                    String run = "echo " + command + " | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                                    os.writeBytes(run);
                                    os.flush();
                                }
                            } else {
                                //Write usb address here
                                ArrayList<String> ipCommands = new ArrayList<>();
                                ipCommands = DuckConverter.stringToCommands(usbIP);
                                for (String command : ipCommands) {
                                    String run = "echo " + command + " | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                                    os.writeBytes(run);
                                    os.flush();
                                }
                            }
                        } else {
                            String command = "echo " + key + " | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                            os.writeBytes(command);
                            os.flush();
                        }
                    }
                    lastLine = duckyLines.get(i);
                    publishProgress(i / size);
                }
                //stop and flush the shell after execution finished
                os.writeBytes("exit\n");
                os.flush();
                os.close();
                publishProgress(1f);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        protected void onProgressUpdate(Float... progress) {
            setPercentage((int) (progress[0] * 100));
        }

        protected void onPostExecute(Integer result) {
            executionFinished();
        }


    }

    private static class Code{
        public String code;
        public String lang;
    }
}
