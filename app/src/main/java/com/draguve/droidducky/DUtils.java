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

    public static void assetsToFiles(String TARGET_BASE_PATH, String path, String copyType,Context appContext) {
        AssetManager assetManager = appContext.getAssets();
        String assets[];
        try {
            // Log.i("tag", "assetsTo" + copyType +"() "+path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(TARGET_BASE_PATH, path,appContext);
            } else {
                String fullPath = TARGET_BASE_PATH + "/" + path;
                // Log.i("tag", "path="+fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && pathIsAllowed(path, copyType)) { // copy thouse dirs
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
                    if (pathIsAllowed(path, copyType)) {
                        assetsToFiles(TARGET_BASE_PATH, p + asset, copyType,appContext);
                    }
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    public static Boolean pathIsAllowed(String path, String copyType) {
        // never copy images, sounds or webkit
        if (!path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit")) {
            if (copyType.equals("sdcard")) {
                if (path.equals("")) {
                    return true;
                }
                return false;
            }
            if (copyType.equals("data")) {
                if (path.equals("")) {
                    return true;
                } else if (path.startsWith("scripts")) {
                    return true;
                } else if (path.startsWith("wallpapers")) {
                    return true;
                } else if (path.startsWith("etc")) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
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

    public static void showToast(String message ,Application application) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(application, message, duration);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


}
