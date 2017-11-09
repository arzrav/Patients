package com.neurofrank.portfolio.util;

import android.content.res.Resources;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class Constants {

    // To restrict instantiation
    private Constants() {}

    public static final int REQUEST_CODE_EDIT_PATIENT = 0;
    public static final int REQUEST_CODE_DELETE_PATIENT = 1;
    public static final int REQUEST_CODE_DELETE_RECEPTION = 2;
    public static final int REQUEST_CODE_DELETE_IMAGE = 3;
    public static final int REQUEST_CODE_PATH_TO_DATA = 4;
    public static final int REQUEST_CODE_LOAD_MAIN_IMAGE = 5;
    public static final int REQUEST_CODE_LOAD_BEFORE_IMAGE = 6;
    public static final int REQUEST_CODE_LOAD_AFTER_IMAGE = 7;
    public static final int REQUEST_CODE_DATE_CHANGE = 8;
    public static final int REQUEST_CODE_SELECT_TAGS = 9;

    public static final int MOBILE_NUMBER_LENGTH = 11;

    public static final int SQUARE_SIZE = (Resources.getSystem().getDisplayMetrics().widthPixels < Resources.getSystem().getDisplayMetrics().heightPixels) ?
            Resources.getSystem().getDisplayMetrics().widthPixels / 2 : Resources.getSystem().getDisplayMetrics().heightPixels / 2;

    public static final DateTimeFormatter jodaTimeDefaultFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter jodaTimeDmySlashFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

    public static final String APP_PREFERENCES_PATH_TO_DATA = "path_to_data";
    public static final String APP_PREFERENCES_COMPLETED_FIRST_LAUNCH = "completed_first_launch";
    public static final String APP_STORAGE_NAME = "Portfolio";

    public static final String DIALOG_DELETE_PATIENT = "DialogDeletePatient";
    public static final String DIALOG_DELETE_RECEPTION = "DialogDeleteReception";
    public static final String DIALOG_DELETE_IMAGE = "DialogDeleteImage";
    public static final String DIALOG_NEW_TAG = "DialogNewTag";
    public static final String DIALOG_SELECT_TAGS = "DialogSelectTags";
    public static final String DIALOG_REMOVE_TAG = "DialogRemoveTag";

    public static final String CREATED = "created";
    public static final String CHANGED = "changed";
    public static final String DELETED = "deleted";
    public static final String IMAGE_DELETED = "imageDeleted";

    public static final String EXTRA_PATIENT_ID = "com.neurofrank.android.portfolio.patient_id";
    public static final String EXTRA_PATIENT_ACTION = "com.neurofrank.android.portfolio.patient_action";
    public static final String EXTRA_WHO_CREATED = "com.neurofrank.android.portfolio.who_created";
    public static final String ARG_PATIENT_ID = "patient_id";

    public static final String EXTRA_RECEPTION_ID = "com.neurofrank.android.portfolio.reception_id";
    public static final String ARG_RECEPTION_ID = "reception_id";

    public static final String EXTRA_DATE = "com.neurofrank.android.portfolio.date";
    public static final String ARG_DATE = "date";

    public static final String EXTRA_TAGS = "com.neurofrank.android.portfolio.tags";
    public static final String EXTRA_AND_TAGS_CONDITION = "com.neurofrank.android.portfolio.and_tags_condition";
    public static final String ARG_TAGS = "tags";
    public static final String ARG_AND_TAGS_CONDITION = "and_tags_condition";

    public static final String EXTRA_IMAGE = "com.neurofrank.android.portfolio.image";
    public static final String ARG_IMAGE = "image";

    public static final String ARG_ID = "id";
    public static final String ARG_TYPE = "type";
    public static final String EXTRA_DESCRIPTION = "com.neurofrank.android.portfolio.description";
    public static final String ARG_DESCRIPTION = "description";

    public static final String SPINNER_DEFAULT_TITLE = "Ничего не выбрано";
}
