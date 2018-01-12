package com.draguve.droidducky;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Draguve on 1/11/2018.
 */

public class httpserver extends NanoHTTPD {

    public httpserver() {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session){
        Log.d("nanoHttpd",session.getUri());
        FileInputStream fis = null;
        File file = new File(Environment.getExternalStorageDirectory(),"/Droidducky"+session.getUri());
        if(file.exists() && file.isFile()){
            try {
                fis = new FileInputStream(Environment.getExternalStorageDirectory() + "/Droidducky" +session.getUri());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return NanoHTTPD.newChunkedResponse(Response.Status.OK,"text/plain",fis);
        }
        return NanoHTTPD.newFixedLengthResponse("File Not Found");
    }
}
