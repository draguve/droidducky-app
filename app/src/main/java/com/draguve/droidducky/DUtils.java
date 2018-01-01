package com.draguve.droidducky;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Draguve on 9/23/2017.
 */

public class DUtils {

    public static String binHome;
    public static Application application;

    //Copies files from the assets folder to the files folder as assets folder is non executable
    public static void assetsToFiles(String TARGET_BASE_PATH, String path, String copyType,Context appContext) {
        AssetManager assetManager = appContext.getAssets();
        String assets[];
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(TARGET_BASE_PATH, path,appContext);
            } else {
                String fullPath = TARGET_BASE_PATH + "/" + path;
                File dir = new File(fullPath);
                if (!dir.exists()) { // copy thouse dirs
                    if (!dir.mkdirs()) {
                        Log.i("tag", "could not create dir " + fullPath);
                    }
                }
                for (String asset : assets) {
                    String p;
                    if (path.equals("")) {
                        p = "";
                    } else {
                        p = path + "/";
                    }
                    assetsToFiles(TARGET_BASE_PATH, p + asset, copyType,appContext);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    public static void initUtils(String _path,Application _app){
        application = _app;
        binHome = _path;
    }

    public static void copyFile(String TARGET_BASE_PATH, String filename,Context appContext) {
        AssetManager assetManager = appContext.getAssets();

        InputStream in;
        OutputStream out;
        String newFileName = null;
        try {
            // Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            newFileName = TARGET_BASE_PATH + "/" + filename;
            out = new FileOutputStream(newFileName);
            byte[] buffer = new byte[8092];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of " + newFileName);
            Log.e("tag", "Exception in copyFile() " + e.toString());
        }
    }

    //Shows a toast to the user for information
    public static void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(application, message, duration);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    //Checks If File is present and has execution permission
    public static boolean checkFilePermissions(String path){
        File f = new File(path);
        return (f.exists() && f.canExecute() && f.canRead() && f.canWrite());
    }


}
