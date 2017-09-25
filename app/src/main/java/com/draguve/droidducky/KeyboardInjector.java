package com.draguve.droidducky;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.util.ArrayList;

/**
 * Created by Draguve on 9/25/2017.
 */


class KeyboardInjector extends AsyncTask<ArrayList<String>,Void,Boolean> {


    /*
    * Plans
    *
    *   make this call the Parser instead of doing it in the main thread
    *   make all conversions in this one and sleeps in this one
    *
    * */

    @Override
    //Sends keystokes to the hostdevice with hid-gadget async
    protected Boolean doInBackground(ArrayList<String> ...keys){
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("cd " + DUtils.binHome + '\n');
            for(String key : keys[0]){
                String command = "echo " + key +" | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                os.writeBytes(command);
            }
            if(isCancelled()){
                return false;
            }
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    protected void onPostExecute(Boolean finished){
        MainActivity.executionFinished(true);
    }
}
