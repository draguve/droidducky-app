package com.draguve.droidducky;

public class ScriptID {
    String _FilePath;
    String _Language;

    public ScriptID(){   }

    public ScriptID(String filePath, String lang){
        this._FilePath = filePath;
        this._Language = lang;
    }

    public String getFilePath(){
        return this._FilePath;
    }

    public void SetFilePath(String name){
        this._FilePath = name;
    }

    public String getLanguage(){
        return this._Language;
    }

    public void setLanguage(String lang){
        this._Language = lang;
    }
}
