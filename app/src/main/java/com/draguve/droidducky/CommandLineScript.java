package com.draguve.droidducky;

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
        return new Script(_name,_code,_lang);
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

