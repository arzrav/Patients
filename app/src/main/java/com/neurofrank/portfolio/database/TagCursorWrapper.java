package com.neurofrank.portfolio.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.neurofrank.portfolio.database.PortfolioContract.TagTable;


public class TagCursorWrapper extends CursorWrapper {

    public TagCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getTag() {
        String tag = getString(getColumnIndex(TagTable.COLUMN_TAG));
        if (tag != null) {
            return tag;
        } else {
            return "";
        }
    }
}
