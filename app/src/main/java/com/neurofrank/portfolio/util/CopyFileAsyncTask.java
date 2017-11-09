package com.neurofrank.portfolio.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.support.v4.provider.DocumentFile;

import com.neurofrank.portfolio.interfaces.CopyFileAsyncResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CopyFileAsyncTask extends AsyncTask<Uri, Void, DocumentFile> {

    public CopyFileAsyncResponse delegate = null;

    private DocumentFile mDestDirectory;
    private Context mContext;

    public CopyFileAsyncTask(DocumentFile destDirectory, Context context) {
        mDestDirectory = destDirectory;
        mContext = context;
    }

    @Override
    protected DocumentFile doInBackground(Uri... uris) {
        for (Uri fileToCopy : uris) {
            /*Check if a file exist in the specified directory*/
            String mimeType = mContext.getContentResolver().getType(fileToCopy);
            Cursor returnCursor = mContext.getContentResolver().query(fileToCopy, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String fileName = returnCursor.getString(nameIndex);
            returnCursor.close();

            Boolean isFileExists = false;
            for (DocumentFile file : mDestDirectory.listFiles()) {
                if (file.isFile()) {
                    String cmpMimeType = mContext.getContentResolver().getType(file.getUri());
                    Cursor cmpReturnCursor = mContext.getContentResolver().query(file.getUri(), null, null, null, null);
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
                DocumentFile file = mDestDirectory.createFile(mimeType, fileName);
                try {
                    InputStream inputStream = mContext.getContentResolver().openInputStream(fileToCopy);
                    OutputStream outputStream = mContext.getContentResolver().openOutputStream(file.getUri());

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
                return file;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(DocumentFile documentFile) {
        super.onPostExecute(documentFile);
        if (documentFile != null) {
            delegate.processFinish(documentFile, mDestDirectory);
        }
    }
}
