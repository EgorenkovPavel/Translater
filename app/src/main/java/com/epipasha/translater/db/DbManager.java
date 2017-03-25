package com.epipasha.translater.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Pavel on 20.03.2017.
 */

public class DbManager {

    private static DbManager manager;

    private SQLiteDatabase db;
    private final Context context;
    private DbHelper dbHelper;

    public static DbManager getInstance(Context context){
        if (manager == null){
            manager = new DbManager(context);
        }
        return manager;
    }

    private DbManager(Context context){
        this.context = context;
        dbHelper = new DbHelper(context);
    }

    public void open(){
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public Cursor getHistory(){
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TABLE_HISTORY, null, null, null, null, null, DbHelper._ID + " DESC");
        //close();
        return cursor;
    }

    public int addHistory(String inputText, String inputCode, String outputText, String outputCode){
        open();
        ContentValues values = new ContentValues(4);

        values.put(DbHelper.INPUT_TEXT, inputText);
        values.put(DbHelper.INPUT_CODE, inputCode);
        values.put(DbHelper.OUTPUT_TEXT, outputText);
        values.put(DbHelper.OUTPUT_CODE, outputCode);

        int res = (int)db.insertOrThrow(DbHelper.TABLE_HISTORY, null, values);
        close();
        return res;
    }

}
