package com.neurofrank.portfolio;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;

import com.neurofrank.portfolio.patient.PatientLab;
import com.neurofrank.portfolio.reception.ReceptionLab;
import com.neurofrank.portfolio.util.FileOperation;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static com.neurofrank.portfolio.util.Constants.ARG_DESCRIPTION;
import static com.neurofrank.portfolio.util.Constants.ARG_ID;
import static com.neurofrank.portfolio.util.Constants.ARG_IMAGE;
import static com.neurofrank.portfolio.util.Constants.ARG_TYPE;
import static com.neurofrank.portfolio.util.Constants.EXTRA_DESCRIPTION;
import static com.neurofrank.portfolio.util.Constants.EXTRA_IMAGE;
import static com.neurofrank.portfolio.util.Constants.EXTRA_PATIENT_ACTION;
import static com.neurofrank.portfolio.util.Constants.DELETED;
import static com.neurofrank.portfolio.util.Constants.IMAGE_DELETED;


public class DeleteDialogFragment extends DialogFragment {

    private ArrayList<DocumentFile> mImageToDelete;
    private String mDescription;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String type = (String) getArguments().getSerializable(ARG_TYPE);
        final UUID uuid;
        if (getArguments().getSerializable(ARG_ID) != null) {
            uuid = (UUID) getArguments().getSerializable(ARG_ID);
        } else {
            uuid = null;
        }
        if (getArguments().getSerializable(ARG_IMAGE) != null) {
            mImageToDelete = (ArrayList<DocumentFile>) getArguments().getSerializable(ARG_IMAGE);
        } else {
            mImageToDelete = null;
        }
        if (getArguments().getSerializable(ARG_DESCRIPTION) != null) {
            mDescription = (String) getArguments().getSerializable(ARG_DESCRIPTION);
        } else {
            mDescription = null;
        }

        switch (type) {
            case "patient":
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.patient_delete_dialog_title)
                        .setMessage(R.string.patient_delete_dialog_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PatientLab.get(getContext()).removePatient(uuid);
                                DocumentFile storageDir = FileOperation.GetStorageDirectory(getActivity());
                                DocumentFile patientDir = storageDir.findFile("Patient{" + uuid + "}");
                                FileOperation.DeleteRecursive(patientDir);
                                sendResult(Activity.RESULT_OK, DELETED);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setCancelable(true)
                        .create();
            case "reception":
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.reception_delete_dialog_title)
                        .setMessage(R.string.reception_delete_dialog_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DocumentFile storageDir = FileOperation.GetStorageDirectory(getActivity());
                                DocumentFile patientDir = storageDir.findFile("Patient{" + ReceptionLab.get(getContext()).getReception(uuid).getPatientId() + "}");
                                DocumentFile receptionDir = patientDir.findFile("Reception{" + uuid + "}");
                                FileOperation.DeleteRecursive(receptionDir);
                                ReceptionLab.get(getContext()).removeReception(uuid);
                                sendResult(Activity.RESULT_OK, DELETED);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setCancelable(true)
                        .create();
            default:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.image_delete_dialog_title)
                        .setMessage(R.string.image_delete_dialog_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileOperation.DeleteRecursive(mImageToDelete.get(0));
                                sendResult(Activity.RESULT_OK, IMAGE_DELETED);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setCancelable(true)
                        .create();
        }
    }

    /**
     * Creates delete dialog with confirm. You should specify type of operation
     * and required value.
     * @param type should be on of "patient", "reception" or "image". If it "patient"
     *             or "reception" then mImageToDelete should be null; if it "image"
     *             then uuid should be null
     * @param uuid patient or reception uuid
     * @param imageToDelete image file
     * @return new fragment
     */
    public static DeleteDialogFragment newInstance(String type, @Nullable UUID uuid, @Nullable DocumentFile imageToDelete, @Nullable String description) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, type);
        if (imageToDelete != null) {
            ArrayList<DocumentFile> mImageToDelete = new ArrayList<>();
            mImageToDelete.add(imageToDelete);
            args.putSerializable(ARG_IMAGE, mImageToDelete);
        } else {
            args.putSerializable(ARG_IMAGE, null);
        }
        args.putSerializable(ARG_ID, uuid);
        args.putSerializable(ARG_DESCRIPTION, description);

        DeleteDialogFragment fragment = new DeleteDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void sendResult(int resultCode, String action) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        if (Objects.equals(action, DELETED)) {
            intent.putExtra(EXTRA_PATIENT_ACTION, action);
        } else {
            intent.putExtra(EXTRA_IMAGE, mImageToDelete);
            intent.putExtra(EXTRA_DESCRIPTION, mDescription);
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
