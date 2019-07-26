package com.openogy.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class UserLoginTokenDb extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserToken.db";
    private static final int DATABASE_VERSION = 1;
    public static final String USER_TABLE_NAME = "user";
    public static final String USER_COLUMN_TOKEN = "token";

    public UserLoginTokenDb(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + USER_TABLE_NAME + "(" +
                USER_COLUMN_TOKEN + " TEXT)"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addToken(String str) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_TOKEN, str);
        db.insert(USER_TABLE_NAME,null, values);
        db.close();
    }

    public void removeToken() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER_TABLE_NAME,null,null);
        db.close();
    }

    public String getToken() {
        String arrData[] = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + USER_TABLE_NAME;
        Cursor cursor = db.query(USER_TABLE_NAME, new String[] { "*" },
                null,
                null, null, null, null, null);;
        if(cursor != null)
        {
            if (cursor.moveToFirst()) {
                arrData = new String[cursor.getColumnCount()];
                arrData[0] = cursor.getString(0); // logintoken
            }
            cursor.close();
            if(arrData!=null && arrData.length > 0)
                return arrData[0].replace("\"","");
            else
                return null;
        }
        else
            return null;

    }
}
