package com.neurofrank.portfolio.reception;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.neurofrank.portfolio.database.PortfolioContract.*;
import com.neurofrank.portfolio.database.PortfolioDbHelper;
import com.neurofrank.portfolio.database.ReceptionCursorWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReceptionLab {

    private static ReceptionLab sReceptionLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ReceptionLab get(Context context) {
        if (sReceptionLab == null) {
            sReceptionLab = new ReceptionLab(context);
        }
        return sReceptionLab;
    }

    private ReceptionLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new PortfolioDbHelper(mContext).getWritableDatabase();
    }

    public void addReception(Reception reception) {
        ContentValues values = getContentValues(reception);
        mDatabase.insert(ReceptionTable.TABLE_NAME, null, values);
    }

    public void updateReception(Reception reception) {
        String uuidString = reception.getId().toString();
        ContentValues values = getContentValues(reception);
        mDatabase.update(ReceptionTable.TABLE_NAME, values, ReceptionTable.COLUMN_UUID + " = ?", new String[] {uuidString});
    }

    public void removeReception(UUID id) {
        String uuidString = id.toString();
        mDatabase.delete(ReceptionTable.TABLE_NAME, ReceptionTable.COLUMN_UUID + " = ?", new String[] {uuidString});
    }

    public List<Reception> getReceptions(UUID patientId) {
        List<Reception> receptions = new ArrayList<>();

        ReceptionCursorWrapper cursor = queryReceptions(
                ReceptionTable.COLUMN_PATIENT_UUID + " = ?",
                new String[] {patientId.toString()}
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                receptions.add(cursor.getReception());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return receptions;
    }

    public Reception getReception(UUID id) {
        ReceptionCursorWrapper cursor = queryReceptions(
                ReceptionTable.COLUMN_UUID + " = ?",
                new String[] {id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getReception();
        }finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Reception reception) {
        ContentValues values = new ContentValues();
        values.put(ReceptionTable.COLUMN_UUID, reception.getId().toString());
        values.put(ReceptionTable.COLUMN_PATIENT_UUID, reception.getPatientId().toString());
        if (reception.getTitle() != null) {
            values.put(ReceptionTable.COLUMN_TITLE, reception.getTitle());
        }
        if (reception.getDate() != null) {
            values.put(ReceptionTable.COLUMN_DATE, reception.getDate().toString());
        }
        if (reception.getHistory() != null) {
            values.put(ReceptionTable.COLUMN_HISTORY, reception.getHistory());
        }
        return values;
    }

    private ReceptionCursorWrapper queryReceptions(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ReceptionTable.TABLE_NAME,
                null, //Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null  //orderBy
        );
        return new ReceptionCursorWrapper(cursor);
    }
}
