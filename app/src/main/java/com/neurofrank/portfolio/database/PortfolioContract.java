package com.neurofrank.portfolio.database;

import android.provider.BaseColumns;

public final class PortfolioContract {
    private PortfolioContract() {}

    public static final class PatientTable implements BaseColumns {
        public static final String TABLE_NAME = "patients";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_MIDDLE_NAME = "middle_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_BIRTHDAY = "birthday";
        public static final String COLUMN_MOBILE_PHONE = "mobile_phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_HISTORY = "history";
    }

    public static final class ReceptionTable implements BaseColumns {
        public static final String TABLE_NAME = "receptions";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_PATIENT_UUID = "patient_uuid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_HISTORY = "history";
    }

    public static final class TagTable implements BaseColumns {
        public static final String TABLE_NAME = "tags";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TAG = "tag";
    }
}
