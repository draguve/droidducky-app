package com.draguve.droidducky;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Draguve on 1/2/2018.
 */

public class Script {
    private String _id;
    private String _name;
    private String _code;

    public Script(String id,String name,String code){
        _id = id;
        _name = name;
        _code = code;
    }

    public Script(String name,String code){
        _id = UUID.randomUUID().toString();
        _name = name;
        _code = code;
    }

    public void setName(String name){
        _name = name;
    }

    public void setCode(String code){
        _code = code;
    }

    public String getName(){
        return  _name;
    }

    public String getCode(){
        return _code;
    }

    public String getID(){
        return _id;
    }

    public void executeCode(Context context){
        ArrayList<String> duckyLines = new ArrayList<>(Arrays.asList(_code.split("\n")));
        duckyLines = DuckConverter.convert(duckyLines,context);
        TheExecuter.injectKeystrokes(duckyLines);
    }
}
