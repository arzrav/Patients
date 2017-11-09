package com.neurofrank.portfolio.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.neurofrank.portfolio.reception.Reception;
import com.neurofrank.portfolio.database.PortfolioContract.ReceptionTable;

import java.util.UUID;

import static com.neurofrank.portfolio.util.Constants.jodaTimeDefaultFormatter;


public class ReceptionCursorWrapper extends CursorWrapper {

    public ReceptionCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Reception getReception() {
        String uuidString = getString(getColumnIndex(ReceptionTable.COLUMN_UUID));
        String uuidPatientString = getString(getColumnIndex(ReceptionTable.COLUMN_PATIENT_UUID));
        String title = getString(getColumnIndex(ReceptionTable.COLUMN_TITLE));
        String date = getString(getColumnIndex(ReceptionTable.COLUMN_DATE));
        String history = getString(getColumnIndex(ReceptionTable.COLUMN_HISTORY));

        Reception reception = new Reception(UUID.fromString(uuidString), UUID.fromString(uuidPatientString));
        if (title != null) {
            reception.setTitle(title);
        }
        if (date != null) {
            try {
                reception.setDate(jodaTimeDefaultFormatter.parseLocalDate(date));
            } catch (Exception e) {}
        }
        if (history != null) {
            reception.setHistory(history);
        }
        return reception;
    }
}
