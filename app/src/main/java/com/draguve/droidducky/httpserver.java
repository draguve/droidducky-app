package com.draguve.droidducky;

import android.os.Environment;

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
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {
        String answer = "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(Environment.getExternalStorageDirectory() + "/Droidducky/draguve.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return new NanoHTTPD.Response(Response.Status.OK, "audio/mpeg", fis);
        return NanoHTTPD.newChunkedResponse(Response.Status.OK,"text/plain",fis);
    }
}
