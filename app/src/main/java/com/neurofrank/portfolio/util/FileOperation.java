package com.neurofrank.portfolio.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;

import static com.neurofrank.portfolio.util.Constants.APP_PREFERENCES_PATH_TO_DATA;
import static com.neurofrank.portfolio.util.Constants.APP_STORAGE_NAME;


public class FileOperation {

    /**
     * Method that deletes file or folder and all contained files.
     * Executes asynchronously.
     * @param fileOrDirectory The file or folder to delete.
     */
    public static void DeleteRecursive(DocumentFile fileOrDirectory) {
        DeleteRecursiveAsyncTask deleteTask = new DeleteRecursiveAsyncTask();
        deleteTask.execute(fileOrDirectory);
    }

    /**
     * The method copies files to the specified directory.
     * Executes asynchronously.
     * @param sourceDirectory source directory
     * @param destDirectory target directory
     * @param context application context
     */
    public static void CopyFiles(DocumentFile sourceDirectory, DocumentFile destDirectory, Context context) {
        CopyDirAsyncTask copyTask = new CopyDirAsyncTask(sourceDirectory, destDirectory, context);
        copyTask.execute();
    }

    /**
     * Create subdirectory in parent directory and return creating subdirectory
     * or return null if directory already exists.
     * @param parentDirectory directory where u want to create subdirectory
     * @param name subdirectory name
     * @return subdirectory if everything is ok or null otherwise
     */
    public static DocumentFile CreateDirectory(DocumentFile parentDirectory, String name) {
        Boolean isDirectoryExists = false;
        for (DocumentFile file : parentDirectory.listFiles()) {
            if (file.isDirectory() && file.getName().equals(name)) {
                isDirectoryExists = true;
            }
        }
        if (!isDirectoryExists) {
            return parentDirectory.createDirectory(name);
        } else {
            return null;
        }
    }

    /**
     * Method that gets path to application data storage selected by the user on first launch
     * from shared preferences and then return user-selected directory.
     * @param context application context
     * @return user-selected directory
     */
    public static DocumentFile GetPickedDirectory(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Uri uri =  Uri.parse(sharedPreferences.getString(APP_PREFERENCES_PATH_TO_DATA, ""));
        return DocumentFile.fromTreeUri(context, uri);
    }

    /**
     * Method gets main application data storage where contains patients and etc.
     * @param context application context
     * @return main data storage directory
     */
    public static DocumentFile GetStorageDirectory(Context context) {
        DocumentFile pickedDir = GetPickedDirectory(context);
        return pickedDir.findFile(APP_STORAGE_NAME);
    }

}
