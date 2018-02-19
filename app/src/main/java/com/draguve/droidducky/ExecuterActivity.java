package com.draguve.droidducky;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ExecuterActivity extends AppCompatActivity {

    Script currentScript;
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executer);
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
    }

    public void executeCode(View view){
        if(!DUtils.checkForFiles()) {
            DUtils.setupFilesForInjection(this);
        }
        new ExecuterAsync().execute(currentScript);
    }

    public static void setPercentage(int percentage){

    }

    public static void executionFinished(){

    }

    public void logREMComment(String comment){
        Log.i("ExecuterActivity",comment);
    }

    public void goBackToSelector(){
        Intent goingBack = new Intent();
        setResult(RESULT_OK,goingBack);
        finish();
    }

    public class ExecuterAsync extends AsyncTask<Script,Float,Integer>{

        protected Integer doInBackground(Script... scripts){
            try {
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
                    publishProgress((float)(i/duckyLines.size()));
                }
                //stop and flush the shell after execution finished
                os.writeBytes("exit\n");
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        protected void onProgressUpdate(Float... progress) {
            Log.e("Async","progress : "+progress);
        }

        protected void onPostExecute(Integer result) {
            //
        }

    }

}
