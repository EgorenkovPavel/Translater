package com.epipasha.translater.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by Pavel on 20.03.2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    public static final String DB_NAME = "translater.db";

    public static final String TABLE_HISTORY = "history";
    public static final String TABLE_FAVORITES = "favorites";

    public static final String _ID = "_id";

    public static final String INPUT_TEXT = "input_text";
    public static final String INPUT_CODE = "input_code";
    public static final String OUTPUT_TEXT = "output_text";
    public static final String OUTPUT_CODE = "output_code";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_HISTORY
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + INPUT_TEXT + " TEXT,"
                + INPUT_CODE + " TEXT,"
                + OUTPUT_TEXT + " TEXT,"
                + OUTPUT_CODE + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITES
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + INPUT_TEXT + " TEXT,"
                + INPUT_CODE + " TEXT,"
                + OUTPUT_TEXT + " TEXT,"
                + OUTPUT_CODE + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
