package com.draguve.droidducky;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ExecuterActivity extends AppCompatActivity {

    Script currentScript;
    CommandLineScript currentCLScript;
    Context appContext;
    private ProgressBar codeProgress;
    private Button runButton;
    private TextView remWindow;
    private ToggleButton serverToggle;
    private httpserver server;
    private Integer currentMode;

    public ArrayList<String> currentIP;
    public ArrayList<String> usbIP;

    private Integer DUCKYSCRIPT_EDIT = 0;
    private Integer COMMANDLINE_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executer);

        //Storing ui elements
        codeProgress = (ProgressBar)findViewById(R.id.script_progress);
        runButton = (Button) findViewById(R.id.runcode);
        remWindow = (TextView) findViewById(R.id.rem_output);
        serverToggle = (ToggleButton) findViewById(R.id.serverToggle);

        //Getting arguments from the calling intent
        Intent callingIntent = getIntent();
        String scriptID = callingIntent.getExtras().getString("idSelected",null);
        currentMode = callingIntent.getExtras().getInt("currentMode",-1);

        //To get script object from the scriptId
        if(currentMode == DUCKYSCRIPT_EDIT){
            ScriptsManager db = new ScriptsManager(this);
            if(scriptID!=null){
                //Stay if the script id is not null
                currentScript = db.getScript(scriptID);
            }else{
                //Go back to the calling activity if the could'nt get id
                goBackToSelector();
            }
        }else if(currentMode == COMMANDLINE_EDIT){
            CommandLineManager commandLineDB = new CommandLineManager(this);
            if(scriptID!=null){
                currentCLScript = commandLineDB.getScript(scriptID);
            }else{
                goBackToSelector();
            }
        }

        appContext = this;

        //Get toolbar to change title and stuff
        final Toolbar toolbar = (Toolbar) findViewById(R.id.executer_toolbar);
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
                if (isChecked && server==null) {
                    server = new httpserver();
                    try {
                        server.start();
                        String IPstring = DUtils.getIPAddress(true);
                        currentIP = DuckConverter.stringToCommands(IPstring);
                        usbIP = DuckConverter.stringToCommands("192.168.42.129");
                        Log.w("Httpd", "Web server initialized.");
                        logREMComment("Web server initialized");
                    } catch(IOException ioe) {
                        Log.w("Httpd", "The server could not start.");
                    }
                }else if(!isChecked && server!=null){
                    Log.w("Httpd", "Web server Stopped.");
                    currentIP = new ArrayList<String>();
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

    public void executeCode(View view){
        remWindow.setText("");
        if(!DUtils.checkForFiles()) {
            DUtils.setupFilesForInjection(this);
        }
        if(currentMode == DUCKYSCRIPT_EDIT){
            new ExecuterAsync().execute(currentScript);
        }else if(currentMode == COMMANDLINE_EDIT){
            new ExecuterAsync().execute(currentCLScript.convertToScript());
        }
        runButton.setEnabled(false);
    }

    public void setPercentage(int percentage){
        codeProgress.setProgress(percentage);
    }

    public void executionFinished(){
        runButton.setEnabled(true);
    }

    public void logREMComment(String comment){
        String s = remWindow.getText().toString();
        s += "\n"+ comment;
        remWindow.setText(s);
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


    //Class to execute the duckyscript without blocking the main thread
    public class ExecuterAsync extends AsyncTask<Script,Float,Integer>{

        protected Integer doInBackground(Script... scripts){
            try {
                publishProgress(0f);
                Script scriptToRun = scripts[0];
                //Initialize the superuser shell
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("cd " + DUtils.binHome + '\n');

                //for each line run the codes
                ArrayList<String> duckyLines = new ArrayList<>(Arrays.asList(scriptToRun.getCode().replaceAll("\\r", "").split("\n")));
                String lastLine = "";
                try{
                    DuckConverter.loadAllProperties(scriptToRun.getLang(),appContext);
                    String IPstring = DUtils.getIPAddress(true);
                    currentIP = DuckConverter.stringToCommands(IPstring);
                    usbIP = DuckConverter.stringToCommands("192.168.42.129");
                }catch (Exception e){
                    e.printStackTrace();
                }
                float size = duckyLines.size();
                for(int i=0;i<duckyLines.size();i++){
                    ArrayList<String> keys = DuckConverter.convertLine(duckyLines.get(i),appContext,lastLine);
                    for(String key : keys){
                        if(key.charAt(0)=='\u0002'){
                            int time = Integer.parseInt(key.substring(1).trim());
                            Thread.sleep(time);
                        }else if(key.charAt(0)=='\u0001'){
                            logREMComment(key.substring(1));
                        }else if(key.charAt(0)=='\u0006'){
                            if(key.charAt(1)=='1'){
                                //Write wifi address here
                                for(String command : currentIP ){
                                    String run = "echo " + command +" | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                                    os.writeBytes(run);
                                    os.flush();
                                }
                            }else{
                                //Write usb address here
                                for(String command : usbIP ){
                                    String run = "echo " + command +" | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                                    os.writeBytes(run);
                                    os.flush();
                                }
                            }
                        }else{
                            String command = "echo " + key +" | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                            os.writeBytes(command);
                            os.flush();
                        }
                    }
                    lastLine = duckyLines.get(i);
                    publishProgress(i/size);
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
            setPercentage((int)(progress[0]*100));
        }

        protected void onPostExecute(Integer result) {
            executionFinished();
        }

    }

}
