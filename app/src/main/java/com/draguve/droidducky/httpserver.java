package com.draguve.droidducky;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
}
