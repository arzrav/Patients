package com.neurofrank.portfolio.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.neurofrank.portfolio.patient.Patient;
import com.neurofrank.portfolio.database.PortfolioContract.PatientTable;

import java.util.UUID;

import static com.neurofrank.portfolio.util.Constants.jodaTimeDefaultFormatter;


public class PatientCursorWrapper extends CursorWrapper {

    public PatientCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Patient getPatient() {
        String uuidString = getString(getColumnIndex(PatientTable.COLUMN_UUID));
        String firstName = getString(getColumnIndex(PatientTable.COLUMN_FIRST_NAME));
        String middleName = getString(getColumnIndex(PatientTable.COLUMN_MIDDLE_NAME));
        String lastName = getString(getColumnIndex(PatientTable.COLUMN_LAST_NAME));
        String birthday = getString(getColumnIndex(PatientTable.COLUMN_BIRTHDAY));
        String mobilePhone = getString(getColumnIndex(PatientTable.COLUMN_MOBILE_PHONE));
        String email = getString(getColumnIndex(PatientTable.COLUMN_EMAIL));
        String history = getString(getColumnIndex(PatientTable.COLUMN_HISTORY));

        Patient patient = new Patient(UUID.fromString(uuidString));
        if (firstName != null) {
            patient.setFirstName(firstName);
        }
        if (middleName != null) {
            patient.setMiddleName(middleName);
        }
        if (lastName != null) {
            patient.setLastName(lastName);
        }
        if (birthday != null) {
            try {
                patient.setBirthday(jodaTimeDefaultFormatter.parseLocalDate(birthday));
            } catch (Exception e) {}
        }
        if (mobilePhone != null) {
            patient.setMobilePhone(mobilePhone);
        }
        if (email != null) {
            patient.setEmail(email);
        }

        if (history != null) {
            patient.setHistory(history);
        }

        return patient;
    }
}
