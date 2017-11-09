package com.neurofrank.portfolio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.neurofrank.portfolio.database.PortfolioContract.*;


public class PortfolioDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = PortfolioDbHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "portfolio.db";
    private static final int DATABASE_VERSION = 1;

    public PortfolioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PATIENTS_TABLE = "CREATE TABLE " + PatientTable.TABLE_NAME + " ("
                + PatientTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PatientTable.COLUMN_UUID + " TEXT, "
                + PatientTable.COLUMN_FIRST_NAME + " TEXT, "
                + PatientTable.COLUMN_MIDDLE_NAME + " TEXT, "
                + PatientTable.COLUMN_LAST_NAME + " TEXT, "
                + PatientTable.COLUMN_BIRTHDAY + " TEXT, "
                + PatientTable.COLUMN_MOBILE_PHONE + " TEXT, "
                + PatientTable.COLUMN_EMAIL + " TEXT, "
                + PatientTable.COLUMN_HISTORY + " TEXT);";
        db.execSQL(SQL_CREATE_PATIENTS_TABLE);
        String SQL_CREATE_RECEPTIONS_TABLE = "CREATE TABLE " + ReceptionTable.TABLE_NAME + " ("
                + ReceptionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReceptionTable.COLUMN_UUID + " TEXT, "
                + ReceptionTable.COLUMN_PATIENT_UUID + " TEXT, "
                + ReceptionTable.COLUMN_TITLE + " TEXT, "
                + ReceptionTable.COLUMN_DATE + " TEXT, "
                + ReceptionTable.COLUMN_HISTORY + " TEXT);";
        db.execSQL(SQL_CREATE_RECEPTIONS_TABLE);
        String SQL_CREATE_TAGS_TABLE = "CREATE TABLE " + TagTable.TABLE_NAME + " ("
                + TagTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TagTable.COLUMN_TAG + " TEXT);";
        db.execSQL(SQL_CREATE_TAGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS " + PatientTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF IT EXISTS " + ReceptionTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF IT EXISTS " + TagTable.TABLE_NAME);
        onCreate(db);
    }
}
