package com.draguve.droidducky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ScriptDatabase extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "scriptsManager";
    private static final String TABLE_SCRIPTS = "scripts";
    private static final String KEY_LANG = "lang";
    private static final String KEY_PATH = "path";

    public ScriptDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCRIPT_TABLE = "CREATE TABLE " + TABLE_SCRIPTS + "("
                + KEY_PATH + " TEXT PRIMARY KEY," + KEY_LANG + " TEXT)";
        db.execSQL(CREATE_SCRIPT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCRIPTS);
        // Create tables again
        onCreate(db);
    }

    void addContact(ScriptID script) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATH, script.getFilePath());
        values.put(KEY_LANG, script.getLanguage());
        db.insert(TABLE_SCRIPTS, null, values);
        db.close(); // Closing database connection
    }

    ScriptID getScript(String filePath) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SCRIPTS, new String[] { KEY_PATH,
                        KEY_LANG}, KEY_PATH + "=?",
                new String[] { filePath }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ScriptID scriptID = new ScriptID(cursor.getString(0),cursor.getString(1));
        // return contact
        return scriptID;
    }

    public List<ScriptID> getAllScripts() {
        List<ScriptID> scriptIDList = new ArrayList<ScriptID>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCRIPTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ScriptID contact = new ScriptID();
                contact.SetFilePath(cursor.getString(0));
                contact.setLanguage(cursor.getString(1));

                scriptIDList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return scriptIDList;
    }

    public int updateScript(ScriptID scriptID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATH, scriptID.getFilePath());
        values.put(KEY_LANG, scriptID.getLanguage());

        // updating row
        return db.update(TABLE_SCRIPTS, values, KEY_PATH + " = ?",
                new String[] { String.valueOf(scriptID.getFilePath()) });
    }

    public void deleteScript(ScriptID scriptID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCRIPTS, KEY_PATH + " = ?",
                new String[] { String.valueOf(scriptID.getFilePath()) });
        db.close();
    }
}