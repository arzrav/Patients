package com.neurofrank.portfolio.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.support.v4.provider.DocumentFile;

import com.neurofrank.portfolio.interfaces.CopyDirAsyncResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyDirAsyncTask extends AsyncTask<Void, Void, Void> {

    public CopyDirAsyncResponse delegate = null;

    private DocumentFile mSourceLocation;
    private DocumentFile mDestLocation;
    private Context mContext;

    public CopyDirAsyncTask(DocumentFile sourceDirectory, DocumentFile destDirectory, Context context) {
        mSourceLocation = sourceDirectory;
        mDestLocation = destDirectory;
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        CopyRecursive(mSourceLocation, mDestLocation, mContext);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        delegate.processFinish();
    }

    private void CopyRecursive(DocumentFile sourceDirectory, DocumentFile destDirectory, Context context) {

        for (DocumentFile child : sourceDirectory.listFiles()) {
            if (child.isDirectory()) {
                DocumentFile targetDir = FileOperation.CreateDirectory(destDirectory, child.getName());
                CopyRecursive(child, targetDir, context);
            } else {
                Uri fileToCopy = child.getUri();
                /*Check if a file exist in the specified directory*/
                String mimeType = context.getContentResolver().getType(fileToCopy);
                Cursor returnCursor = context.getContentResolver().query(fileToCopy, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                String fileName = returnCursor.getString(nameIndex);
                returnCursor.close();

                Boolean isFileExists = false;
                for (DocumentFile file : destDirectory.listFiles()) {
                    if (file.isFile()) {
                        String cmpMimeType = context.getContentResolver().getType(file.getUri());
                        Cursor cmpReturnCursor = context.getContentResolver().query(file.getUri(), null, null, null, null);
                        int cmpNameIndex = cmpReturnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        cmpReturnCursor.moveToFirst();
                        String cmpFileName = cmpReturnCursor.getString(cmpNameIndex);
                        cmpReturnCursor.close();
                        if (cmpFileName.equals(fileName) && cmpMimeType.equals(mimeType)) {
                            isFileExists = true;
                        }
                    }
                }
            /*If no match is found copy the file to the specified directory*/
                if (!isFileExists) {
                    DocumentFile file = destDirectory.createFile(mimeType, fileName);
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(fileToCopy);
                        OutputStream outputStream = context.getContentResolver().openOutputStream(file.getUri());

                        try {
                            byte[] buf = new byte[1024];
                            int len;

                            while ((len = inputStream.read(buf)) > 0) {
                                outputStream.write(buf, 0, len);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                outputStream.close();
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
