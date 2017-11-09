package com.neurofrank.portfolio.tag;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.neurofrank.portfolio.R;
import com.neurofrank.portfolio.util.RegexInputFilter;


public class TagNewDialogFragment extends DialogFragment {

    private EditText mNewTagEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_tag, null);
        RegexInputFilter nameFilter = new RegexInputFilter("[А-Яа-яёЁA-Za-z -]+");
        mNewTagEditText = (EditText) v.findViewById(R.id.dialog_new_tag_edit_text);
        mNewTagEditText.setFilters(new InputFilter[]{
                nameFilter,
                new InputFilter.LengthFilter(50)
        });
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.new_tag_dialog_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TagLab.get(getActivity()).addTag(mNewTagEditText.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setCancelable(true)
                .create();
    }
}
