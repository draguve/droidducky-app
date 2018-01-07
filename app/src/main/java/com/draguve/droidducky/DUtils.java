package com.draguve.droidducky;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Draguve on 9/23/2017.
 */

public class DUtils {

    public static String binHome;

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

    public static void initUtils(String _path){
        binHome = _path;
    }

    public static void copyFile(String TARGET_BASE_PATH, String filename,Context appContext) {
        AssetManager assetManager = appContext.getAssets();

        InputStream in;
        OutputStream out;
        String newFileName = null;
        try {
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


    public static boolean checkForFiles() {
        if (DUtils.checkFilePermissions(binHome + "/hid-gadget-test")) {
            return true;
        } else {
            return false;
        }
    }

    public static void setupFilesForInjection(Context context){
        binHome = "/data/data/"+context.getApplicationContext().getPackageName()+"/files";
        DUtils.initUtils(binHome);
        assetsToFiles(binHome,"","data",context);
        String command = "chmod 755 " + binHome + "/hid-gadget-test";
        TheExecuter.runAsRoot(command);
    }

    //Checks If File is present and has execution permission
    public static boolean checkFilePermissions(String path){
        File f = new File(path);
        return (f.exists() && f.canExecute() && f.canRead() && f.canWrite());
    }

    public void addFileToCodes(String filename,Context appContext){
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path,"/DroidDucky/"+filename.trim());
        String finalCode = "";
        if(file.exists()){
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String receiveString = "";
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    finalCode += receiveString+"\n";
                }
                bufferedReader.close();
                Script toAdd = new Script(filename,finalCode);
                ScriptsManager db = new ScriptsManager(appContext);
                db.addScript(toAdd);
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }
        }else{
            Toast.makeText(appContext,"Can't Find File",Toast.LENGTH_SHORT);
        }
    }
}
