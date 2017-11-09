package com.neurofrank.portfolio.tag;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.neurofrank.portfolio.R;


public class TagRemoveDialogFragment extends DialogFragment {

    private Spinner mTagSpinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_remove_tag, null);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, TagLab.get(getActivity()).getTags());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTagSpinner = (Spinner) v.findViewById(R.id.dialog_remove_tag_spinner);
        mTagSpinner.setAdapter(adapter);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.remove_tag_dialog_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TagLab.get(getActivity()).removeTag(mTagSpinner.getSelectedItem().toString());
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setCancelable(true)
                .create();
    }
}
