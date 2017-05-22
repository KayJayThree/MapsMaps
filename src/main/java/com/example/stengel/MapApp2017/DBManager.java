package com.example.stengel.MapApp2017;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager {
    static final String KEY_ROWID = "_id";
    static final String KEY_TITLE = "title";
    static final String KEY_LAT = "lat";
    static final String KEY_LON = "lon";
    static final String TAG = "DBManager";
    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "maps";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE =
            "create table maps (_id integer primary key autoincrement, title text not null, lat text not null, lon text not null);";
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBManager(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS maps");
            onCreate(db);
        }
    }

    //---opens the database--- for writing data
    public DBManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // Open for reading data
    public DBManager openRead() throws SQLException {
        db = DBHelper.getReadableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    //---insert a location into the database---
    public long insertLocation(String title, String lat, String lon) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_LAT, lat);
        initialValues.put(KEY_LON, lon);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public Cursor getAllLocations() throws SQLException{
        // Get all information and return in a cursor
        return db.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_LAT, KEY_LON}, null, null, null, null, null);
    }

    //---retrieves a particular location---
    public Cursor getLocation(long rowId) throws SQLException {
        //this.open();
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_LAT, KEY_LON}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        //this.close();
        return mCursor;

    /*//method for select query
    public Cursor display() {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + maps, null);
        return res;
    }*/
    }
}
