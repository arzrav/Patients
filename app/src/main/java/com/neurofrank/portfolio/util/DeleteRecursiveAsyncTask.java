package com.neurofrank.portfolio.util;

import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;


public class DeleteRecursiveAsyncTask extends AsyncTask<DocumentFile, Void, Void> {
    @Override
    protected Void doInBackground(DocumentFile... filesOrDirectories) {
        for (DocumentFile fileOrDirectory : filesOrDirectories) {
            DeleteRecursive(fileOrDirectory);
        }
        return null;
    }

    private void DeleteRecursive(DocumentFile fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (DocumentFile child : fileOrDirectory.listFiles()) {
                DeleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
}
