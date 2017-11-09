package com.neurofrank.portfolio;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.LocalDate;

import static com.neurofrank.portfolio.util.Constants.ARG_DATE;
import static com.neurofrank.portfolio.util.Constants.EXTRA_DATE;


public class DatePickerDialogFragment extends DialogFragment {

    private DatePicker mDatePicker;

    public static DatePickerDialogFragment newInstance(LocalDate date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDate date = (LocalDate) getArguments().getSerializable(ARG_DATE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date_picker, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker_date_picker);
        mDatePicker.init(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth(), null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocalDate localDate = new LocalDate(mDatePicker.getYear(), mDatePicker.getMonth() + 1, mDatePicker.getDayOfMonth());
                        sendResult(Activity.RESULT_OK, localDate);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setCancelable(true)
                .create();
    }

    public void sendResult(int resultCode, LocalDate date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
