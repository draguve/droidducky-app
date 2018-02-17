package com.draguve.droidducky;

/**
 * Created by draguve on Rumsha's Laptop on 1/1/18.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class DuckConverter {


    /* contains the keyboard configuration */
    private static Properties keyboardProps = new Properties();
    /* contains the language layout */
    private static Properties layoutProps = new Properties();
    /* contains the commands configuration */
    private static Properties commandProps = new Properties();
    private static int defaultDelay = 200;
    private static String lastLine;
    static Context mAppContext = null;

    public static ArrayList<String> convert(ArrayList<String> DuckLines,Context appContext){
        Properties properties = new Properties();
        Properties lang = new Properties();
        mAppContext = appContext;
        try{
            loadProperties("us",appContext);
        }catch(IOException e){
            Log.e("DuckConverter",e.toString());
        }
        ArrayList<String> letters = new ArrayList<>();
        for(String line: DuckLines){
            letters.addAll(convertLine(line,properties,letters));
            lastLine=line;
        }
        return letters;
    }

    public static void loadAllProperties(String lang,Context context) throws IOException {
        if(keyboardProps==null)
            keyboardProps = loadProperties("keyboard",context);
        layoutProps = loadProperties(lang,context);
        if(commandProps==null)
            commandProps = loadProperties("commands",context);
    }

    public static Properties loadProperties(String file, Context context) throws IOException {
        String filename = file + ".properties";
        Properties prop = new Properties();
        if(context==null){
            Log.e("DuckConverter","Context is Null");
        }
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(filename);
        if(inputStream==null){
            Log.e("DuckConverter","Language not found");
        }
        prop.load(inputStream);
        return prop;
    }

    public static ArrayList<String> convertLine(String line,Properties properties,ArrayList<String> allLetters) {
        ArrayList<String> letters = new ArrayList<>();
        String[] words = line.trim().split(" ");
        if(words[0].trim().toUpperCase().equals("STRING")){
            return convertString(line.trim().substring(6),properties,true);
        }else if(words[0].trim().toUpperCase().equals("REPEAT")){
            int numberOfTime=Integer.parseInt(words[1]);
            for(int i=0;i<numberOfTime;i++){
                allLetters.addAll(convertLine(lastLine,properties,allLetters));
            }
            return letters;
        }else if(words[0].trim().toUpperCase().equals("REM")){
            letters.add("\u0001"+line.substring(3).trim());
            return letters;
        }else if(words[0].trim().toUpperCase().equals("DELAY")){
            letters.add("\u0002"+line.substring(5).trim());
            return letters;
        }else if(words[0].trim().toUpperCase().equals("DEFAULTDELAY")){
            letters.add("\u0002"+"200");
            return letters;
        }else if(words[0].trim().toUpperCase().equals("DEFAULT_DELAY")){
            letters.add("\u0002"+"200");
            return letters;
        }else if(words[0].trim().toUpperCase().equals("WRITE_FILE")){
            File path = Environment.getExternalStorageDirectory();
            File file = new File(path,"/DroidDucky/code/"+words[1].trim());
            if(file.exists()){
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String receiveString = "";
                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        //Log.d("DuckConverter",receiveString);
                        letters.addAll(convertString(receiveString,properties,false));
                        letters.add("enter");
                    }
                    bufferedReader.close();
                }
                catch (FileNotFoundException e) {
                    Log.e("login activity", "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.e("login activity", "Can not read file: " + e.toString());
                }
            }else{
                Toast.makeText(mAppContext,"Can't Find File , Ignoring File ",Toast.LENGTH_SHORT);
            }
            return letters;
        }else{
            letters.add(convertCommand(line.trim().split(" "),properties));
            return letters;
        }
    }
}
