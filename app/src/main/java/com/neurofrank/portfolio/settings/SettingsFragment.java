package com.neurofrank.portfolio.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;

import com.neurofrank.portfolio.R;
import com.neurofrank.portfolio.interfaces.CopyDirAsyncResponse;
import com.neurofrank.portfolio.util.CopyDirAsyncTask;
import com.neurofrank.portfolio.util.FileOperation;

import java.util.Objects;

import static com.neurofrank.portfolio.util.Constants.APP_PREFERENCES_COMPLETED_FIRST_LAUNCH;
import static com.neurofrank.portfolio.util.Constants.APP_PREFERENCES_PATH_TO_DATA;
import static com.neurofrank.portfolio.util.Constants.APP_STORAGE_NAME;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_PATH_TO_DATA;


public class SettingsFragment extends PreferenceFragment implements CopyDirAsyncResponse {

    private DocumentFile mSourceDir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        Preference pref = findPreference("pref_key_path_to_data");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = preference.getIntent();
                intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                startActivityForResult(intent, REQUEST_CODE_PATH_TO_DATA);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PATH_TO_DATA) {
            if (data == null) {
                return;
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (Objects.equals(sharedPreferences.getString(APP_PREFERENCES_PATH_TO_DATA, ""), data.getData().toString())) {
                return;
            }
            mSourceDir = FileOperation.GetStorageDirectory(getActivity());
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putBoolean(APP_PREFERENCES_COMPLETED_FIRST_LAUNCH, true);
            DocumentFile pickedDir = DocumentFile.fromTreeUri(getActivity(), data.getData());
            getActivity().grantUriPermission(getActivity().getPackageName(), data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getActivity().getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            DocumentFile destDir = FileOperation.CreateDirectory(pickedDir, APP_STORAGE_NAME);
            editor.putString(APP_PREFERENCES_PATH_TO_DATA, data.getData().toString());
            editor.apply();
            CopyDirAsyncTask copyTask = new CopyDirAsyncTask(mSourceDir, destDir, getActivity());
            copyTask.delegate = this;
            copyTask.execute();
        }
    }

    @Override
    public void processFinish() {
        FileOperation.DeleteRecursive(mSourceDir);
    }
}
