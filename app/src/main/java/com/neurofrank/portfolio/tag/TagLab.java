package com.neurofrank.portfolio.tag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.neurofrank.portfolio.database.PortfolioContract.TagTable;
import com.neurofrank.portfolio.database.PortfolioDbHelper;
import com.neurofrank.portfolio.database.TagCursorWrapper;

import java.util.ArrayList;
import java.util.List;

public class TagLab {

    private static TagLab sTagLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static TagLab get(Context context) {
        if (sTagLab == null) {
            sTagLab = new TagLab(context);
        }
        return sTagLab;
    }

    private TagLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new PortfolioDbHelper(mContext).getWritableDatabase();
    }

    public void addTag(String tag) {
        Boolean isTagExists = false;
        List<String> tags = TagLab.get(mContext).getTags();
        for (String tagFromBase: tags) {
            if (tag.equalsIgnoreCase(tagFromBase)) {
                isTagExists = true;
            }
        }
        if (!isTagExists) {
            ContentValues values = getContentValues(tag);
            mDatabase.insert(TagTable.TABLE_NAME, null, values);
        }
    }

    public void removeTag(String tag) {
        mDatabase.delete(TagTable.TABLE_NAME, TagTable.COLUMN_TAG + " = ?", new String[] {tag});
    }

    public List<String> getTags() {
        List<String> tags = new ArrayList<>();
        TagCursorWrapper cursor = queryTags(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tags.add(cursor.getTag());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return tags;
    }

    private static ContentValues getContentValues(String tag) {
        ContentValues values = new ContentValues();
        values.put(TagTable.COLUMN_TAG, tag);
        return values;
    }

    private TagCursorWrapper queryTags(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TagTable.TABLE_NAME,
                null, //Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                TagTable.COLUMN_TAG  //orderBy
        );
        return new TagCursorWrapper(cursor);
    }
}
