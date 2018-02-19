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
import android.widget.ProgressBar;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ExecuterActivity extends AppCompatActivity {

    Script currentScript;
    Context appContext;
    private ProgressBar codeProgress;
    private Button runButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executer);
        codeProgress = (ProgressBar)findViewById(R.id.script_progress);
        runButton = (Button) findViewById(R.id.runcode);
        Intent callingIntent = getIntent();
        String scriptID = callingIntent.getExtras().getString("idSelected",null);
        ScriptsManager db = new ScriptsManager(this);
        appContext = this;
        if(scriptID!=null){
            //Stay if the script id is not null
            currentScript = db.getScript(scriptID);
        }else{
            //Go back to the calling activity if the could'nt get id
            goBackToSelector();
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.executer_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("The Executor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        goBackToSelector();
        return true;
    }

    public void executeCode(View view){
        if(!DUtils.checkForFiles()) {
            DUtils.setupFilesForInjection(this);
        }
        new ExecuterAsync().execute(currentScript);
        runButton.setEnabled(false);

    }

    public void setPercentage(int percentage){
        codeProgress.setProgress(percentage);
    }

    public void executionFinished(){
        runButton.setEnabled(true);
    }

    public void logREMComment(String comment){
        Log.i("ExecuterActivity",comment);
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
                }catch (Exception e){
                    e.printStackTrace();
                }
                float size = duckyLines.size();
                //for(String line: duckyLines){
                for(int i=0;i<duckyLines.size();i++){
                    ArrayList<String> keys = DuckConverter.convertLine(duckyLines.get(i),appContext,lastLine);
                    for(String key : keys){
                        if(key.charAt(0)=='\u0002'){
                            int time = Integer.parseInt(key.substring(1).trim());
                            Thread.sleep(time);
                        }else if(key.charAt(0)=='\u0001'){
                            logREMComment(key.substring(1));
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
