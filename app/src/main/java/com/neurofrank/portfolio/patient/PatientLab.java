package com.neurofrank.portfolio.patient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.neurofrank.portfolio.database.PatientCursorWrapper;
import com.neurofrank.portfolio.database.PortfolioContract.PatientTable;
import com.neurofrank.portfolio.database.PortfolioDbHelper;
import com.neurofrank.portfolio.reception.Reception;
import com.neurofrank.portfolio.reception.ReceptionLab;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PatientLab {

    private static PatientLab sPatientLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static PatientLab get(Context context) {
        if (sPatientLab == null) {
            sPatientLab = new PatientLab(context);
        }
        return sPatientLab;
    }

    private PatientLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new PortfolioDbHelper(mContext).getWritableDatabase();
    }

    public void addPatient(Patient patient) {
        ContentValues values = getContentValues(patient);
        mDatabase.insert(PatientTable.TABLE_NAME, null, values);
    }

    public void updatePatient(Patient patient) {
        String uuidString = patient.getId().toString();
        ContentValues values = getContentValues(patient);
        mDatabase.update(PatientTable.TABLE_NAME, values, PatientTable.COLUMN_UUID + " = ?", new String[] {uuidString});
    }

    public void removePatient(UUID id) {
        String uuidString = id.toString();
        ReceptionLab receptionLab = ReceptionLab.get(mContext);
        mDatabase.delete(PatientTable.TABLE_NAME, PatientTable.COLUMN_UUID + " = ?", new String[] {uuidString});
        List<Reception> receptions = receptionLab.getReceptions(id);
        for (Reception reception : receptions) {
            receptionLab.removeReception(reception.getId());
        }
    }

    /**
     * Gets patients from database.
     * @param table if null, then it will be default Patient table
     * @param columns if null, then selected all columns
     * @param whereClause can be null
     * @param whereArgs can be null
     * @param groupBy can be null
     * @param having can be null
     * @param orderBy can be null
     * @return List of patients
     */
    public List<Patient> getPatients(String table, String[] columns, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy) {
        List<Patient> patients = new ArrayList<>();

        PatientCursorWrapper cursor = queryPatients(table, columns, whereClause, whereArgs, groupBy, having, orderBy);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                patients.add(cursor.getPatient());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return patients;
    }

    public Patient getPatient(UUID id) {
        PatientCursorWrapper cursor = queryPatients(
                null,
                null,
                PatientTable.COLUMN_UUID + " = ?",
                new String[] {id.toString()},
                null,
                null,
                null
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getPatient();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Patient patient) {
        ContentValues values = new ContentValues();
        values.put(PatientTable.COLUMN_UUID, patient.getId().toString());
        if (patient.getFirstName() != null) {
            values.put(PatientTable.COLUMN_FIRST_NAME, patient.getFirstName());
        }
        if (patient.getMiddleName() != null) {
            values.put(PatientTable.COLUMN_MIDDLE_NAME, patient.getMiddleName());
        }
        if (patient.getLastName() != null) {
            values.put(PatientTable.COLUMN_LAST_NAME, patient.getLastName());
        }
        if (patient.getBirthday() != null) {
            values.put(PatientTable.COLUMN_BIRTHDAY, patient.getBirthday().toString());
        }
        if (patient.getMobilePhone() != null) {
            values.put(PatientTable.COLUMN_MOBILE_PHONE, patient.getMobilePhone());
        }
        if (patient.getEmail() != null) {
            values.put(PatientTable.COLUMN_EMAIL, patient.getEmail());
        }
        if (patient.getHistory() != null) {
            values.put(PatientTable.COLUMN_HISTORY, patient.getHistory());
        }
        return values;
    }

    private PatientCursorWrapper queryPatients(String table, String[] columns, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy) {
        if (table == null) {
            table = PatientTable.TABLE_NAME;
        }
        Cursor cursor = mDatabase.query(
                table, //table
                columns, //columns - null выбирает все столбцы
                whereClause, //whereClause
                whereArgs, //whereArgs
                groupBy, //groupBy
                having, //having
                orderBy //orderBy
        );
        return new PatientCursorWrapper(cursor);
    }

}
