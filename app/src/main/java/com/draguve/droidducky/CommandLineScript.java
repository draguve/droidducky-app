package com.draguve.droidducky;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Draguve on 3/30/2018.
 */

public class CommandLineScript {
    private String _id;
    private String _name;
    private String _code;
    private String _lang;
    private OperatingSystem _os;
    private Integer delayBetweenLines = 200;
    private Integer initalDelay = 1000;

    public CommandLineScript(String id,String name,String code,String lang,OperatingSystem os){
        _id = id;
        _name = name;
        _code = code;
        _lang = lang;
        _os = os;
    }

    public CommandLineScript(String name,String code,String lang,OperatingSystem os){
        _id = UUID.randomUUID().toString();
        _name = name;
        _code = code;
        _lang = lang;
        _os = os;
    }

    public void setName(String name){
        _name = name;
    }

    public void setCode(String code){
        _code = code;
    }

    public void setLang(String lang) { _lang = lang; }

    public void setOS(OperatingSystem os){
        _os = os;
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

    public String getLang() { return _lang;}

    public OperatingSystem getOS(){
        return _os;
    }

    public Script convertToScript(){
        //Change this to convert the code according to the system
        String[] converted_code = _code.replace("\r","").split("\n");
        ArrayList<String> finalCode = new ArrayList<String>();
        String compiledCode = "";
        switch(_os){
            case WINDOWS:
                finalCode.add("DELAY " + initalDelay);
                finalCode.add("GUI r");
                finalCode.add("DELAY " + initalDelay);
                finalCode.add("String cmd");
                finalCode.add("DELAY " + initalDelay);
                finalCode.add("ENTER \nDELAY 1000");
                break;
            case DARWIN:
                finalCode.add("DELAY " + initalDelay+ "\n" +
                        "GUI space\n" +
                        "DELAY 500\n" +
                        "ALT F2\n" +
                        "DELAY 500\n" +
                        "BACKSPACE\n" +
                        "DELAY 100\n" +
                        "STRING terminal\n"+
                        "ENTER\n" +
                        "DELAY 3000");
                break;
            case LINUX:
                finalCode.add("DELAY " + initalDelay);
                finalCode.add("ALT F2\n" +
                        "DELAY 500\n" +
                        "STRING xterm\n" +
                        "DELAY 500\n" +
                        "ENTER\n" +
                        "DELAY 750");
                break;
            case WINDOWS_ADMIN:
                finalCode.add("DELAY 3000\n" +
                        "CONTROL ESCAPE\n" +
                        "DELAY 1000\n" +
                        "STRING cmd\n" +
                        "DELAY 1000\n" +
                        "CTRL-SHIFT ENTER\n" +
                        "DELAY 1000\n" +
                        "ALT y\n" +
                        "DELAY 300\n" +
                        "ENTER\n" + "DELAY 1000");
                break;
        }
        for(String line : converted_code){
            finalCode.add("STRING "+line);
            finalCode.add("DELAY " + delayBetweenLines);
            finalCode.add("ENTER");
            finalCode.add("DELAY " + delayBetweenLines);
        }
        for(String line : finalCode){
            compiledCode += line + "\n";
        }
        return new Script(_name,compiledCode,_lang);
    }

    public String getString(){
        return _os.getString();
    }

    public enum OperatingSystem{
        LINUX,
        WINDOWS,
        DARWIN,
        WINDOWS_ADMIN;

        public static OperatingSystem fromInteger(int x) {
            switch(x) {
                case 0:
                    return LINUX;
                case 1:
                    return WINDOWS;
                case 2:
                    return DARWIN;
                case 3:
                    return WINDOWS_ADMIN;
            }
            return null;
        }

        public String getString(){
            switch (this){
                case LINUX:
                    return "Linux";
                case WINDOWS:
                    return "Windows";
                case DARWIN:
                    return "Darwin";
                case WINDOWS_ADMIN:
                    return "Windows-UAC";
            }
            return null;
        }
    }
}

