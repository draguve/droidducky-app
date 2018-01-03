package com.draguve.droidducky;

/**
 * Created by draguve on Rumsha's Laptop on 1/1/18.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class DuckConverter {

    static String lastLine="";

    public static ArrayList<String> convert(ArrayList<String> DuckLines,Context appContext){
        Properties properties = new Properties();
        try{
            properties = loadProperties(appContext);
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

    public static Properties loadProperties(Context context) throws IOException {
        Properties prop = new Properties();
        if(context==null){
            Log.e("DuckConverter","Context is Null");
        }
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("keyboard.properties");
        if(inputStream==null){
            Log.e("DuckConverter","InputStream is Null");
        }
        prop.load(inputStream);
        return prop;
    }

    public static ArrayList<String> convertLine(String line,Properties properties,ArrayList<String> allLetters) {
        ArrayList<String> letters = new ArrayList<>();
        String[] words = line.trim().split(" ");
        if(words[0].trim().toUpperCase().equals("STRING")){
            return convertString(line.trim().substring(6),properties);
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
        }else{
            letters.add(convertCommand(line.trim().split(" "),properties));
            return letters;
        }
    }

    public static ArrayList<String> convertString(String line,Properties properties){
        line = line.trim();
        ArrayList<String> letters = new ArrayList<>();
        for(char letter : line.toCharArray()){
            letters.add(convertChar(letter,properties));
        }
        return letters;
    }

    public static String convertChar(char letter,Properties properties){
        if(Character.isLetterOrDigit(letter)){
            if(Character.isUpperCase(letter)){
                return "left-shift "+ Character.toLowerCase(letter);
            }else{
                return ""+letter;
            }
        }else{
            String value = properties.getProperty(""+letter,"");
            if(value != null){
                return value;
            }
        }
        return "";
    }

    public static String convertCommand(String[] words,Properties properties){
        if(words.length>1){
            String word = words[0].trim().toUpperCase();
            word = properties.getProperty(word,"");
            return word + " " + convertCommand(Arrays.copyOfRange(words,1,words.length),properties);
        }else{
            if(words[0].length()==1){
                return ""+convertChar(words[0].charAt(0),properties);
            }else{
                return properties.getProperty(words[0].trim().toUpperCase(),"");
            }
        }
    }
}
