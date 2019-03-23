package com.draguve.droidducky;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Draguve on 1/11/2018.
 */

public class httpserver extends NanoHTTPD {

    public httpserver() {
        super(8080);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.d("nanoHttpd", session.getUri());
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                Map<String, String> postData = new HashMap<String, String>();
                session.parseBody(postData);
                Writer output = null;
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
                String date = getRandomHexString(8)+" "+df.format(Calendar.getInstance().getTime());
                File file = new File(Environment.getExternalStorageDirectory(),"/DroidDucky/responses/" + date);
                output = new BufferedWriter(new FileWriter(file));
                output.write(postData.get("postData"));
                output.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            FileInputStream fis = null;
            String fileName = session.getUri();
            File file = new File(Environment.getExternalStorageDirectory(), "/Droidducky/host" + fileName);
            if (file.exists() && file.isFile()) {
                try {
                    fis = new FileInputStream(Environment.getExternalStorageDirectory() + "/Droidducky/host" + fileName);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return NanoHTTPD.newChunkedResponse(Response.Status.OK, getMimeType(fileName), fis);
            }
            return NanoHTTPD.newFixedLengthResponse("File Not Found");
        }
        return NanoHTTPD.newFixedLengthResponse("Problem in serve");
    }

    private String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
}
